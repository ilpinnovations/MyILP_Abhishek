package com.ilp.ilpschedule.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.ilp.ilpschedule.util.Util;

import java.io.IOException;


public class GcmIDListenerService extends InstanceIDListenerService {
    String regId;

    @Override
    public void onTokenRefresh() {
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        try {
            regId = instanceID.getToken(String.valueOf(Util.GOOGLE_PROJECT_ID), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        storeRegIdInServer();
    }

    private void storeRegIdInServer() {
        /*AsyncHttpClient client = new AsyncHttpClient();
        client.post(AppConstant.GCM_SERVER_URL, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        String vehicle = responseString.substring(1);
                        Log.d("response", "Failure : " + vehicle);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {

                        String vehicle = responseString.substring(1);
                        Log.d("response", "Success :" + vehicle);
                    }
                });*/
    }
}