package com.example.carecall.activities;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.carecall.R;
import com.example.carecall.databinding.ActivityDetailBinding;
import com.example.carecall.entity.DoctorData;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DoctorData intent = (DoctorData) getIntent().getSerializableExtra("doctor");

        binding.name.setText(intent.Name);
        binding.speciality.setText(intent.Special);
        binding.address.setText(intent.Site);
        binding.patiens.setText(intent.Patiens);
        binding.experience.setText(intent.getExperience().toString() + " Year");
        binding.bio.setText(intent.Biography);
        binding.rating.setText(intent.getRating().toString());
        Glide.with(this).load(intent.Picture).error(R.drawable.doctor).into(binding.doctorImg);

        // listeners

        binding.backBtn.setOnClickListener(v -> finish());
        binding.appointmentBtn.setOnClickListener(v -> {
            Intent startIntent = new Intent(this, BookAppointmentActivity.class);
            startIntent.putExtra("selectedDoctor", intent);
            startActivity(startIntent);
        });
    }
}