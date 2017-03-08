package com.ilp.ilpschedule.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.model.AssociateGAS;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.model.ILPLocation;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Util {
    public static final String GOOGLE_PROJECT_ID = "1039038689673";
    private static final String TAG = "Util";
    private static List<ILPLocation> locations;
    private static ProgressDialog progressDialog;
    private static boolean WORK_IN_PROGRESS = false;

    public static int saveEmployee(Context context, Employee emp) {
        int empError = emp.isValid();
        if (empError == Constants.EMP_ERRORS.NO_ERROR) {
            SharedPreferences spf = context.getSharedPreferences(
                    Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
            spf.edit()
                    .putLong(Constants.PREF_KEYS.EMP_ID, emp.getEmpId())
                    .putString(Constants.PREF_KEYS.EMP_NAME, emp.getName())
                    .putString(Constants.PREF_KEYS.EMP_EMAIL, emp.getEmail())
                    .putString(Constants.PREF_KEYS.EMP_LG, emp.getLg())
                    .putString(Constants.PREF_KEYS.EMP_LOCATION,
                            emp.getLocation()).apply();
        }
        return empError;

    }

    public static Employee getEmployee(Context context) {
        SharedPreferences spf = context.getSharedPreferences(
                Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        Employee emp = new Employee();
        emp.setEmpId(spf.getLong(Constants.PREF_KEYS.EMP_ID, -1));
        emp.setName(spf.getString(Constants.PREF_KEYS.EMP_NAME, null));

        emp.setEmail(spf.getString(Constants.PREF_KEYS.EMP_EMAIL, null));
        emp.setLg(spf.getString(Constants.PREF_KEYS.EMP_LG, null));
        emp.setLocation(spf.getString(Constants.PREF_KEYS.EMP_LOCATION, null));
        if (emp.isValid() == Constants.EMP_ERRORS.NO_ERROR)
            return emp;
        else
            return null;
    }

    public static String getErrorMsg(int errorCode) {
        String msg = "";
        switch (errorCode) {
            case Constants.EMP_ERRORS.NAME.BLANK:
                msg = "Name cannot be blank";
                break;
            case Constants.EMP_ERRORS.NAME.INVALID:
                msg = "Invalid name";
                break;
            case Constants.EMP_ERRORS.EMAIL.BLANK:
                msg = "Email cannot be blank";
                break;
            case Constants.EMP_ERRORS.EMAIL.INVALID:
                msg = "Please enter official email id.";
                break;
            case Constants.EMP_ERRORS.BATCH.BLANK:
                msg = "Batch cannot be blank";
                break;
            case Constants.EMP_ERRORS.BATCH.INVALID:
                msg = "Invalid batch";
                break;
            case Constants.EMP_ERRORS.EMP_ID.BLANK:
                msg = "Employee ID cannot be blank";
                break;
            case Constants.EMP_ERRORS.EMP_ID.INVALID:
                msg = "Invalid employee ID";
                break;
            case Constants.EMP_ERRORS.LOCATION.BLANK:
                msg = "Location cannot be blank";
                break;
            case Constants.EMP_ERRORS.LOCATION.INVALID:
                msg = "Invalid Location";
                break;
            case Constants.EMP_ERRORS.EMP_LG.BLANK:
                msg = "LG Name cannot be blank";
                break;
            case Constants.EMP_ERRORS.EMP_LG.INVALID:
                msg = "Invalid LG name";
                break;
        }
        return msg;
    }

    public static boolean checkLogin(Context context) {
        SharedPreferences spf = context.getSharedPreferences(
                Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return spf.getBoolean(Constants.PREF_KEYS.IS_LOGIN, false);
    }

    public static void doLogin(Context context) {
        SharedPreferences spf = context.getSharedPreferences(
                Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        spf.edit().putBoolean(Constants.PREF_KEYS.IS_LOGIN, true).apply();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null
                && activity.getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), 0);
    }

    public static boolean hasInternetAccess(Context applicationContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public static void toast(Context context, String msg) {
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

    }

    public static ProgressDialog getProgressDialog(Activity activity) {
        if (progressDialog == null && activity != null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setOwnerActivity(activity);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        return progressDialog;
    }


    public static void showProgressDialog(Activity activity) {
        if (!WORK_IN_PROGRESS && activity != null) {
            try {
                getProgressDialog(activity).setMessage("Please wait..");
                getProgressDialog(activity).show();
                WORK_IN_PROGRESS = !WORK_IN_PROGRESS;
            } catch (Exception ex) {
                Log.d(TAG, "Error");
                ex.printStackTrace();
            }

        }
    }

    public static void hideProgressDialog() {
        if (WORK_IN_PROGRESS && progressDialog != null && progressDialog.isShowing()) {

            try {
                progressDialog.dismiss();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            WORK_IN_PROGRESS = !WORK_IN_PROGRESS;
        }
    }

    public static boolean checkString(String string) {
        return string != null && string.trim().length() > 0;
    }

    public static String getUrlEncodedString(Map<String, String> parameters) {
        StringBuilder strb = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                strb.append(
                        URLEncoder.encode(entry.getKey(), Constants.CHARSET))
                        .append(Constants.EQUALS)
                        .append(URLEncoder.encode(entry.getValue(),
                                Constants.CHARSET)).append(Constants.AND);
            }
            int length = strb.length();
            if (length > 0) {
                // remove extra & at end of string
                strb.deleteCharAt(length - 1);
            }
        } catch (Exception ex) {
            Log.d(TAG, "invalid parameters " + ex.getLocalizedMessage());
            strb.setLength(0);
        }
        return strb.toString();
    }

    public static int getBadgeByPoints(int count) {
        if (count >= 15 && count < 30)
            return R.drawable.badge_karma_warrior;
        else if (count >= 30 && count < 60)
            return R.drawable.badge_karma_empower;
        else if (count >= 60 && count < 100)
            return R.drawable.badge_karma_leader;
        else if (count >= 100)
            return R.drawable.badge_karma_king;
        else
            return R.drawable.badge_nobadge;
    }

    public static List<ILPLocation> getLocations(String location, String type) {
        List<ILPLocation> locs = new ArrayList<>();
        for (ILPLocation loc : getLocations()) {
            if (loc.getLocation().equalsIgnoreCase(location)
                    && loc.getType().equalsIgnoreCase(type))
                locs.add(loc);
        }
        return locs;
    }

    public static List<ILPLocation> getLocations() {
        if (locations == null) {
            locations = new ArrayList<>();

            ILPLocation location;
            location = new ILPLocation();
            location.setLocation(Constants.LOCATIONS.TRIVANDRUM.LOC_NAME);
            location.setLat(8.5525038);
            location.setLon(76.8800041);
            location.setType(Constants.LOCATIONS.TYPE.ILP);
            location.setName("Peepul park");
            locations.add(location);

            location = new ILPLocation();
            location.setLocation(Constants.LOCATIONS.TRIVANDRUM.LOC_NAME);
            location.setLat(8.555145);
            location.setLon(76.880294);
            location.setType(Constants.LOCATIONS.TYPE.ILP);
            location.setName("TCS CLC building");
            locations.add(location);

            location = new ILPLocation();
            location.setLocation(Constants.LOCATIONS.GUWAHATI.LOC_NAME);
            location.setLat(26.150799);
            location.setLon(91.790978);
            location.setType(Constants.LOCATIONS.TYPE.ILP);
            location.setName("Guwahati ILP center");
            locations.add(location);

            location = new ILPLocation();
            location.setLocation(Constants.LOCATIONS.CHENNAI.LOC_NAME);
            location.setLat(13.096596);
            location.setLon(80.165716);
            location.setType(Constants.LOCATIONS.TYPE.ILP);
            location.setName("Chennai ILP center");
            locations.add(location);

            location = new ILPLocation();
            location.setLocation(Constants.LOCATIONS.HYDERABAD.LOC_NAME);
            location.setLat(17.427160);
            location.setLon(78.331661);
            location.setType(Constants.LOCATIONS.TYPE.ILP);
            location.setName("Hyderabad ILP center");
            locations.add(location);

            location = new ILPLocation();
            location.setLocation(Constants.LOCATIONS.TRIVANDRUM.LOC_NAME);
            location.setLat(8.553064);
            location.setLon(76.877913);
            location.setType(Constants.LOCATIONS.TYPE.HOSTEL);
            location.setName("Executive Hostel TCS");
            locations.add(location);

            location = new ILPLocation();
            location.setLocation(Constants.LOCATIONS.TRIVANDRUM.LOC_NAME);
            location.setLat(8.551389);
            location.setLon(76.879763);
            location.setType(Constants.LOCATIONS.TYPE.HOSTEL);
            location.setName("Peepul Park Hostel TCS");
            locations.add(location);

            location = new ILPLocation();
            location.setLocation(Constants.LOCATIONS.AHMEDABAD.LOC_NAME);
            location.setLat(8.551389);
            location.setLon(76.879763);
            location.setType(Constants.LOCATIONS.TYPE.ILP);
            location.setName("TCS Ahmedabad ILP");
            locations.add(location);

        }
        return locations;
    }


    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity.getApplicationContext());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, activity, 0).show();
            return false;
        }
    }

    public static void saveMyPoints(Context context, int points) {
        SharedPreferences spf = context.getSharedPreferences(
                Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        spf.edit().putInt(Constants.PREF_KEYS.BADGE_POINTS, points).apply();
    }

    public static int getMyPoints(Context context) {
        SharedPreferences spf = context.getSharedPreferences(
                Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return spf.getInt(Constants.PREF_KEYS.BADGE_POINTS, 0);
    }

    public static String getRegId(Context context) {
        SharedPreferences spf = context.getSharedPreferences(
                Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return spf.getString(Constants.PREF_KEYS.GCM_REG_ID, null);
    }

    public static void saveRegId(Context context, String regId) {
        SharedPreferences spf = context.getSharedPreferences(
                Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        spf.edit().putString(Constants.PREF_KEYS.GCM_REG_ID, regId).apply();
    }

    public static void clearPref(Context context) {
        SharedPreferences spf = context.getSharedPreferences(
                Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences sd = context.getSharedPreferences("chennai", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sd.edit();
        ed.putBoolean("response", false);
        ed.apply();
        SharedPreferences FF = context.getSharedPreferences("tutorial", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = FF.edit();
        edt.putBoolean("shown", false);
        edt.apply();
        String regId = spf.getString(Constants.PREF_KEYS.GCM_REG_ID, null);
        spf.edit().clear().apply();
        if (regId != null) {
            spf.edit().putString(Constants.PREF_KEYS.GCM_REG_ID, regId)
                    .apply();
        }
    }

    public static boolean isGrabASeat(Context context){
        Employee employee = getEmployee(context);

        if (employee.getLocation().equalsIgnoreCase("Trivandrum")){
            return true;
        }else {
            return false;
        }
    }

    public static void saveAssociateGAS(Context context, AssociateGAS associate){
        SharedPreferences prefs = context.getSharedPreferences(Constants.GRAB_A_SEAT.PREFERENCES.NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(Constants.GRAB_A_SEAT.PREFERENCES.CHECK_ASSOCIATE.KEY_LG, associate.getLg());
        ed.putString(Constants.GRAB_A_SEAT.PREFERENCES.CHECK_ASSOCIATE.KEY_ACCOMMODATION, associate.getAccommodation());
        ed.putString(Constants.GRAB_A_SEAT.PREFERENCES.CHECK_ASSOCIATE.KEY_LAP, associate.getLap());
        ed.apply();
    }

    public static AssociateGAS getAssociateGAS(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.GRAB_A_SEAT.PREFERENCES.NAME, Context.MODE_PRIVATE);

        AssociateGAS data = new AssociateGAS();
        data.setLg(prefs.getString(Constants.GRAB_A_SEAT.PREFERENCES.CHECK_ASSOCIATE.KEY_LG, ""));
        data.setAccommodation(prefs.getString(Constants.GRAB_A_SEAT.PREFERENCES.CHECK_ASSOCIATE.KEY_ACCOMMODATION, ""));
        data.setLap(prefs.getString(Constants.GRAB_A_SEAT.PREFERENCES.CHECK_ASSOCIATE.KEY_LAP, "no"));

        return data;
    }

    public static void clearGASPrefs(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.GRAB_A_SEAT.PREFERENCES.NAME, Context.MODE_PRIVATE);

        prefs.edit().clear().apply();
    }

    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }

    public static long utcToCurrentTime(long time){
        // converting UTC to actual time
        // time * 1000 + 5hr 30min
        long currentTime = time * 1000 + ((5*60 + 30)* 60 * 10);
        return currentTime;
    }

    public static double kelvinToCelsius(double temp){
        // converting temperature in kelvin to celsius
        // celsius = kelvin - 273.15

        return temp - 273.15;
    }

    public static String formatWeatherTime(long time){

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);

        long sunriseTime = Util.utcToCurrentTime(time);
        String formattedTime = formatter.format(sunriseTime);

        return formattedTime;
    }
}
