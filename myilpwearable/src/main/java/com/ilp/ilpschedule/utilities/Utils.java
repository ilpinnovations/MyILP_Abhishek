package com.ilp.ilpschedule.utilities;

import android.util.Log;

import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.beans.ContactsBean;
import com.ilp.ilpschedule.beans.SlotBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by 1007546 on 24-10-2016.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static ArrayList<SlotBean> scheduleParser(String response){
        try {
            Log.d(TAG, "got some data from server");
            JSONObject jobj = new JSONObject(response);
            if (jobj.has("Android")) {
                JSONArray jarr = jobj.getJSONArray("Android");
                if (jarr.length() > 0) {
                    JSONObject obj;
                    ArrayList<SlotBean> data = new ArrayList<>();
                    SlotBean s;
                    for (int i = 0; i < jarr.length(); i++) {
                        s = new SlotBean();
                        obj = jarr.getJSONObject(i);
                        if (obj.has("course"))
                            s.setTitle(obj.getString("course"));
                        if (obj.has("room"))
                            s.setLocation(obj.getString("room"));
                        if (obj.has("slot"))
                            s.setSlot(obj.getString("slot"));
                        if (obj.has("batch"))
                            s.setBatch(obj.getString("batch"));
                        data.add(s);
                        System.out.println(s);
//                        Log.i(TAG, "SLOT TITLE: " + s.getTitle());
                    }

                    return data;

                } else {
                    // no schdule
                    Log.e(TAG, "Parser error: no schedule!");
                    return null;
                }
            }

        } catch (JSONException ex) {
            Log.d(TAG, ex.getLocalizedMessage());
        }
        Log.d(TAG, response);
        return null;
    }

    public static ArrayList<ContactsBean> contactsParser(String response){
        try {
            Log.d(TAG, "fetch contacts response ->" + response);
            JSONArray jarr = new JSONArray(response);
            JSONObject jobj;
            ArrayList<ContactsBean> contactsList = new ArrayList<>();
            ContactsBean contact;
            for (int i = 0; i < jarr.length(); i++) {
                jobj = jarr.getJSONObject(i);
                Iterator<String> it = jobj.keys();
                while (it.hasNext()) {
                    contact = new ContactsBean();
                    String title = it.next();
                    contact.setContactName(title);
                    contact.setContactNumber(jobj.getString(title));
                    Log.d(TAG, contact.toString());
                    contactsList.add(contact);
                }
            }

            return contactsList;


        } catch (JSONException ex) {
            Log.d(TAG, "error in json data" + ex.getLocalizedMessage());
        }
        return null;
    }
}
