package com.example.carecall.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carecall.R;
import com.example.carecall.databinding.ActivityLoginScreenBinding;

public class LoginScreenActivity extends AppCompatActivity {

    private ActivityLoginScreenBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_PASSWORD = "userPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);  // user credential are stored in this file

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            redirectToDashboard();
        }

        binding.btnSignIn.setOnClickListener(v -> loginMethod());
        binding.tvSignUp.setOnClickListener(v -> openSignUpScreen());

    }

    private void openSignUpScreen() {
        startActivity(new Intent(this, SignUpScreenActivity.class));
    }

    private void loginMethod() {
        String userEmailText = binding.etEmail.getText().toString().trim();
        String userPasswordText = binding.etPassword.getText().toString().trim();

        if (userEmailText.isEmpty()) {
            binding.etEmail.setError("Please enter an email.");
            binding.etEmail.requestFocus();
        } else if (userPasswordText.isEmpty()) {
            binding.etPassword.setError("Please enter a password.");
            binding.etPassword.requestFocus();
        } else if (userEmailText.equalsIgnoreCase("adityaas9350@gmail.com") && userPasswordText.equals("Aditya@1234")) {
            saveUserCredentials(userEmailText, userPasswordText);
            redirectToDashboard();
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Username or Password!", Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        String email = sharedPreferences.getString(KEY_EMAIL, null);
        String password = sharedPreferences.getString(KEY_PASSWORD, null);
        return email != null && password != null;
    }

    private void redirectToDashboard() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }
}