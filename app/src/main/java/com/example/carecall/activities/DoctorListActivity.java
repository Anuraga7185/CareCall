package com.example.carecall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.carecall.R;
import com.example.carecall.adapter.GenericRecyclerAdapter;
import com.example.carecall.databinding.ActivityDoctorListBinding;
import com.example.carecall.databinding.DoctorRowBinding;
import com.example.carecall.entity.DashboardData;
import com.example.carecall.entity.DoctorData;

import java.util.ArrayList;
import java.util.List;

public class DoctorListActivity extends AppCompatActivity {
    ActivityDoctorListBinding binding;
    List<DoctorData> doctorDataList = new ArrayList<>();
    List<DoctorData> showingList = new ArrayList<>();
    DashboardData dashboardData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        dashboardData = (DashboardData) intent.getSerializableExtra("doctors");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (dashboardData != null) {
            doctorDataList = dashboardData.Doctors;
            showingList = dashboardData.Doctors;
            binding.recyclerView.setAdapter(new GenericRecyclerAdapter<>(showingList, R.layout.doctor_row, this::createRow));
            binding.search.clearFocus();
            binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    List<DoctorData> filteredList = new ArrayList<>();
                    for (DoctorData data : dashboardData.Doctors) {
                        if (data.Name.toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(data);
                        }
                    }

                    binding.recyclerView.setAdapter(new GenericRecyclerAdapter<>(filteredList, R.layout.doctor_row, DoctorListActivity.this::createRow));

                    return false;
                }

            });

        }

    }

    private void createRow(View view, DoctorData item, int i) {
        Log.d("Adding DoctorListActivity", "DoctorDataList Size: " + doctorDataList.size());

        DoctorRowBinding doctorRowBinding = DoctorRowBinding.bind(view);
        doctorRowBinding.name.setText(item.Name);
        doctorRowBinding.specialist.setText(item.Special);
        doctorRowBinding.ratings.setText(item.getRating().toString());
        doctorRowBinding.experience.setText(item.getExperience().toString() + " Year");
        Glide.with(this).load(item.Picture).error(R.drawable.doctor).into(doctorRowBinding.doctorImg);
        doctorRowBinding.getRoot().setOnClickListener(v -> {
            Intent doctorIntent = new Intent(this, DetailActivity.class); // to start new activity
            doctorIntent.putExtra("doctor", item);
            startActivity(doctorIntent);
        });

    }
}