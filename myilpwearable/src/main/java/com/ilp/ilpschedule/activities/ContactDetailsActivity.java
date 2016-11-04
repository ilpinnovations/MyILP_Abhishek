package com.ilp.ilpschedule.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.beans.ContactsBean;

public class ContactDetailsActivity extends Activity {

    private TextView mNameTextView, mNumberTextView;
    private ContactsBean contact;

    private final static String TAG = ContactDetailsActivity.class.getSimpleName();
    private final static String TAG_CONTACT_NAME = "contactName";
    private final static String TAG_CONTACT_NUMBER = "contactNumber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        String contactName = null, contactNumber = null;

        if (intent != null){
            if (intent.hasExtra(TAG_CONTACT_NAME)){
                contactName = intent.getStringExtra(TAG_CONTACT_NAME);
            }else {
                Log.e(TAG, "Contact Name not received!");
            }

            if (intent.hasExtra(TAG_CONTACT_NUMBER)){
                contactNumber = intent.getStringExtra(TAG_CONTACT_NUMBER);
            }else {
                Log.e(TAG, "Contact Number not received!");
            }
        }else {
            Log.e(TAG, "Received a null intent!");
        }

        setContentView(R.layout.activity_contact_details);
        mNameTextView = (TextView) findViewById(R.id.contact_details_name);
        mNumberTextView = (TextView) findViewById(R.id.contact_details_number);

//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
//            }
//        });

        if (contactName != null && contactNumber != null){
            contact = new ContactsBean(contactName, contactNumber);
            mNameTextView.setText(contact.getContactName());
            mNumberTextView.setText(contact.getContactNumber());
        }else {
            Log.e(TAG, "Contacts name or number is null!");
        }
    }
}
