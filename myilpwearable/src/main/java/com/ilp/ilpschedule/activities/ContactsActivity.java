package com.ilp.ilpschedule.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
import com.ilp.ilpschedule.adapters.ContactsAdapter;
import com.ilp.ilpschedule.beans.ContactsBean;
import com.ilp.ilpschedule.beans.SlotBean;
import com.ilp.ilpschedule.utilities.Utils;

import java.util.ArrayList;

public class ContactsActivity extends Activity implements WearableListView.ClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String nodeId;
    private ArrayList<ContactsBean> contacts = new ArrayList<>();
    private TextView mHeader;
    private TextView mNetworkError;
    private ProgressBar mProgressBar;
    WearableListView wearableListView;
    private ContactsAdapter mContactsAdapter;

    private static final String TAG_EXTRA_INTENT = "INTENT";
    private static final String TAG = ContactsActivity.class.getSimpleName();

    private static final String CONTACTS = "/contacts";
    private static final String MSG_INTERNET_NOT_AVAILABLE_CONTACTS = "INTERNET_NOT_AVAILABLE_CONTACTS";
    private static final String MSG_LOGIN_ERROR_CONTACTS = "LOGIN_ERROR_CONTACTS";

    private final static String TAG_CONTACT_NAME = "contactName";
    private final static String TAG_CONTACT_NUMBER = "contactNumber";

    private GoogleApiClient googleClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        mHeader = (TextView) findViewById(R.id.header);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        wearableListView = (WearableListView) findViewById(R.id.wearable_List);
        mNetworkError = (TextView) findViewById(R.id.network_error);

        mHeader.setText("Contacts");

        mContactsAdapter = new ContactsAdapter(this, contacts);
        wearableListView.setAdapter(mContactsAdapter);
        wearableListView.setClickListener(mClickListener);
        wearableListView.addOnScrollListener(mOnScrollListener);

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
        IntentFilter messageFilter = new IntentFilter("com.ilp.ilpschedule.INTENT_CONTACTS");
        ContactsReceiver messageReceiver = new ContactsReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }


    // Handle our Wearable List's click events
    private WearableListView.ClickListener mClickListener =
            new WearableListView.ClickListener() {
                @Override
                public void onClick(WearableListView.ViewHolder viewHolder) {
                    /*Toast.makeText(ContactsActivity.this,
                            String.format("You selected item #%s",
                                    viewHolder.getLayoutPosition() + 1),
                            Toast.LENGTH_SHORT).show();*/

                    Intent intent = new Intent(ContactsActivity.this, ContactDetailsActivity.class);
                    intent.putExtra(TAG_CONTACT_NAME, contacts.get(viewHolder.getLayoutPosition()).getContactName());
                    intent.putExtra(TAG_CONTACT_NUMBER, contacts.get(viewHolder.getLayoutPosition()).getContactNumber());
                    startActivity(intent);
                }

                @Override
                public void onTopEmptyRegionClick() {
                   /* Toast.makeText(ContactsActivity.this,
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
    public void onConnected(Bundle connectionHint) {
        String message = "Hello wearable\n Via the data layer";

        new SendToDataLayerThread(CONTACTS, message).start();
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
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }

    public class ContactsReceiver extends BroadcastReceiver {

        public void updateAdapter(String jsonData) {
            ArrayList<ContactsBean> data = Utils.contactsParser(jsonData);
            if (data != null && data.size() != 0) {

                //Set visibility accordingly
                mProgressBar.setVisibility(View.GONE);
                mNetworkError.setVisibility(View.GONE);
                wearableListView.setVisibility(View.VISIBLE);
                mHeader.setVisibility(View.VISIBLE);

                contacts.clear();
                for (ContactsBean s : data) {
                    contacts.add(s);
                }

                //notify data changed to the adapter
                mContactsAdapter.notifyDataSetChanged();
            } else if (data == null) {
                // Network Error
                Log.e(TAG, "Parser returned null dataset!");

                mProgressBar.setVisibility(View.GONE);
                wearableListView.setVisibility(View.GONE);
                mHeader.setVisibility(View.GONE);
                mNetworkError.setVisibility(View.GONE);
//                mNetworkError.setText("No Internet Connection");

                Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG);
                toast.show();
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

        public void handleInternetNotAvailable() {
            // Network Error
            Log.e(TAG, "Parser returned null dataset!");

            mProgressBar.setVisibility(View.GONE);
            wearableListView.setVisibility(View.GONE);
            mHeader.setVisibility(View.GONE);
            mNetworkError.setVisibility(View.VISIBLE);
            mNetworkError.setText("No Internet Connection");

//            Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG);
//            toast.show();
        }

        public void handleLoginError() {
            //Login Error
            Log.e(TAG, "User not logged in!");

            mProgressBar.setVisibility(View.GONE);
            wearableListView.setVisibility(View.GONE);
            mHeader.setVisibility(View.GONE);
            mNetworkError.setVisibility(View.VISIBLE);
            mNetworkError.setText("Please sign-in from the application first");

//            Toast toast = Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG);
//            toast.show();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String intentData = intent.getStringExtra(TAG_EXTRA_INTENT);

            switch (intentData) {
                case MSG_INTERNET_NOT_AVAILABLE_CONTACTS:
                    handleInternetNotAvailable();
                    break;

                case MSG_LOGIN_ERROR_CONTACTS:
                    handleLoginError();
                    break;

                default:
                    Log.i(TAG, "JSON DATA");
                    updateAdapter(intentData);
            }
        }
    }
}