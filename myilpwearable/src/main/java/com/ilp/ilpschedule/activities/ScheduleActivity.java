package com.ilp.ilpschedule.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.adapters.ScheduleAdapter;
import com.ilp.ilpschedule.beans.SlotBean;
import com.ilp.ilpschedule.utilities.DividerItemDecorator;
import com.ilp.ilpschedule.utilities.Utils;

import java.util.ArrayList;

public class ScheduleActivity extends Activity implements WearableListView.ClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String nodeId;
    private static final String TAG = "SCHEDULE ACTIVITY";
    private static ArrayList<SlotBean> schedules = new ArrayList<>();
    WearableListView wearableListView;
    private ScheduleAdapter mScheduleAdapter;
    private TextView mHeader;
    private TextView mNetworkError;
    private ProgressBar mProgressBar;

    private static final String SCHEDULE = "/schedule";
    private static final String MSG_INTERNET_NOT_AVAILABLE_SCHEDULE = "INTERNET_NOT_AVAILABLE_SCHEDULE";
    private static final String MSG_LOGIN_ERROR_SCHEDULE = "LOGIN_ERROR_SCHEDULE";
    private static final String MSG_NO_SCHEDULE = "NO_SCHEDULE";

    private static final String TAG_EXTRA_INTENT = "INTENT";

    private GoogleApiClient googleClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
//        populateSchedules();

        mNetworkError = (TextView) findViewById(R.id.network_error);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mHeader = (TextView) findViewById(R.id.header);
        // mHeader.setText("TMS AO 208");

        wearableListView = (WearableListView) findViewById(R.id.wearable_List);
        mScheduleAdapter = new ScheduleAdapter(this, schedules);
        wearableListView.setAdapter(mScheduleAdapter);
        wearableListView.setClickListener(mClickListener);
        wearableListView.addOnScrollListener(mOnScrollListener);
        wearableListView.addItemDecoration(new DividerItemDecorator(ScheduleActivity.this, LinearLayoutManager.VERTICAL));

        //Set visibility
        mHeader.setVisibility(View.GONE);
        mNetworkError.setVisibility(View.GONE);
        wearableListView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter("com.ilp.ilpschedule.INTENT_SCHEDULE");
        ScheduleReceiver messageReceiver = new ScheduleReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }


    // Handle our Wearable List's click events
    private WearableListView.ClickListener mClickListener =
            new WearableListView.ClickListener() {
                @Override
                public void onClick(WearableListView.ViewHolder viewHolder) {
                    /*Toast.makeText(ScheduleActivity.this,
                            String.format("You selected item #%s",
                                    viewHolder.getLayoutPosition() + 1),
                            Toast.LENGTH_SHORT).show();*/
                }

                @Override
                public void onTopEmptyRegionClick() {
                    /*Toast.makeText(ScheduleActivity.this,
                            "Top empty area tapped", Toast.LENGTH_SHORT).show();*/
                }
            };

    // The following code ensures that the title scrolls as the user scrolls up
    // or down the list
    private WearableListView.OnScrollListener mOnScrollListener =
            new WearableListView.OnScrollListener() {
                @Override
                public void onAbsoluteScrollChange(int i) {
                    // Only scroll the title up from its original base position
                    // and not down.
                    if (i > 0) {
                        mHeader.setY(-i);
                    }
                }

                @Override
                public void onScroll(int i) {
                    // Placeholder
                }

                @Override
                public void onScrollStateChanged(int i) {
                    // Placeholder
                }

                @Override
                public void onCentralPositionChanged(int i) {
                    // Placeholder
                }
            };

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

    }

    @Override
    public void onTopEmptyRegionClick() {

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Wearable methods Message Events


    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }



    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        String message = "Hello wearable\n Via the data layer";
        new SendToDataLayerThread(SCHEDULE, message).start();
    }

    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient,
                        node.getId(),
                        path,
                        message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }

    public class ScheduleReceiver extends BroadcastReceiver {

        public void updateAdapter(String jsonData) {
            ArrayList<SlotBean> data = Utils.scheduleParser(jsonData);
            if (data != null && data.size() != 0) {
                String batch = data.get(0).getBatch();

                //Set visibility accordingly
                mProgressBar.setVisibility(View.GONE);
                mNetworkError.setVisibility(View.GONE);
                wearableListView.setVisibility(View.VISIBLE);
                mHeader.setVisibility(View.VISIBLE);

                //Set batch name in the header
                mHeader.setText(batch);
                schedules.clear();
                for (SlotBean s : data) {
                    schedules.add(s);
                }

                //notify data changed to the adapter
                mScheduleAdapter.notifyDataSetChanged();
            } else if (data == null) {
                // No Schedule
//                Log.e(TAG, "Parser returned null dataset!");
//
//                mProgressBar.setVisibility(View.GONE);
//                wearableListView.setVisibility(View.GONE);
//                mHeader.setVisibility(View.GONE);
//                mNetworkError.setVisibility(View.VISIBLE);
////                mNetworkError.setText("No Internet Connection");
//
//                Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT);
//                toast.show();

                handleNoSchedule();
            } else if (data.size() == 0) {
                // No schedule
                Log.e(TAG, "size is 0!");

                mProgressBar.setVisibility(View.GONE);
                wearableListView.setVisibility(View.GONE);
                mHeader.setVisibility(View.GONE);
                mNetworkError.setVisibility(View.VISIBLE);

                mNetworkError.setText("No Schedule");
            }
        }

        public void handleInternetNotAvailable(){
            // Network Error
            Log.e(TAG, "Parser returned null dataset!");

            mProgressBar.setVisibility(View.GONE);
            wearableListView.setVisibility(View.GONE);
            mHeader.setVisibility(View.GONE);
            mNetworkError.setVisibility(View.VISIBLE);
            mNetworkError.setText("No Internet Connection");

//            Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT);
//            toast.show();
        }

        public void handleLoginError(){
            //Login Error
            Log.e(TAG, "User not logged in!");

            mProgressBar.setVisibility(View.GONE);
            wearableListView.setVisibility(View.GONE);
            mHeader.setVisibility(View.GONE);
            mNetworkError.setVisibility(View.VISIBLE);
            mNetworkError.setText("Please sign-in from the application first");

//            Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT);
//            toast.show();
        }

        public void handleNoSchedule(){
            // No schedule
            Log.e(TAG, "size is 0!");

            mProgressBar.setVisibility(View.GONE);
            wearableListView.setVisibility(View.GONE);
            mHeader.setVisibility(View.GONE);
            mNetworkError.setVisibility(View.VISIBLE);

            mNetworkError.setText("No Schedule");
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String intentData = intent.getStringExtra(TAG_EXTRA_INTENT);
            Log.i(TAG, "ïntentData: " + intentData );
            switch (intentData){
                case MSG_INTERNET_NOT_AVAILABLE_SCHEDULE:
                    handleInternetNotAvailable();
                    break;

                case MSG_LOGIN_ERROR_SCHEDULE:
                    handleLoginError();
                    break;

                case MSG_NO_SCHEDULE:
                    //ToDo
                    //No schedule
                    handleNoSchedule();
                    break;

                default:
                    Log.i(TAG, "JSON DATA");
                    updateAdapter(intentData);
            }

        }
    }

}
