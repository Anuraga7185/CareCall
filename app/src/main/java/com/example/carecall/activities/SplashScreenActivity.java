package com.example.carecall.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carecall.MyApplication;
import com.example.carecall.databinding.ActivitySplashScreenBinding;
import com.example.carecall.entity.CurrentUser;
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


public class SplashScreenActivity extends AppCompatActivity {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private SharedPreferences sharedPreferences;
    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(LoginScreenActivity.PREFS_NAME, MODE_PRIVATE);  // user credential are stored in this file

        // Check if user is already logged in
        isUserLoggedIn();

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        binding.moveBtnToDashboard.setOnClickListener(v -> {
            Log.d("SplashScreen Activity: ", "SplashScreen Activity");
            startActivity(new Intent(this, LoginScreenActivity.class));
            finish();
        });
    }

    private void isUserLoggedIn() {
        String email = sharedPreferences.getString(LoginScreenActivity.KEY_EMAIL, null);
        String password = sharedPreferences.getString(LoginScreenActivity.KEY_PASSWORD, null);
        matchCredentials(email, password);
    }

    private void matchCredentials(String email, String passwd) {
        binding.progressBar.setVisibility(View.VISIBLE);
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
                binding.progressBar.setVisibility(View.GONE);
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
            startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class));
            finish();
        }
        binding.progressBar.setVisibility(View.GONE);
    }
}