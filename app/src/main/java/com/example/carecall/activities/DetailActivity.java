package com.example.carecall.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.carecall.R;
import com.example.carecall.databinding.ActivityDetailBinding;
import com.example.carecall.entity.DashboardData;
import com.example.carecall.entity.DoctorData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    DoctorData intent;
    DoctorData favourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = (DoctorData) getIntent().getSerializableExtra("doctor");

        binding.name.setText(intent.Name);
        binding.speciality.setText(intent.Special);
        binding.address.setText(intent.Address);
        binding.patiens.setText(intent.Patiens);
        binding.experience.setText(intent.getExperience().toString() + " Year");
        binding.bio.setText(intent.Biography);
        binding.rating.setText(intent.getRating().toString());
        if (intent.isFavourite) {
            binding.likeBtn.setVisibility(View.GONE);
            binding.unlikeBtn.setVisibility(View.VISIBLE);
        } else {
            binding.likeBtn.setVisibility(View.VISIBLE);
            binding.unlikeBtn.setVisibility(View.GONE);

        }
        Glide.with(this).load(intent.Picture).error(R.drawable.doctor).into(binding.doctorImg);

        // listeners

        binding.backBtn.setOnClickListener(v -> finish());
        binding.appointmentBtn.setOnClickListener(v -> {
            Intent startIntent = new Intent(this, BookAppointmentActivity.class);
            startIntent.putExtra("selectedDoctor", intent);
            startActivity(startIntent);
        });

        binding.likeBtn.setOnClickListener(v -> {
            addToWhishList();
        });
        binding.unlikeBtn.setOnClickListener(v -> {
            removeToWhishList(String.valueOf(favourites.id));
        });
    }

    private void removeToWhishList(String doctorId) {
        if (doctorId == null) {
            return;
        }
        binding.progress.setVisibility(View.VISIBLE);
        executor.execute(() -> {

            try {
                // Construct the URL for the DELETE request (use the doctor's Id to target the resource)
                URL url = new URL("https://67440f77b4e2e04abea090b8.mockapi.io/api/v1/getFavourites/" + doctorId);

                // Open a connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE"); // Set request method to DELETE
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode(); // Get the response code from the server
                Log.i("DeleteData", "Response Code: " + responseCode);

                // If the delete was successful (HTTP 204 No Content)
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Perform UI updates on the main thread
                    mainThreadHandler.post(() -> updateUI());
                } else {
                    // Handle failure, if needed
                    Log.e("DeleteData", "Failed to delete doctor. Response Code: " + responseCode);
                }

            } catch (Exception e) {
                mainThreadHandler.post(() -> hideProgress());
                Log.e("DeleteData", "Error deleting data", e);
            }
        });
    }

    private void updateUI() {
        intent.isFavourite = false;
        binding.progress.setVisibility(View.GONE);
        binding.unlikeBtn.setVisibility(View.GONE);
        binding.likeBtn.setVisibility(View.VISIBLE);
    }

    private void addToWhishList() {
        if (intent == null) {
            return;
        }
        intent.isFavourite = true;
        Gson gson = new Gson();
        String jsonInput = gson.toJson(intent);
        binding.progress.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {

                URL url = new URL("https://67440f77b4e2e04abea090b8.mockapi.io/api/v1/getFavourites");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Write the JSON input to the output stream
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.i("PostData", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    Log.i("PostData", "Response: " + result.toString());
                    String jsonData = result.toString();
                    mainThreadHandler.post(() -> parseAndLogJson(jsonData));
                }

            } catch (Exception e) {
                mainThreadHandler.post(() -> hideProgress());
                Log.e("PostData", "Error posting data", e);
            }
        });
    }

    private void hideProgress() {
        binding.progress.setVisibility(View.GONE);
    }

    private void parseAndLogJson(String jsonData) {
        try {
            Gson gson = new Gson();
           /* Type listType = new TypeToken<ArrayList<DoctorData>>() {
            }.getType();*/
            favourites = gson.fromJson(jsonData, DoctorData.class);
            binding.progress.setVisibility(View.GONE);
            binding.unlikeBtn.setVisibility(View.VISIBLE);
            binding.likeBtn.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e("FetchData", "Error parsing JSON", e);
        }
    }
}