package com.example.carecall.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carecall.R;
import com.example.carecall.databinding.ActivityBookAppointmentBinding;
import com.example.carecall.databinding.ActivityDetailBinding;
import com.example.carecall.entity.DoctorData;

public class BookAPpointmentActivity extends AppCompatActivity {
    ActivityBookAppointmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBookAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DoctorData intent = (DoctorData) getIntent().getSerializableExtra("selectedDoctor");


    }
}