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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.carecall.R;
import com.example.carecall.adapter.GenericRecyclerAdapter;
import com.example.carecall.databinding.ActivityDoctorListBinding;
import com.example.carecall.databinding.DoctorRowBinding;
import com.example.carecall.databinding.DoctorSpecialistRowBinding;
import com.example.carecall.entity.CategoryData;
import com.example.carecall.entity.DashboardData;
import com.example.carecall.entity.DoctorData;

import java.util.ArrayList;
import java.util.List;

public class DoctorListActivity extends AppCompatActivity {
    ActivityDoctorListBinding binding;
    List<DoctorData> showingList = new ArrayList<>();
    List<CategoryData> categoryData = new ArrayList<>();
    DashboardData dashboardData;
    boolean isSpeciality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        dashboardData = (DashboardData) intent.getSerializableExtra("doctors");
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            isSpeciality = bundle.getBoolean("isSpeciality");
        }
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, isSpeciality ? 5 : 2));
        if (dashboardData != null) {
            showingList = dashboardData.Doctors;
            categoryData = dashboardData.Category;
            if (isSpeciality) {
                binding.recyclerView.setAdapter(new GenericRecyclerAdapter<>(categoryData, R.layout.doctor_specialist_row, this::createCategoryRow));
            } else {
                binding.recyclerView.setAdapter(new GenericRecyclerAdapter<>(showingList, R.layout.doctor_row, this::createRow));
            }

            binding.search.clearFocus();
            binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (isSpeciality) {
                        List<CategoryData> filteredList = new ArrayList<>();
                        for (CategoryData data : dashboardData.Category) {
                            if (data.Name.toLowerCase().contains(newText.toLowerCase())) {
                                filteredList.add(data);
                            }
                        }
                        binding.recyclerView.setAdapter(new GenericRecyclerAdapter<>(filteredList, R.layout.doctor_specialist_row, DoctorListActivity.this::createCategoryRow));

                    } else {
                        List<DoctorData> filteredList = new ArrayList<>();
                        for (DoctorData data : dashboardData.Doctors) {
                            if (data.Name.toLowerCase().contains(newText.toLowerCase())) {
                                filteredList.add(data);
                            }
                        }

                        binding.recyclerView.setAdapter(new GenericRecyclerAdapter<>(filteredList, R.layout.doctor_row, DoctorListActivity.this::createRow));
                    }
                    return false;
                }

            });

        }

    }

    private void createRow(View view, DoctorData item, int i) {

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

    private void createCategoryRow(View view, CategoryData item, int i) {
        DoctorSpecialistRowBinding doctorSpecialistRowBinding = DoctorSpecialistRowBinding.bind(view);
        doctorSpecialistRowBinding.specialistName.setText(item.Name);
        Glide.with(this).load(item.Picture).into(doctorSpecialistRowBinding.image4);
        // need to show doctors with selected category Only
    }

}