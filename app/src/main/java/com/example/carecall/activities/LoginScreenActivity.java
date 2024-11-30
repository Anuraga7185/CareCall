package com.example.carecall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carecall.R;
import com.example.carecall.databinding.ActivityLoginScreenBinding;

public class LoginScreenActivity extends AppCompatActivity {

    private ActivityLoginScreenBinding binding;
    String userEmail = "adityaas9350@gmail.com";
    String userPassword = "Aditya@1234";
    String userEmailText, userPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       binding.btnSignIn.setOnClickListener(v -> {
           loginMethod();
       });
      binding.tvSignUp.setOnClickListener(v -> {
          openSignUpScreen();
      });

    }

    private void openSignUpScreen() {
        startActivity(new Intent(this, SignUpScreenActivity.class));
    }

    private void loginMethod() {
        userEmailText = binding.etEmail.getText().toString().trim();
        userPasswordText = binding.etPassword.getText().toString().trim();
        if (userEmailText.isEmpty()) {
            binding.etEmail.setError("Please enter an email.");
            binding.etEmail.requestFocus();
        } else if (userPasswordText.isEmpty()) {
            binding.etPassword.setError("Please enter an password.");
            binding.etPassword.requestFocus();
        } else if (userEmail.equalsIgnoreCase(userEmailText) && userPassword.equalsIgnoreCase(userPasswordText)) {
                startActivity(new Intent(this, DashboardActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Username or Password!",
                    Toast.LENGTH_LONG).show();
        }
    }
}