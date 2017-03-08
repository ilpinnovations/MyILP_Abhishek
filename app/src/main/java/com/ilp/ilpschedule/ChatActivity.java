package com.ilp.ilpschedule;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.ilp.ilpschedule.adapter.ChatListAdapter;
import com.ilp.ilpschedule.model.ChatMessage;
import com.ilp.ilpschedule.model.ChatType;
import com.ilp.ilpschedule.model.Status;
import com.ilp.ilpschedule.util.AIResponseHandler;
import com.ilp.ilpschedule.util.AndroidUtilities;
import com.ilp.ilpschedule.util.Config;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.MessageEvent;
import com.ilp.ilpschedule.util.TTS;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.GsonFactory;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.ui.AIButton;

public class ChatActivity extends AppCompatActivity implements AIButton.AIButtonListener {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private static ChatActivity Instance;

    private ListView chatListView;
    private EditText chatEditText1;
    private ArrayList<ChatMessage> chatMessages;
    private ImageView enterChatView1;
    private ChatListAdapter listAdapter;

    private AIButton aiButton;
    private Gson gson = GsonFactory.getGson();


    //===============================================================================================================================
    //          Send button OnClick functions
    //===============================================================================================================================

    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if(v==chatEditText1)
                {
                    String query = editText.getText().toString();
                    sendApiAIRequest(query);
                    sendMessage(chatEditText1.getText().toString());
                }

                chatEditText1.setText("");

                return true;
            }
            return false;

        }
    };

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v==enterChatView1)
            {

                String query = chatEditText1.getText().toString();
                sendApiAIRequest(query);
                sendMessage(chatEditText1.getText().toString());
            }

            chatEditText1.setText("");

        }
    };

    private void sendApiAIRequest(String query){
        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(query);

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                     onResultProcessResponse(aiResponse, false);
                }
            }
        }.execute(aiRequest);
    }

    //===============================================================================================================================
    //          Custom Listeners
    //===============================================================================================================================

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {
                aiButton.setVisibility(View.VISIBLE);
                enterChatView1.setVisibility(View.GONE);
            } else {
                enterChatView1.setVisibility(View.VISIBLE);
                aiButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
//            if(editable.length()==0){
//                enterChatView1.setImageResource(R.drawable.ic_chat_send);
//            }else{
//                enterChatView1.setImageResource(R.drawable.ic_chat_send_active);
//            }
        }
    };


    //===============================================================================================================================
    //          Activity Callbacks
    //===============================================================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Instance = this;

        TTS.init(getApplicationContext());

        AndroidUtilities.statusBarHeight = getStatusBarHeight();

        chatMessages = new ArrayList<>();

        chatListView = (ListView) findViewById(R.id.chat_list_view);

        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);
        listAdapter = new ChatListAdapter(chatMessages, this);

        chatListView.setAdapter(listAdapter);

        chatEditText1.setOnKeyListener(keyListener);

        enterChatView1.setOnClickListener(clickListener);

        chatEditText1.addTextChangedListener(watcher1);

        aiButton = (AIButton) findViewById(R.id.mic_button);

        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        config.setRecognizerStartSound(getResources().openRawResourceFd(R.raw.test_start));
        config.setRecognizerStopSound(getResources().openRawResourceFd(R.raw.test_stop));
        config.setRecognizerCancelSound(getResources().openRawResourceFd(R.raw.test_cancel));

        aiButton.initialize(config);
        aiButton.setResultsListener(this);

    }

    public static ChatActivity getInstance()
    {
        return Instance;
    }

    private int sendMessage(final String messageText)
    {
        if(messageText.trim().length()==0)
            return -1;

        final ChatMessage message = new ChatMessage();
        message.setMessageStatus(Status.SENT);
        message.setMessageText(messageText);
        message.setUserType(ChatType.USER);
        message.setMessageTime(new Date().getTime());
        chatMessages.add(message);

        if(listAdapter!=null)
            listAdapter.notifyDataSetChanged();

        return chatMessages.size()-1;
    }

    private void respondMessage(final MessageEvent event, final int position) {
        chatMessages.get(position).setMessageStatus(Status.DELIVERED);

        final ChatMessage message = new ChatMessage();
        message.setMessageStatus(Status.SENT);
        message.setMessageText(event.getMessage());
        message.setUserType(event.getType());
        message.setMessageTime(new Date().getTime());
        message.setBean(event.getBean());
        message.setCseBean(event.getSearchData());
        chatMessages.add(message);

        if (listAdapter != null){
            listAdapter.notifyDataSetChanged();
        }


    }

    private Activity getActivity()
    {
        return this;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        checkAudioRecordPermission();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // use this method to reinit connection to recognition service
        aiButton.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        hideEmojiPopup();
        // use this method to disconnect from speech recognition service
        // Not destroying the SpeechRecognition object in onPause method would block other apps from using SpeechRecognition service
        aiButton.pause();
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //===============================================================================================================================
    //          API.AI Button Listener Callbacks
    //===============================================================================================================================

    @Override
    public void onResult(AIResponse response) {
        onResultProcessResponse(response, true);
    }

    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onError");
//                resultTextView.setText(error.toString());
                sendMessage(error.toString());
            }
        });
    }

    @Override
    public void onCancelled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onCancelled");
//                resultTextView.setText("");
            }
        });
    }

    //===============================================================================================================================
    //          AI Responce Processing
    //===============================================================================================================================

    private void onResultProcessResponse(final AIResponse response, final boolean FLAG_SPEECH){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onResult");

//                resultTextView.setText(gson.toJson(response));
                Log.i(TAG, "RESPONSE:\n" + gson.toJson(response));

                final Result result = response.getResult();
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());

                final String speech = result.getFulfillment().getSpeech();
                Log.i(TAG, "Speech: " + speech);

                String action = result.getAction();
                Log.i(TAG, action);

                if (FLAG_SPEECH){
                    int position = sendMessage(result.getResolvedQuery());
                    if (!speech.equalsIgnoreCase("") && !action.equalsIgnoreCase(Constants.ACTION_TYPES.INPUT_UNKNOWN)){
                        MessageEvent event = new MessageEvent(speech, ChatType.BOT_DEFAULT);
                        respondMessage(event, position);
                        TTS.speak(speech);
                    }
                }else {
                    if (!speech.equalsIgnoreCase("") && !action.equalsIgnoreCase(Constants.ACTION_TYPES.INPUT_UNKNOWN)){
                        MessageEvent event = new MessageEvent(speech, ChatType.BOT_DEFAULT);
                        respondMessage(event, chatMessages.size() - 1);
                    }
                }


                AIResponseHandler handler = new AIResponseHandler(getApplicationContext());
                handler.processIntent(response);
            }

        });
    }

    //===============================================================================================================================
    //          EventBus custom events
    //===============================================================================================================================
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        respondMessage(event, chatMessages.size()-1);
        TTS.speak(event.getMessage());
    }

    /**
     * Get the system status bar height
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chatEditText1.getWindowToken(), 0);
    }
}
