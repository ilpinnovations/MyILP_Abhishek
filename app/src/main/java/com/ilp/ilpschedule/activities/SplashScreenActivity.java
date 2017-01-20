package com.ilp.ilpschedule.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.app_intro.Intro;

/**
 * Created by abhi on 6/20/2016.
 */

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(SplashScreenActivity.this, Intro.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                    finish();
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int SPLASH_TIME_OUT = 2000;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }, SPLASH_TIME_OUT);
                        }
                    });
                }
            }
        });

        // Start the thread
        t.start();

    }
}
