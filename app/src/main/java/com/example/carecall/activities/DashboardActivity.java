package com.example.carecall.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carecall.MainActivity;
import com.example.carecall.databinding.DashboardActivityBinding;

public class DashboardActivity extends AppCompatActivity {
    DashboardActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DashboardActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setOnClickListeners();
    }
    private void setOnClickListeners() {

    }
}
