package com.example.carecall.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carecall.R;
import com.example.carecall.entity.DoctorData;

public class ChatActivity extends AppCompatActivity {
    DoctorData intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        intent = (DoctorData) getIntent().getSerializableExtra("doctors");



    }
}