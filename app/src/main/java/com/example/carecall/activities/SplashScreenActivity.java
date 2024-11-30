package com.example.carecall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carecall.databinding.ActivitySplashScreenBinding;


public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        binding.moveBtnToDashboard.setOnClickListener(v -> {
            Log.d( "SplashScreen Activity: ", "SplashScreen Activity");
            startActivity(new Intent(this, LoginScreenActivity.class));
        });
    }
}