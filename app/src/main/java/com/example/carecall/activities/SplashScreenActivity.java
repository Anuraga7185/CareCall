package com.example.carecall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


import com.example.carecall.MainActivity;
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
            startActivity(new Intent(this, DashboardActivity.class));
        });
    }
}