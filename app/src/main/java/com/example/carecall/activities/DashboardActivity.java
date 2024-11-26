package com.example.carecall.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.carecall.R;
import com.example.carecall.adapter.GenericRecyclerAdapter;
import com.example.carecall.databinding.DashboardActivityBinding;
import com.example.carecall.databinding.DoctorRowBinding;
import com.example.carecall.databinding.DoctorSpecialistRowBinding;
import com.example.carecall.entity.CategoryData;
import com.example.carecall.entity.DashboardData;
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

public class DashboardActivity extends AppCompatActivity {
    DashboardActivityBinding binding;
    List<DashboardData> dashboardDataList = new ArrayList<>();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DashboardActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setOnClickListeners();

        // Fetch Json Data From Server
        fetchData();
    }

    private void setOnClickListeners() {

    }


    private void fetchData() {
        executor.execute(() -> {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://67440f77b4e2e04abea090b8.mockapi.io/api/v1/getDashboardData");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                String jsonData = result.toString();
                mainThreadHandler.post(() -> parseAndLogJson(jsonData));

            } catch (Exception e) {
                Log.e("FetchData", "Error fetching data", e);
            }
        });
    }

    public void dataToView() {
        if (dashboardDataList.isEmpty() || dashboardDataList.get(0).Category.isEmpty()) {
            return;
        }
        binding.doctorListHeader.setVisibility(View.VISIBLE);
        binding.doctorSpecialistHeader.setVisibility(View.VISIBLE);
        binding.doctorSpecialistData.setVisibility(View.VISIBLE);
        binding.doctorListHeaderData.setVisibility(View.VISIBLE);
        binding.doctorSpecialistList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.doctorSpecialistList.setAdapter(new GenericRecyclerAdapter<>(dashboardDataList.get(0).Category, R.layout.doctor_specialist_row, (view, item, position) -> {
            DoctorSpecialistRowBinding doctorSpecialistRowBinding = DoctorSpecialistRowBinding.bind(view);
            doctorSpecialistRowBinding.specialistName.setText(item.Name);
            Glide.with(this).load(item.Picture).into(doctorSpecialistRowBinding.image4);
        }));

        binding.doctorList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.doctorList.setAdapter(new GenericRecyclerAdapter<>(dashboardDataList.get(0).Doctors, R.layout.doctor_row, (view, item, position) -> {
            DoctorRowBinding doctorRowBinding = DoctorRowBinding.bind(view);
            doctorRowBinding.name.setText(item.Name);
            doctorRowBinding.specialist.setText(item.Special);
            doctorRowBinding.ratings.setText(item.getRating().toString());
            doctorRowBinding.experience.setText(item.getExperience().toString()+" Year");
            Glide.with(this).load(item.Picture).error(R.drawable.doctor).into(doctorRowBinding.doctorImg);

        }));
    }

    private void parseAndLogJson(String jsonData) {
        try {

            Gson gson = new Gson();
            Type listType = new TypeToken<List<DashboardData>>() {
            }.getType();
            dashboardDataList = gson.fromJson(jsonData, listType);
            binding.loadPage.setVisibility(View.GONE);
            this.dataToView();
        } catch (Exception e) {
            Log.e("FetchData", "Error parsing JSON", e);
        }
    }
}
