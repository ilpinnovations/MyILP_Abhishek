package com.ilp.ilpschedule.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.model.LoginResult;
import com.ilp.ilpschedule.service.RetrofitService;
import com.ilp.ilpschedule.service.ServiceGenerator;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // UI references.
    private AutoCompleteTextView empIdView, lgView, emailView;
    private Button loginBtn;

    private RequestQueue mRequestQueue;
    private ProgressDialog pDialog;

    private View.OnClickListener loginBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (Util.hasInternetAccess(getApplicationContext())){
                boolean cancel = false;
                View focusView = null;

                long empId = -999;
                String lg = lgView.getText().toString().trim().replace(" ", "");
                String email = emailView.getText().toString().trim();

                if (lg.equalsIgnoreCase("")){
                    Log.i(TAG, "location empty");
                    lgView.setError("Please enter LG name");
                    focusView = lgView;
                    cancel = true;
                }

                if (email.equalsIgnoreCase("")){
                    Log.i(TAG, "location empty");
                    emailView.setError("Please enter Email ID");
                    focusView = emailView;
                    cancel = true;
                }

                if (!empIdView.getText().toString().equalsIgnoreCase("")) {
                    try {
                        empId = Long.parseLong(empIdView.getText().toString().trim());
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        cancel = true;
                        Log.i(TAG, "invalid long");
                        empIdView.setText("Invalid Employee ID");
                        focusView = emailView;
                    }
                }else {
                    Log.i(TAG, "location empty");
                    empIdView.setError("Please enter Employee ID");
                    focusView = empIdView;
                    cancel = true;
                }

                if (cancel){
                    Log.i(TAG, "in cancel");
                    focusView.requestFocus();
                }else{
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Constants.USER_TABLE_ATTRIB.EMP_ID, String.valueOf(empId));
                    params.put(Constants.USER_TABLE_ATTRIB.EMAIL, email);
                    params.put(Constants.USER_TABLE_ATTRIB.BATCH, lg);

                    login(params, (int) empId, lg, email);
                }
            }else {
                Util.toast(getApplicationContext(),
                        getString(R.string.toast_no_internet));
            }

        }
    };

    private void login(final Map<String, String> params, int empId, String lg, String email){
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        pDialog.setMessage("Please wait..");
        pDialog.show();

        String url = Constants.URL_LOGIN +
                "?emp_id=" + empId +
                "&emp_email=" + email +
                "&emp_batch=" + lg;

        Log.i(TAG, "Url: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.hide();
                        Log.i(TAG, "in onResponse");
                        Log.i(TAG, response.toString());
                        try {
                            String result = response.getString("status");
                            if (result.equalsIgnoreCase("success")){
                                Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Login successful!");

                                int empId = response.getInt(Constants.USER_TABLE_ATTRIB.EMP_ID);
                                String email = response.getString(Constants.USER_TABLE_ATTRIB.EMAIL);
                                String name = response.getString(Constants.USER_TABLE_ATTRIB.NAME);
                                String location = response.getString(Constants.USER_TABLE_ATTRIB.LOCATION);
                                String batch = response.getString(Constants.USER_TABLE_ATTRIB.BATCH);

                                Employee emp = new Employee(empId, name, email, location);
                                emp.setLg(batch);

                                int errorId = Util.saveEmployee(getApplicationContext(), emp);

                                if (errorId == Constants.EMP_ERRORS.NO_ERROR){
                                    Util.doLogin(getApplicationContext());
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Util.toast(getApplicationContext(), Util.getErrorMsg(errorId));
                                }
                            }else {
                                Toast.makeText(getApplicationContext(), "Invalid login details! Check again.", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Login failed!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.hideProgressDialog();
                Log.i(TAG, "in onErrorResponse");
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error Response!", Toast.LENGTH_SHORT).show();
            }
        });

        mRequestQueue.add(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pDialog == null){
            pDialog = new ProgressDialog(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pDialog != null){
            pDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        pDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        VolleyLog.DEBUG = true;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        empIdView = (AutoCompleteTextView) findViewById(R.id.emp_id);
        lgView = (AutoCompleteTextView) findViewById(R.id.lg);
        emailView = (AutoCompleteTextView) findViewById(R.id.email);
        loginBtn = (Button) findViewById(R.id.btn_sign_in);

        loginBtn.setOnClickListener(loginBtnListener);
    }
}
