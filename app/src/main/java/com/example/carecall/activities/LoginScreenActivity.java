package com.example.carecall.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carecall.MyApplication;
import com.example.carecall.databinding.ActivityLoginScreenBinding;
import com.example.carecall.entity.CurrentUser;
import com.example.carecall.entity.DoctorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginScreenActivity extends AppCompatActivity {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private ActivityLoginScreenBinding binding;
    public SharedPreferences sharedPreferences;
    public static final String PREFS_NAME = "UserPrefs";
    public static final String KEY_EMAIL = "userEmail";
    public static final String KEY_PASSWORD = "userPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
        } else {
            matchCredentials(userEmailText, userPasswordText);
        }
    }

    private void matchCredentials(String email, String passwd) {
        executor.execute(() -> {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://674f657bbb559617b26f0d55.mockapi.io/api/v1/getPatientDetails");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                String jsonData = result.toString();
                mainThreadHandler.post(() -> parseAndLogJsonBooking(jsonData, email, passwd));

            } catch (Exception e) {
                Log.e("FetchData", "Error fetching data", e);
            }
        });
    }

    private void parseAndLogJsonBooking(String response, String email, String passwd) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<CurrentUser>>() {
        }.getType();
        List<CurrentUser> currentUserList = gson.fromJson(response, listType);
        boolean isFound = false;
        CurrentUser loggedInUser = null;
        for (CurrentUser currentUser : currentUserList) {
            if (currentUser.email.equalsIgnoreCase(email) && currentUser.passwd.equals(passwd)) {
                isFound = true;
                loggedInUser = currentUser;
            }
        }
        if (isFound) {
            MyApplication.setCurrentUser(loggedInUser);
            saveUserCredentials(loggedInUser.email, loggedInUser.passwd);
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

    private void redirectToDashboard() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }
}