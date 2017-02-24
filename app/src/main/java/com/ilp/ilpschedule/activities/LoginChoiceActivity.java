package com.ilp.ilpschedule.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.util.Util;

public class LoginChoiceActivity extends AppCompatActivity {

    private AppCompatButton loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choice);

        loginBtn = (AppCompatButton) findViewById(R.id.login_btn);
        registerBtn = (AppCompatButton) findViewById(R.id.register_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginChoiceActivity.this, LoginActivity.class));
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginChoiceActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.checkLogin(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }
}
