package com.ilp.ilpschedule.util;

import android.app.Activity;
import android.app.admin.SystemUpdatePolicy;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ilp.ilpschedule.BuildConfig;
import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.adapter.ContactsAdapter;
import com.ilp.ilpschedule.db.DbHelper;
import com.ilp.ilpschedule.model.ChatType;
import com.ilp.ilpschedule.model.CseBean;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.model.Slot;
import com.ilp.ilpschedule.model.WeatherApiBean;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.api.model.AIResponse;
import ai.api.model.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 1115394 on 12/8/2016.
 */
public class AIResponseHandler {

    private final String TAG = AIResponseHandler.class.getSimpleName();
    private Gson gson = ai.api.android.GsonFactory.getGson();

    private RequestQueue mRequestQueue;
    private Context mContext;
    private String mLocation;

    public AIResponseHandler(Context context){
        mContext = context;
    }

    public void processIntent(AIResponse response){
        Log.d(TAG, "on identifyIntent()");
        Log.i(TAG, "RESPONSE:\n" + gson.toJson(response));

        mRequestQueue = Volley.newRequestQueue(mContext);

        Result result = response.getResult();
        String action = result.getAction();

        switch (action){

            case Constants.ACTION_TYPES.WEATHER_INTENT:
                weatherIntent(result);
                break;

            case Constants.ACTION_TYPES.INPUT_UNKNOWN:
                inputUnknownIntent(result);
                break;
        }
    }

    //===============================================================================================================================
    //          Weather Intent
    //===============================================================================================================================

