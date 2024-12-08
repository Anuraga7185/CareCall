package com.example.carecall.activities;

import android.content.Intent;
import android.database.CursorWindowAllocationException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carecall.R;
import com.example.carecall.databinding.ActivitySignUpScreenBinding;
import com.example.carecall.entity.CurrentUser;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SignUpScreenActivity extends AppCompatActivity {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private ActivitySignUpScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSignUp.setOnClickListener(v -> {
            // Redirect to Login Screen
            createUser();

        });

        binding.tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpScreenActivity.this, LoginScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

    }

    private void createUser() {
        CurrentUser currentUser = new CurrentUser();
        currentUser.name = binding.name.getText().toString();
        currentUser.email = binding.email.getText().toString();
        currentUser.passwd = binding.passwd.getText().toString();
        currentUser.uniqieId = binding.uniqueId.getText().toString();
        Gson gson = new Gson();
        String jsonInput = gson.toJson(currentUser);
        executor.execute(() -> {
            try {

                URL url = new URL("https://674f657bbb559617b26f0d55.mockapi.io/api/v1/getPatientDetails");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Write the JSON input to the output stream
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                if (conn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                    openPage();
                }

            } catch (Exception e) {
                Log.e("PostData", "Error posting data", e);
            }
        });
    }

    private void openPage() {
        Intent intent = new Intent(SignUpScreenActivity.this, LoginScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}