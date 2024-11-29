package com.example.carecall.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carecall.databinding.ActivityBookAppointmentBinding;
import com.example.carecall.entity.DoctorData;

public class BookAppointmentActivity extends AppCompatActivity {
    ActivityBookAppointmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBookAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DoctorData intent = (DoctorData) getIntent().getSerializableExtra("selectedDoctor");


    }
}