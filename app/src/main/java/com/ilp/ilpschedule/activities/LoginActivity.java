package com.ilp.ilpschedule.activities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private Spinner locationSpinner;
    private EditText editTextName, editTextEmail, editTextEmpId, editTextLg;

    InstanceID instanceID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.location_item, getResources().getStringArray(
                R.array.locations));

        editTextName = (EditText) findViewById(R.id.editTextLoginName);
        editTextEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        editTextEmpId = (EditText) findViewById(R.id.editTextLoginEmployeeId);

        editTextLg = (EditText) findViewById(R.id.editTextLoginLg);

        locationSpinner = ((Spinner) findViewById(R.id.spinnerLoginLocation));
        locationSpinner.setAdapter(locationAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.checkLogin(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }

    public void login(View view) {

        if (Util.hasInternetAccess(getApplicationContext())) {
            Employee emp = new Employee();

            long empId = Long.parseLong(editTextEmpId.getText().toString().trim());

            emp.setEmpId(empId);

            emp.setName(editTextName.getText().toString().trim()
                    .toUpperCase(Locale.US));
            emp.setLg(editTextLg.getText().toString().trim()
                    .toUpperCase(Locale.US));
            emp.setEmail(editTextEmail.getText().toString().trim());
            emp.setLocation((String) locationSpinner.getSelectedItem());

            int errorId = Util.saveEmployee(getApplicationContext(), emp);
            if (errorId == Constants.EMP_ERRORS.NO_ERROR) {
                Util.showProgressDialog(this);

                String imei = "";
                Map<String, String> params = new HashMap<>();
                params.put(Constants.NETWORK_PARAMS.REGISTER.NAME,
                        emp.getName());
                params.put(Constants.NETWORK_PARAMS.REGISTER.EMIAL,
                        emp.getEmail());
                params.put(Constants.NETWORK_PARAMS.REGISTER.EMP_ID,
                        String.valueOf(emp.getEmpId()));
                params.put(Constants.NETWORK_PARAMS.REGISTER.LOCATION,
                        emp.getLocation());
                params.put(Constants.NETWORK_PARAMS.REGISTER.BATCH, emp.getLg());
                params.put(Constants.NETWORK_PARAMS.REGISTER.IMEI, imei);
                RegisterAsyncTask rat = new RegisterAsyncTask(params);
                rat.execute();
            } else {
                Util.toast(getApplicationContext(), Util.getErrorMsg(errorId));
            }
        } else {
            Util.toast(getApplicationContext(),
                    getString(R.string.toast_no_internet));
        }
    }

    private class RegisterAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
        private Map<String, String> parameters;
        private String regId;

        public RegisterAsyncTask(Map<String, String> parameters) {
            this.parameters = parameters;
        }

        private void post(String endpoint) throws IOException {
            Log.d(TAG, "post function");
            URL url;
            try {
                url = new URL(endpoint);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("invalid url: " + endpoint);
            }
            String body = Util.getUrlEncodedString(parameters);
            Log.d(TAG, "Posting '" + body + "' to " + url);

            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                Log.d("URL", "> " + url);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");
                // post the request
                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                // handle the response
                int status = conn.getResponseCode();
                // If response is not success
                if (status != 200) {
                    throw new IOException("Post failed with error code "
                            + status);
                }
            } finally {
                if (conn != null) {
                    Log.i(TAG, "Finally");
                    conn.disconnect();
                }
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean res = false;
            int MAX_ATTEMPTS = 4;
            int BACKOFF_MILLI_SECONDS = 1000;
            try {
                instanceID = InstanceID.getInstance(getApplicationContext());
                regId = instanceID.getToken(String.valueOf(Util.GOOGLE_PROJECT_ID), GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                Log.d("GCM Key", regId);
                Util.saveRegId(getApplicationContext(), regId);

                if (regId != null && regId.trim().length() > 0) {
                    publishProgress(true);
                    // register with app server ilp server
                    parameters.put("regId", regId);
                    long backoff = BACKOFF_MILLI_SECONDS + (int) Math.random()
                            * 1000;
                    for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                        Log.d(TAG, "Attempt #" + i + " to register");
                        try {
                            post(Constants.URL_REGISTER);
                            res = true;
                            Log.d(TAG, "success to register on attempt " + i);
                            break;
                        } catch (IOException e) {
                            Log.d(TAG, "Failed to register on attempt " + i
                                    + ":" + e);
                            if (i == MAX_ATTEMPTS) {
                                break;
                            }
                            try {
                                Log.d(TAG, "Sleeping for " + backoff
                                        + " ms before retry");
                                Thread.sleep(backoff);
                            } catch (InterruptedException e1) {
                                // AndArActivity finished before we complete - exit.
                                Log.d(TAG,
                                        "Thread interrupted: abort remaining retries!");
                                Thread.currentThread().interrupt();
                                return res;
                            }
                            backoff *= 2;
                        }
                    }
                }
            } catch (IOException e) {
//                Log.d(TAG, "fail to register with gcm " + e.getLocalizedMessage());
                Log.d(TAG,"IOEXCEPTION");
                res = false;
            }
            return res;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (values[0]) {
                Util.doLogin(getApplicationContext());
            } else {
                Log.d(TAG, "fail to register with google try again");
                Util.toast(getApplicationContext(),
                        getString(R.string.toast_reg_error));
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                Util.toast(getApplicationContext(),
                        getString(R.string.toast_reg_error));
            }
            Util.hideProgressDialog();
        }
    }

}
