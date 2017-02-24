package com.ilp.ilpschedule.app_intro;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.activities.LoginChoiceActivity;
import com.ilp.ilpschedule.activities.RegisterActivity;

/**
 * Created by 1115394 on 1/17/2017.
 */
public class Intro extends AppIntro {
    private static final String TAG = Intro.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance("MyILP", "Initial Learning Program is a program designed to train recently joined associates or new recruits. MyILP makes ILP simple & fun.", R.drawable.ic_launcher_large, getResources().getColor(R.color.peter_river)));
        addSlide(AppIntroFragment.newInstance("MyILP", getResources().getString(R.string.app_desc), R.drawable.ic_launcher_large, getResources().getColor(R.color.colorPrimary)));
        addSlide(SampleSlide.newInstance(R.layout.slide_features));

        addSlide(AppIntroFragment.newInstance("CAMERA", "Camera permission is required to click pictures natively.", R.drawable.permission_camera, getResources().getColor(R.color.amethyst)));
        addSlide(AppIntroFragment.newInstance("LOCATION", "Location permission is required to fetch GPS coordinates (Latitude & Longitude).", R.drawable.permission_location, getResources().getColor(R.color.alizarin)));
        addSlide(AppIntroFragment.newInstance("PHONE", "Phone permission is required to make calls.", R.drawable.permission_phone, getResources().getColor(R.color.wet_asphalt)));
        addSlide(AppIntroFragment.newInstance("SMS", "SMS permission is required to send messages from the application.", R.drawable.permission_message, getResources().getColor(R.color.midnight_blue)));
        addSlide(AppIntroFragment.newInstance("GET STARTED", "You are all set. Enjoy MyILP", R.drawable.tick_icon, getResources().getColor(R.color.peter_river)));

        // OPTIONAL METHODS

        // SHOW or HIDE the statusbar
        showStatusBar(false);
        showSkipButton(false);


//
//        // Edit the color of the nav bar on Lollipop+ devices
//        setNavBarColor(R.color.nav_color);
//
//        // Turn vibration on and set intensity
//        // NOTE: you will need to ask VIBRATE permission in Manifest if you haven't already
//        setVibrate(true);
//        setVibrateIntensity(30);

        // Animations -- use only one of the below. Using both could cause errors.
//        setFadeAnimation(); // OR
//        setZoomAnimation(); // OR
        setFlowAnimation(); // OR
//        setSlideOverAnimation(); // OR
//        setDepthAnimation(); // OR
//        setCustomTransformer(yourCustomTransformer);

        // Permissions -- takes a permission and slide number
        askForPermissions(new String[]{Manifest.permission.CAMERA}, 4);
        askForPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 5);
        askForPermissions(new String[]{Manifest.permission.CALL_PHONE}, 6);
        askForPermissions(new String[]{Manifest.permission.SEND_SMS}, 7);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    @Override
    public void onSlideChanged() {
        super.onSlideChanged();
    }

    @Override
    public void onNextPressed() {
        super.onNextPressed();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(Intro.this, LoginChoiceActivity.class);
        startActivity(intent);
        finish();
    }
}
