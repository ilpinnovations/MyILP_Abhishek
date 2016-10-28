package com.ilp.ilpschedule.fragments;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.activities.MainActivity;
import com.ilp.ilpschedule.adapter.ContactsAdapter;
import com.ilp.ilpschedule.db.DbHelper;
import com.ilp.ilpschedule.model.Slot;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleFragment extends Fragment {

    public static final String TAG = "com.tcs.myilp.ScheduleFragment";
    private TextView textViewdate;
    private EditText editTextLgName;
    private Date date;
    private String lgName;
    private ListView listViewSchedule;
    private ContactsAdapter ContactsAdapter;
    private ImageButton changeDate;
    private ImageView getSchedule;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "E, dd MMM yyyy", Locale.US);
    private RequestQueue reqQueue;

    private Response.Listener<String> schedulerTaskSuccessListner = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {
            try {
                Log.d(TAG, "got some data from server");
                JSONObject jobj = new JSONObject(response);
                if (jobj.has("Android")) {
                    JSONArray jarr = jobj.getJSONArray("Android");
                    if (jarr.length() > 0) {
                        JSONObject obj;
                        ArrayList<Slot> data = new ArrayList<>();
                        Slot s;
                        for (int i = 0; i < jarr.length(); i++) {
                            s = new Slot();
                            obj = jarr.getJSONObject(i);
                            if (obj.has("course"))
                                s.setCourse(obj.getString("course"));
                            if (obj.has("faculty"))
                                s.setFaculty(obj.getString("faculty"));
                            if (obj.has("room"))
                                s.setRoom(obj.getString("room"));
                            if (obj.has("slot"))
                                s.setSlot(obj.getString("slot"));
                            if (obj.has("batch"))
                                s.setBatch(obj.getString("batch"));
                            if (obj.has("date1"))
                                s.setDate(Date.valueOf(obj.getString("date1")));
                            data.add(s);
                            System.out.println(s);
                        }

                        if (getActivity() != null) {
                            DbHelper dbh = new DbHelper(getActivity());
                            dbh.addSlots(data);


                            ((ContactsAdapter) listViewSchedule.getAdapter())
                                    .setData(dbh.getSchedule(date, editTextLgName
                                            .getText().toString().trim()
                                            .toUpperCase(Locale.US)));

                        }
                    } else {
                        // no schdule
                        if (getActivity() != null)
                            Util.toast(getActivity(),
                                    getString(R.string.toast_no_schedule));
                    }
                }

            } catch (JSONException ex) {
                Log.d(TAG, ex.getLocalizedMessage());
            } finally {
                if (getActivity() != null)
                    Util.hideProgressDialog();

            }
            Log.d(TAG, response);
        }

    };
    private Response.ErrorListener schedulerTaskErrorListner = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                Util.toast(getActivity(),
                        "Error connecting server. Try again!");
                Log.d(TAG, "error" + error + error.getLocalizedMessage());

                Util.hideProgressDialog();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    };
    private OnDateSetListener dateSetListner = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String dateStr = String.valueOf(year)
                    + "-"
                    + (monthOfYear < 9 ? "0" + String.valueOf(monthOfYear + 1)
                    : String.valueOf(monthOfYear + 1))
                    + "-"
                    + (dayOfMonth < 9 ? "0" + String.valueOf(dayOfMonth)
                    : String.valueOf(dayOfMonth));
            Log.d(TAG, dateStr);
            date = Date.valueOf(dateStr);
            textViewdate.setText(dateFormat.format(date));
            fetchSchedule();
        }
    };
    private OnClickListener dateChangeClickListner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            new DatePickFragment(dateSetListner, date).show(
                    getFragmentManager(), DatePickFragment.TAG);
        }
    };
    private OnClickListener getScheduleClickListner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            fetchSchedule();
        }
    };

    public ScheduleFragment() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String dateStr = String.valueOf(year)
                + "-"
                + (month < 10 ? "0" + String.valueOf(month) : String
                .valueOf(month)) + "-"
                + (day < 10 ? "0" + String.valueOf(day) : String.valueOf(day));
        Log.d(TAG, "dateStr" + dateStr);
        date = Date.valueOf(dateStr);
    }

    private RequestQueue getRequestQueue() {
        if (reqQueue == null)
            reqQueue = Volley.newRequestQueue(getActivity());
        return reqQueue;
    }

    private void fetchScheduleFromServer() {
        String batch = editTextLgName.getText().toString().trim()
                .toUpperCase(Locale.US);
        if (isDataValid()) {
            if (Util.hasInternetAccess(getActivity())) {
                Util.showProgressDialog(getActivity());
                Map<String, String> params = new HashMap<>();
                params.put(Constants.NETWORK_PARAMS.SCHEDULE.BATCH, batch);
                params.put(Constants.NETWORK_PARAMS.SCHEDULE.DATE,
                        Constants.paramsDateFormat.format(date));
                String url = new StringBuilder(
                        Constants.NETWORK_PARAMS.SCHEDULE.URL).append(
                        Util.getUrlEncodedString(params)).toString();

                StringRequest request = new StringRequest(url,
                        schedulerTaskSuccessListner, schedulerTaskErrorListner);
                request.setTag(1);
                getRequestQueue().cancelAll(1);
                getRequestQueue().add(request);
            } else {
                Util.toast(getActivity(),
                        getString(R.string.toast_no_internet));
                Util.hideProgressDialog();
            }
        }
    }

    private void fetchSchedule() {
        if (isDataValid()) {
            Util.hideKeyboard(getActivity());
            // check data in db then do n/w operation
            String batch = editTextLgName.getText().toString().trim()
                    .toUpperCase(Locale.US);
            List<Slot> schedule = new DbHelper(getActivity()).getSchedule(date, batch);
            if (schedule.size() == 0) {
                // no data in db check for server
                Log.d(TAG, "no data in db check for server");
                fetchScheduleFromServer();
            } else {
                // we got some data from db
                Log.d(TAG, "we got some data from db");
                ((ContactsAdapter) listViewSchedule.getAdapter())
                        .setData(schedule);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container,
                false);

        textViewdate = (TextView) rootView
                .findViewById(R.id.textViewScheduleDate);

        editTextLgName = (EditText) rootView.findViewById(R.id.editTextLgName);
        if (getActivity() != null) {
            try {
                lgName = Util.getEmployee(getActivity()).getLg();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }

        changeDate = (FloatingActionButton) rootView
                .findViewById(R.id.imageButtonChangeDate);

        changeDate.setOnClickListener(dateChangeClickListner);

        getSchedule = (ImageView) rootView
                .findViewById(R.id.imageButtonGetSchedule);

        getSchedule.setOnClickListener(getScheduleClickListner);

        if (ContactsAdapter == null)
            ContactsAdapter = new ContactsAdapter(getActivity(),
                    new ArrayList<Slot>(),
                    ((MainActivity) getActivity())
                            .getScheduleItemClickListner());

        listViewSchedule = (ListView) rootView
                .findViewById(R.id.listViewSchedule);
        listViewSchedule.setEmptyView(rootView
                .findViewById(R.id.textViewScheduleEmptyView));
        listViewSchedule.setAdapter(ContactsAdapter);
        if (savedInstanceState != null) {
            ArrayList<Slot> data = savedInstanceState
                    .getParcelableArrayList("schedules");
            ContactsAdapter.setData(data);
            lgName = savedInstanceState.getString("lgName");
            date = new Date(savedInstanceState.getLong("date"));
        }
        textViewdate.setText(dateFormat.format(date));
        editTextLgName.setText(lgName);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_schedule_refresh) {
            fetchScheduleFromServer();
            return true;
        } else if (id == R.id.action_schedule_help) {
            Util.toast(getActivity(), getString(R.string.toast_schedule_help));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isDataValid() {
        Log.d(TAG, "cheking data");
        if (date != null
                && editTextLgName.getText().toString().trim().length() > 0) {
            Pattern pattern = Pattern.compile("[^a-z0-9]",
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(editTextLgName.getText()
                    .toString().trim());
            if (matcher.find()) {
                Util.toast(getActivity(),
                        getString(R.string.toast_invalid_lg));
                return false;
            }
            return true;
        } else {
            Util.toast(getActivity(),
                    getString(R.string.toast_blank_lg));
            return false;
        }
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.title_schedule);
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchSchedule();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Slot> values = ContactsAdapter.getData();
        outState.putParcelableArrayList("schedules", values);
        outState.putString("lgName", lgName);
        outState.putLong("date", date.getTime());
    }

}
