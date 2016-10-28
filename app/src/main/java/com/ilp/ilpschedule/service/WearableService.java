package com.ilp.ilpschedule.service;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.adapter.ContactsAdapter;
import com.ilp.ilpschedule.db.DbHelper;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.model.Slot;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.HttpManager;
import com.ilp.ilpschedule.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WearableService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
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


            if (Util.checkLogin(getApplicationContext())){
                if (Util.hasInternetAccess(getApplicationContext())){
                    Date date = new Date();
                    Log.i(TAG,"Current date: " + date);

                    Employee emp = Util.getEmployee(getApplicationContext());

                    if (emp != null){
                        String lg = emp.getLg();

                        Map<String, String> params = new HashMap<>();
                        params.put(Constants.NETWORK_PARAMS.SCHEDULE.BATCH, lg);
                        params.put(Constants.NETWORK_PARAMS.SCHEDULE.DATE,
                                Constants.paramsDateFormat.format(date));
                        String url = new StringBuilder(
                                Constants.NETWORK_PARAMS.SCHEDULE.URL).append(
                                Util.getUrlEncodedString(params)).toString();

                        Log.i(TAG, "Schedule URL: " + url);

                        HttpManager httpManager = new HttpManager(getApplicationContext(), new HttpManager.ServiceResponse() {
                            @Override
                            public void onServiceResponse(String serviceResponse) {
                                if (serviceResponse != null && HttpManager.getStatusCode() == 200){
                                    new SendToDataLayerThread(SCHEDULE, serviceResponse).start();
                                }else {
                                    new SendToDataLayerThread(ERROR, MSG_INTERNET_NOT_AVAILABLE_SCHEDULE).start();
                                }
                            }
                        });

                        httpManager.execute(url);
                    }else {
                        // No employee database
                        // similar to user login failure
                        Log.i(TAG, "Employee database failure!");
                        new SendToDataLayerThread(ERROR, MSG_LOGIN_ERROR_SCHEDULE).start();
                    }
                }else {
                    // No internet access
                    Log.e(TAG, "SCHEDULE: No internet access!");
                    new SendToDataLayerThread(ERROR, MSG_INTERNET_NOT_AVAILABLE_SCHEDULE).start();
//                    Util.toast(getApplicationContext(), getString(R.string.toast_no_internet));
                }
            }else {
                // user not logged in
                Log.e(TAG, "SCHEDULE: User not logged in!");
                new SendToDataLayerThread(ERROR, MSG_LOGIN_ERROR_SCHEDULE).start();
            }


        }else if (messageEvent.getPath().equals(CONTACTS)){

            if (Util.checkLogin(getApplicationContext())){
                if (Util.hasInternetAccess(getApplicationContext())) {
                    Map<String, String> params = new HashMap<>();
                    params.put(Constants.NETWORK_PARAMS.CONTACT.ILP,
                            Util.getEmployee(getApplicationContext()).getLocation());
                    String url = Constants.URL_CONTACTS + Util.getUrlEncodedString(params);
                    new StringBuilder(
                            Constants.URL_CONTACTS).append(
                            Util.getUrlEncodedString(params)).toString();

                    Log.i(TAG, "Schedule URL: " + url);

                    HttpManager httpManager = new HttpManager(getApplicationContext(), new HttpManager.ServiceResponse() {
                        @Override
                        public void onServiceResponse(String serviceResponse) {
                            if (serviceResponse != null && HttpManager.getStatusCode() == 200){
                                new SendToDataLayerThread(CONTACTS, serviceResponse).start();
                            }else {
                                new SendToDataLayerThread(ERROR, MSG_INTERNET_NOT_AVAILABLE_CONTACTS).start();
                            }
                        }
                    });

                    httpManager.execute(url);
                } else {
                    // no internet access
                    Log.e(TAG, "CONTACTS: No internet access!");
                    new SendToDataLayerThread(ERROR, MSG_INTERNET_NOT_AVAILABLE_CONTACTS).start();
                }
            }else {
                // user not logged in
                Log.e(TAG, "CONTACTS: User not logged in!");
                new SendToDataLayerThread(ERROR, MSG_LOGIN_ERROR_CONTACTS).start();
            }


        }
        else {
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