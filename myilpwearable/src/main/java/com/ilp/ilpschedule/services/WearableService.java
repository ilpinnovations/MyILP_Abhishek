package com.ilp.ilpschedule.services;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WearableService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;

    private static final String TAG_INTENT_EXTRA = "INTENT";

    private static final String TAG = "PhoneService";

    private static final String SCHEDULE = "/schedule";
    private static final String CONTACTS = "/contacts";
    private static final String ERROR = "/error";

    private static final String MSG_INTERNET_NOT_AVAILABLE_SCHEDULE = "INTERNET_NOT_AVAILABLE_SCHEDULE";
    private static final String MSG_INTERNET_NOT_AVAILABLE_CONTACTS = "INTERNET_NOT_AVAILABLE_CONTACTS";
    private static final String MSG_LOGIN_ERROR_SCHEDULE = "LOGIN_ERROR_SCHEDULE";
    private static final String MSG_LOGIN_ERROR_CONTACTS = "LOGIN_ERROR_CONTACTS";
    private static final String MSG_NO_SCHEDULE = "NO_SCHEDULE";

    private String nodeId;
    private static final long CONNECTION_TIME_OUT_MS = 100;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Created");

        if (null == mGoogleApiClient) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            Log.v(TAG, "GoogleApiClient created");
        }

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            Log.v(TAG, "Connecting to GoogleApiClient..");
        }
    }

    @Override
    public void onDestroy() {

        Log.v(TAG, "Destroyed");

        if (null != mGoogleApiClient) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
                Log.v(TAG, "GoogleApiClient disconnected");
            }
        }

        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.v(TAG, "onConnectionSuspended called");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "onConnectionFailed called");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v(TAG, "onConnected called");

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        Log.v(TAG, "Data Changed");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(SCHEDULE)) {
            nodeId = messageEvent.getSourceNodeId();
            final String message = new String(messageEvent.getData());
            Log.v(TAG, "Message path received on watch is: " + messageEvent.getPath());
            Log.v(TAG, "Message received on watch is: " + message);
            String reply = "Hello reply from handheld";

            Intent messageIntent = new Intent();
            messageIntent.setAction("com.ilp.ilpschedule.INTENT_SCHEDULE");
            messageIntent.putExtra(TAG_INTENT_EXTRA, message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

            //new SendToDataLayerThread(MOBILE_PATH, reply).start();
        }else if (messageEvent.getPath().equals(CONTACTS)){
            //ToDo
            //Implement broadcast reciever for contacts
            nodeId = messageEvent.getSourceNodeId();
            final String message = new String(messageEvent.getData());
            Log.v(TAG, "Message path received on watch is: " + messageEvent.getPath());
            Log.v(TAG, "Message received on watch is: " + message);
            String reply = "Hello reply from handheld";

            Intent messageIntent = new Intent();
            messageIntent.setAction("com.ilp.ilpschedule.INTENT_CONTACTS");
            messageIntent.putExtra(TAG_INTENT_EXTRA, message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }else if (messageEvent.getPath().equals(ERROR)){
            //error event
            nodeId = messageEvent.getSourceNodeId();
            final String message = new String(messageEvent.getData());
            Log.v(TAG, "Message path received on watch is: " + messageEvent.getPath());
            Log.v(TAG, "Message received on watch is: " + message);
            String reply = "Hello reply from handheld";

            Intent messageIntent = new Intent();

            switch (message){
                case MSG_INTERNET_NOT_AVAILABLE_CONTACTS:
                    //internet not available for contacts
                    Log.i(TAG, "NETWORK ERROR CONTACTS: " + message);

                    messageIntent.setAction("com.ilp.ilpschedule.INTENT_CONTACTS");
                    messageIntent.putExtra(TAG_INTENT_EXTRA, MSG_INTERNET_NOT_AVAILABLE_CONTACTS);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                    break;

                case MSG_INTERNET_NOT_AVAILABLE_SCHEDULE:
                    //internet not available for schedule
                    Log.i(TAG, "NETWORK ERROR SCHEDULE: " + message);

                    messageIntent.setAction("com.ilp.ilpschedule.INTENT_SCHEDULE");
                    messageIntent.putExtra(TAG_INTENT_EXTRA, MSG_INTERNET_NOT_AVAILABLE_SCHEDULE);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                    break;
                case MSG_LOGIN_ERROR_SCHEDULE:
                    //login error for schedule
                    Log.i(TAG, "LOGIN ERROR SCHEDULE: " + message);

                    messageIntent.setAction("com.ilp.ilpschedule.INTENT_SCHEDULE");
                    messageIntent.putExtra(TAG_INTENT_EXTRA, MSG_LOGIN_ERROR_SCHEDULE);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                    break;

                case MSG_LOGIN_ERROR_CONTACTS:
                    //login error for contacts
                    Log.i(TAG, "LOGIN ERROR CONTACTS: " + message);

                    messageIntent.setAction("com.ilp.ilpschedule.INTENT_CONTACTS");
                    messageIntent.putExtra(TAG_INTENT_EXTRA, MSG_LOGIN_ERROR_CONTACTS);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                    break;

                case MSG_NO_SCHEDULE:
                    //Todo
                    //no schedule present
                    break;
                default:
                    Log.e(TAG, "message parameter not defined | " + message);
                    super.onMessageReceived(messageEvent);
                    break;
            }
        } else {
            Log.e(TAG, "message event not defined | " + messageEvent.getPath());
            super.onMessageReceived(messageEvent);
        }
    }


    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.v(TAG, "Peer Connected " + peer.getDisplayName());
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.v(TAG, "Peer Disconnected " + peer.getDisplayName());
    }

    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }

}