package com.example.carecall.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carecall.databinding.ActivitySplashScreenBinding;


public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(LoginScreenActivity.PREFS_NAME, MODE_PRIVATE);  // user credential are stored in this file

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        binding.moveBtnToDashboard.setOnClickListener(v -> {
            Log.d( "SplashScreen Activity: ", "SplashScreen Activity");
            startActivity(new Intent(this, LoginScreenActivity.class));
            finish();
        });
    }

    private boolean isUserLoggedIn() {
        String email = sharedPreferences.getString(LoginScreenActivity.KEY_EMAIL, null);
        String password = sharedPreferences.getString(LoginScreenActivity.KEY_PASSWORD, null);
        return email != null && password != null;
    }
}