    private void weatherIntent(Result result){
        Log.d(TAG, "on weatherIntent()");
        boolean isActionIncomplete = result.isActionIncomplete();

        if (isActionIncomplete){

        }else {
            // Calling open weather map API
            Employee employee = Util.getEmployee(mContext);
            String location;

            if (employee == null){
                Util.toast(mContext, "Unable to fetch employee details");
                Log.i(TAG, "Unable to fetch employee details");
                return;
            }else {
                location = employee.getLocation() + ",IN";
                mLocation = location;
            }

            String url = Constants.WEATHER_API.API_END_POINT;
            url = url + "?q=" + location
                    + "&appid=" + BuildConfig.WEATHER_API_KEY;

            Log.i(TAG, url);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    url,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, "in weather intent onResponse");
                            WeatherApiBean api = new WeatherApiBean();

                            String location = mLocation;
                            String weatherDescription;
                            String temp;
                            String tempMax;
                            String tempMin;
                            String pressure;
                            String humidity;
                            String windSpeed;
                            String weatherId;
                            String sunrise;
                            String sunset;

                            try {
                                JSONObject weatherObj = response.getJSONArray(Constants.WEATHER_API.TAG_WEATHER).getJSONObject(0);
                                weatherDescription = weatherObj.getString(Constants.WEATHER_API.TAG_WEATHER_MAIN);
                                weatherId = String.valueOf(weatherObj.getInt(Constants.WEATHER_API.TAG_WEATHER_ID));

                                JSONObject mainObj = response.getJSONObject(Constants.WEATHER_API.TAG_MAIN);
                                temp = String.valueOf(mainObj.getString(Constants.WEATHER_API.TAG_MAIN_TEMP));
                                tempMax = String.valueOf(mainObj.getString(Constants.WEATHER_API.TAG_MAIN_TEMP_MAX));
                                tempMin = String.valueOf(mainObj.getString(Constants.WEATHER_API.TAG_MAIN_TEMP_MIN));
                                pressure = String.valueOf(mainObj.getString(Constants.WEATHER_API.TAG_MAIN_PRESSURE));
                                humidity = String.valueOf(mainObj.getString(Constants.WEATHER_API.TAG_MAIN_HUMIDITY));

                                JSONObject windObj = response.getJSONObject(Constants.WEATHER_API.TAG_WIND);
                                windSpeed = String.valueOf(windObj.getString(Constants.WEATHER_API.TAG_WIND_SPEED));

                                JSONObject sysObj = response.getJSONObject(Constants.WEATHER_API.TAG_SYS);
                                sunrise = Util.formatWeatherTime(sysObj.getLong(Constants.WEATHER_API.TAG_SYS_SUNRISE));
                                sunset = Util.formatWeatherTime(sysObj.getLong(Constants.WEATHER_API.TAG_SYS_SUNSET));

                                api.setLocation(location);
                                api.setHumidity(humidity);
                                api.setPressure(pressure);
                                api.setSunrise(sunrise);
                                api.setSunset(sunset);
                                api.setTemp(temp);
                                api.setTempMax(tempMax);
                                api.setTempMin(tempMin);
                                api.setWeatherDescription(weatherDescription);
                                api.setWindSpeed(windSpeed);
                                api.setWeatherId(weatherId);

                                StringBuilder speechResponse = new StringBuilder();
                                speechResponse.append("Temperature is  " + Util.kelvinToCelsius(Double.parseDouble(api.getTemp())) + "°celcius\n");
                                speechResponse.append("Humidity is " + api.getHumidity() + "%\n");
                                speechResponse.append("Sunrise is at " + sunrise + "\n");
                                speechResponse.append("Sunset is at " + sunset + "\n");

                                EventBus.getDefault().post(new MessageEvent(speechResponse.toString(), api, ChatType.BOT_WEATHER));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "weather api call failed");
                            error.printStackTrace();
                            Util.toast(mContext, "weather call failed");
                        }
            });

            mRequestQueue.add(request);
        }
    }


    //===============================================================================================================================
    //          Input Unknown Intent
    //===============================================================================================================================

    private void inputUnknownIntent(Result result){
        Log.d(TAG, "on inputUnknownIntent()");
        boolean isActionIncomplete = result.isActionIncomplete();

        if (isActionIncomplete){

        }else {
            String query = result.getResolvedQuery();
            query = query.replace(" ", "+");
            query = query.replace(".", "+");

            Log.i(TAG, "Query: " + query);

            String url = Constants.SEARCH_API.BASE_URL
                    + "?key=" + BuildConfig.CSE_API_KEY
                    + "&cx=" + BuildConfig.CSE_ID
                    + "&q=" + query;

            Log.i(TAG, url);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    url,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, "in cse intent onResponse");

                            String heading;
                            String link;
                            String summary;
                            String imagePath;

                            try {
                                JSONObject itemArray = response.getJSONArray(Constants.SEARCH_API.TAG_ITEMS).getJSONObject(0);

                                heading = itemArray.getString(Constants.SEARCH_API.TAG_ITEMS_TITLE);
                                link = itemArray.getString(Constants.SEARCH_API.TAG_ITEMS_LINK);
                                summary = itemArray.getString(Constants.SEARCH_API.TAG_ITEMS_SNIPPET);
                                imagePath = itemArray.getJSONObject(Constants.SEARCH_API.TAG_ITEMS_PAGEMAP).getJSONArray(Constants.SEARCH_API.TAG_ITEMS_CSE_THUMBNAIL).getJSONObject(0).getString(Constants.SEARCH_API.TAG_ITEMS_SRC);

                                CseBean bean = new CseBean(heading, link, summary, imagePath);

                                EventBus.getDefault().post(new MessageEvent(summary, bean, ChatType.BOT_SEARCH));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "google custom search api call failed");
                            error.printStackTrace();
                            Util.toast(mContext, "Google custom search api call failed");
                        }
                    });

            mRequestQueue.add(request);

        }
    }

    //===============================================================================================================================
    //          Input Unknown Intent
    //===============================================================================================================================

    private com.android.volley.Response.Listener<String> schedulerTaskSuccessListner = new com.android.volley.Response.Listener<String>() {

        @Override
        public void onResponse(String response) {
            try {
                Log.d(TAG, "got some data from server");
                JSONObject jobj = new JSONObject(response);
                if (jobj.has("Android")) {
                    JSONArray jarr = jobj.getJSONArray("Android");
                    if (jarr.length() > 0) {
                        JSONObject obj;
                        Slot s;
                        StringBuilder message = new StringBuilder();

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
                                s.setDate(java.sql.Date.valueOf(obj.getString("date1")));

                            message.append("SLOT: " + s.getSlot());
                            message.append("LECTURE: " + s.getCourse());
                            message.append("FACULTY: " + s.getFaculty());
                            message.append("ROOM: " + s.getRoom());
                            message.append("\n");
                            System.out.println(s);
                        }

                        EventBus.getDefault().post(new MessageEvent(message.toString(), ChatType.BOT_DEFAULT));
                    } else {
                        // no schedule
                        if (mContext != null) {
                            Util.toast(mContext,
                                    mContext.getString(R.string.toast_no_schedule));

                            EventBus.getDefault().post(new MessageEvent(mContext.getString(R.string.toast_no_schedule), ChatType.BOT_DEFAULT));
                        }
                    }
                }

            } catch (JSONException ex) {
                Log.d(TAG, ex.getLocalizedMessage());
            } finally {
                if (mContext != null)
                    Util.hideProgressDialog();

            }
            Log.d(TAG, response);
        }

    };
    private com.android.volley.Response.ErrorListener schedulerTaskErrorListner = new com.android.volley.Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                Util.toast(mContext,
                        "Error connecting server. Try again!");
                Log.d(TAG, "error" + error + error.getLocalizedMessage());

                EventBus.getDefault().post(new MessageEvent("Error connecting server. Try again!", ChatType.BOT_DEFAULT));
                Util.hideProgressDialog();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    };

    private void fetchScheduleFromServer(String lg, Date date) {
        String batch = lg.trim()
                .toUpperCase(Locale.US);
        if (Util.hasInternetAccess(mContext)) {
            Util.showProgressDialog((Activity) mContext);
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
            mRequestQueue.add(request);
        } else {
            Util.toast(mContext,
                    mContext.getString(R.string.toast_no_internet));
            Util.hideProgressDialog();
        }
    }

    private void fetchSchedule(String lg, Date date) {
        Util.hideKeyboard((Activity) mContext);
        // check data in db then do n/w operation
        String batch = lg.trim()
                .toUpperCase(Locale.US);
        List<Slot> schedule = new DbHelper(mContext).getSchedule(date, batch);
        if (schedule.size() == 0) {
            // no data in db check for server
            Log.d(TAG, "no data in db check for server");
            fetchScheduleFromServer(lg, date);
        } else {
            // we got some data from db
            Log.d(TAG, "we got some data from db");

            StringBuilder message = new StringBuilder();

            for (Slot s: schedule){
                message.append("SLOT: " + s.getSlot());
                message.append("LECTURE: " + s.getCourse());
                message.append("FACULTY: " + s.getFaculty());
                message.append("ROOM: " + s.getRoom());
                message.append("\n");
            }

            EventBus.getDefault().post(new MessageEvent(message.toString(), ChatType.BOT_DEFAULT));
        }
    }


    private void scheduleIntent(Result result){
        Log.d(TAG, "on scheduleIntent()");
        boolean isActionIncomplete = result.isActionIncomplete();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        if (isActionIncomplete){

        }else {

            try {
                HashMap<String, JsonElement> params = result.getParameters();
                String date = params.get(Constants.SCHEDULE_INTENT.DATE_KEY).getAsString();

                boolean isDate = date.matches("^\\d{4}-\\d{2}-\\d{2}");

                if (isDate){
                    String lg = Util.getEmployee(mContext).getLg();

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                    fetchSchedule(lg, new Date(inputFormat.parse(date).getTime()));
                }else {
                    date = formatter.format(new java.util.Date());
                }
            }catch (ParseException | NullPointerException e){
                e.printStackTrace();
            }
        }
    }

}
