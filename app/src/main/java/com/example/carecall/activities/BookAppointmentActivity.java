package com.example.carecall.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.carecall.R;
import com.example.carecall.adapter.GenericRecyclerAdapter;
import com.example.carecall.databinding.ActivityBookAppointmentBinding;
import com.example.carecall.databinding.DateRowBinding;
import com.example.carecall.entity.DoctorData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {
    ActivityBookAppointmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBookAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DoctorData intent = (DoctorData) getIntent().getSerializableExtra("selectedDoctor");
        if (intent != null) {
            Glide.with(this).load(intent.Picture).error(R.drawable.doctor).into(binding.doctorImg);
            binding.experience.setText(intent.getExperience() + "");
            binding.patiens.setText(intent.Patiens);
            binding.doctorName.setText(intent.Name);
            binding.doctorSpeciality.setText(intent.Special);
            binding.doctorSite.setText(intent.Address);

        }
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setAdapter(new GenericRecyclerAdapter<>(getListOfDates(), R.layout.date_row, (view, item, position) -> {
            DateRowBinding dateRowBinding = DateRowBinding.bind(view);

            // Get the current date
            Locale indiaLocaless = new Locale("en", "IN");
            DateTimeFormatter formatterss = DateTimeFormatter.ofPattern("EEE", indiaLocaless); // "EEE" gives abbreviated day name
            String abbreviatedDayName = item.format(formatterss).toUpperCase(); // Convert to uppercase

            dateRowBinding.day.setText(position == 0 ? "Today" : abbreviatedDayName + " ");

            Locale indiaLocales = new Locale("en", "IN"); // For India, you can also use Locale.US

            // Create a DateTimeFormatter to get the abbreviated month name (e.g., "NOV")
            DateTimeFormatter formatters = DateTimeFormatter.ofPattern("MMM", indiaLocales);
            String shortMonthName = item.format(formatters).toUpperCase();


            Locale indiaLocale = new Locale("en", "IN");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d", indiaLocale); // "d" gives only the day of the month
            String formattedDay = item.format(formatter);
            dateRowBinding.time.setText(formattedDay + " " + shortMonthName);
        }));

    }

    public List<LocalDate> getListOfDates() {
        List<LocalDate> datesList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i < 16; i++) {
            LocalDate localDate = currentDate.plusDays(i);
            datesList.add(localDate);
        }
        return datesList;
    }
}