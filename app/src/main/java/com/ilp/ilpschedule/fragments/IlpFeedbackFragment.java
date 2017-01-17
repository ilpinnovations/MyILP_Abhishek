package com.ilp.ilpschedule.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.vision.text.Text;
import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.activities.MainActivity;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class IlpFeedbackFragment extends Fragment {

    private static final String TAG = IlpFeedbackFragment.class.getSimpleName();

    private Spinner spinner;
    private EditText serviceProviderEt, commentEt;
    private TextView feedbackHeading, charCount;
    private RatingBar ratingBar;
    private ImageButton submitButton;

    private static final String FEEDBACK_URL = "http://theinspirer.in/ilpscheduleapp/post_feedback.php";

    private final String KEY_EMP_ID = "emp_id";
    private final String KEY_EMP_NAME = "emp_name";
    private final String KEY_EMP_LOC = "emp_loc";
    private final String KEY_EMP_BATCH = "emp_batch";
    private final String KEY_FEEDBACK_TYPE = "feedback_type";
    private final String KEY_SERVICE_PROVIDER = "service_provider";
    private final String KEY_RATING = "rating";
    private final String KEY_COMMENT = "comment";
    private final String KEY_DATE = "date";
    private final String KEY_RESULT = "result";

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            // On selecting a spinner item
            String item = adapterView.getItemAtPosition(i).toString();

            resetForm();

            feedbackHeading.setText(item);

//            String[] feedbackType = getActivity().getResources().getStringArray(R.array.feedback_array);
//            if (item.equalsIgnoreCase(feedbackType[0])){
//                feedbackTypeTv.setText("Building");
//            }else if (item.equalsIgnoreCase(feedbackType[1])){
//
//            }else if (item.equalsIgnoreCase(feedbackType[2])){
//
//            }else {
//                Log.i(TAG, "Null");
//            }
            // Showing selected spinner item
//            Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String rating = String.valueOf(ratingBar.getRating());
            final String serviceProvider = serviceProviderEt.getText().toString();
            final String comment = commentEt.getText().toString();
            final String emp_id, emp_name, location, batch, feedback_type, date;

            feedback_type = String.valueOf(spinner.getSelectedItemPosition()+1);
            if (comment.length() < 30){
                Toast.makeText(getActivity(), "Comment should be of atleast 30 characters!", Toast.LENGTH_SHORT).show();
            }else if (serviceProvider.length() == 0){
                Toast.makeText(getActivity(), "Provider's name can not be left empty!", Toast.LENGTH_SHORT).show();
            }else {
                Employee employee = Util.getEmployee(getActivity());
                if (employee == null){
                    Log.i(TAG, "employee details missing!");
                    Toast.makeText(getActivity(), "Employee details missing!", Toast.LENGTH_SHORT).show();
                }else {
                    emp_id = String.valueOf(employee.getEmpId());
                    emp_name = employee.getName();
                    location = employee.getLocation();
                    batch = employee.getLg();
                    date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                    Log.i(TAG, "Date: " + date + " | rating: " + rating);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, FEEDBACK_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
//                                    Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();

                                    try {
                                        JSONObject reader = new JSONObject(response);
                                        String result = reader.getString(KEY_RESULT);

                                        if (result.equalsIgnoreCase("Success")){
                                            Toast.makeText(getActivity(), "Feedback recorded successfully!", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(getActivity(), "Your feedback has already been recorded!", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }finally {
                                        resetForm();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getActivity(),"Can not connect to internet!",Toast.LENGTH_SHORT).show();
                                }
                            }){
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put(KEY_EMP_ID,emp_id);
                            params.put(KEY_EMP_NAME,emp_name);
                            params.put(KEY_EMP_LOC,location);
                            params.put(KEY_EMP_BATCH,batch);
                            params.put(KEY_FEEDBACK_TYPE,feedback_type);
                            params.put(KEY_SERVICE_PROVIDER,serviceProvider);
                            params.put(KEY_RATING,rating);
                            params.put(KEY_COMMENT,comment);
                            params.put(KEY_DATE,date);
                            return params;
                        }

                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    requestQueue.add(stringRequest);
                }
            }
        }
    };

    private TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() < 30){
                charCount.setTextColor(Color.RED);
                charCount.setText("Character Remaining: " + (30 - charSequence.length()));
            }else {
                charCount.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                charCount.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private RatingBar.OnRatingBarChangeListener ratingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            if (v < 1.0f)
                ratingBar.setRating(1.0f);
        }
    };


    public IlpFeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ilp_feedback, container, false);

        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        serviceProviderEt = (EditText) rootView.findViewById(R.id.feedback_service_provider_et);
        commentEt = (EditText) rootView.findViewById(R.id.feedback_comment_et);
        feedbackHeading = (TextView) rootView.findViewById(R.id.feedback_type_header);
        charCount = (TextView) rootView.findViewById(R.id.feedback_word_count_tv);
        ratingBar = (RatingBar) rootView.findViewById(R.id.feedback_rating);
        submitButton = (ImageButton) rootView.findViewById(R.id.feedback_send_btn);

        spinner.setOnItemSelectedListener(itemSelectedListener);
        commentEt.addTextChangedListener(textChangeListener);
        submitButton.setOnClickListener(clickListener);
        ratingBar.setOnRatingBarChangeListener(ratingBarChangeListener);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.feedback_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        checkInternetConnection();

        return rootView;
    }

    private void resetForm(){
        serviceProviderEt.getText().clear();
        commentEt.getText().clear();
        ratingBar.setRating(1F);
        charCount.setTextColor(Color.DKGRAY);
    }

    public void checkInternetConnection() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("No Internet");
        alertDialogBuilder.setMessage("Please connect to a network.");

        alertDialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                checkInternetConnection();
            }
        });

        alertDialogBuilder.setNegativeButton("Leave", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        if (!Util.hasInternetAccess(getActivity())) {
            alertDialog.show();
            return;
        } else {
            alertDialog.dismiss();
        }
    }

}
