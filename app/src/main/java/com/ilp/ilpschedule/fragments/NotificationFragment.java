package com.ilp.ilpschedule.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.adapter.NotificationAdapter;
import com.ilp.ilpschedule.db.DbHelper;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.model.Notification;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationFragment extends Fragment {
    public static final String TAG = "com.ilp.ilpschedule.fragments.NotificationFragment";
    private ListView notificationList;
    private NotificationAdapter notificationAdapter;
    private RequestQueue requestQueue;
    private ArrayList<Notification> notificationArrayList = new ArrayList<>();

    private TextView emptyView;

    private Response.Listener<String> requestSuccessListner = new Response.Listener<String>() {

        @Override
        public void onResponse(String result) {
            Log.d(TAG, result);

            try {
                JSONObject obj = new JSONObject(result);
                if (obj.has("Android")) {
                    notificationArrayList.clear();
                    JSONArray arr = obj.getJSONArray("Android");
                    Notification n;
                    for (int i = 0; i < arr.length(); i++) {
                        obj = arr.getJSONObject(i);
                        n = new Notification(
                                Notification.inputDateFormat.parse(obj
                                        .getString("msg_date")),
                                obj.getString("message"), obj.getLong("s_no"));
                        notificationArrayList.add(n);
                        Log.d(TAG, n.toString());
                    }
                }
            } catch (Exception ex) {
                Log.d(TAG, ex.getLocalizedMessage());
            } finally {
                Log.i(TAG, notificationArrayList.toString());

//                DbHelper dbh = new DbHelper(getActivity());
//                int added = dbh.addNotifications(notificationArrayList);
//
//                Log.i(TAG, "Added: " + added);

                if (notificationArrayList.size() != 0){
                    emptyView.setVisibility(View.GONE);
                }else {
                    emptyView.setVisibility(View.VISIBLE);
                }

                notificationAdapter.notifyDataSetChanged();
                Util.hideProgressDialog();
            }
        }
    };
    private Response.ErrorListener requestErrorListner = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError result) {
            try {
                Log.d(TAG, "error " + result.getMessage());
                Util.hideProgressDialog();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "in onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_notification,
                container, false);

        emptyView = (TextView) rootView.findViewById(R.id.textViewNotificationEmptyView);

        if (notificationAdapter == null)
            notificationAdapter = new NotificationAdapter(getActivity()
                    .getApplicationContext(), notificationArrayList);

        notificationList = (ListView) rootView
                .findViewById(R.id.listViewNotification);
        notificationList.setAdapter(notificationAdapter);
//        notificationList.setEmptyView((TextView) rootView
//                .findViewById(R.id.textViewNotificationEmptyView));
        setHasOptionsMenu(true);
        VolleyLog.DEBUG = true;
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notification_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notification_refresh) {
            fetchNotifications();
            return true;
        } else if (id == R.id.action_notification_help) {
            Util.toast(getActivity(), getString(R.string.toast_notification_help));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "in onResume");
        getActivity().setTitle(R.string.title_notification);
        fetchNotifications();
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.i(TAG, "in onStart");
        if (notificationAdapter != null) {
//            notificationArrayList.clear();
//            notificationArrayList = new DbHelper(getActivity())
//                    .getNotifications();
//
//            Log.i(TAG, "onStart | " + notificationArrayList.toString());
//            if (notificationArrayList != null && notificationArrayList.size() > 0) {
//                emptyView.setVisibility(View.GONE);
//                notificationAdapter.notifyDataSetChanged();
//            } else{
//                emptyView.setVisibility(View.VISIBLE);
//                fetchNotifications();
//            }

            fetchNotifications();
        }
        super.onStart();
    }

    private void fetchNotifications() {
        Log.i(TAG, "in fetchNotifications");
        if (Util.hasInternetAccess(getActivity())) {
            Util.showProgressDialog(getActivity());
            if (requestQueue == null)
                requestQueue = Volley.newRequestQueue(getActivity());

            Employee employee = Util.getEmployee(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_NOTIFICATION + "?batch=" + employee.getLg() + "&location=" + employee.getLocation(),
                    requestSuccessListner, requestErrorListner);

            requestQueue.add(stringRequest);

        } else {
            Util.toast(getActivity(), getString(R.string.toast_no_internet));
        }
    }
}
