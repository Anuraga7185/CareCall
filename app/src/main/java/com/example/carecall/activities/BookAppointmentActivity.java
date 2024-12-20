package com.example.carecall.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.carecall.R;
import com.example.carecall.adapter.GenericRecyclerAdapter;
import com.example.carecall.databinding.ActivityBookAppointmentBinding;
import com.example.carecall.databinding.DateRowBinding;
import com.example.carecall.entity.DoctorData;
import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class BookAppointmentActivity extends AppCompatActivity implements PaymentResultListener {
    ActivityBookAppointmentBinding binding;
    String startTime = "07:00 AM";
    String endTime = "4:00 PM";
    int selectedTime = 0;
    int selectedDate = 0;
    DoctorData intent;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBookAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = (DoctorData) getIntent().getSerializableExtra("selectedDoctor");
        if (intent != null) {
            Glide.with(this).load(intent.Picture).error(R.drawable.doctor).into(binding.doctorImg);
            binding.experience.setText(intent.getExperience() + "");
            binding.patiens.setText(intent.Patiens);
            binding.doctorName.setText(intent.Name);
            binding.doctorSpeciality.setText(intent.Special);
            binding.doctorSite.setText(intent.Address);
            binding.appointmentBtn.setText("Pay " + intent.getPrice());
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setAdapter(new GenericRecyclerAdapter<>(getListOfDates(), R.layout.date_row, (view, item, position) -> {
            DateRowBinding dateRowBinding = DateRowBinding.bind(view);
            dateRowBinding.day.setVisibility(View.VISIBLE);
            if (selectedDate == position) {
                dateRowBinding.container.setBackground(getDrawable(R.drawable.oval_purple_bg));
                dateRowBinding.time.setTextColor(getColor(android.R.color.white));
                dateRowBinding.day.setTextColor(getColor(android.R.color.white));

            } else {
                dateRowBinding.container.setBackground(getDrawable(R.drawable.curved_edit_box));
                dateRowBinding.time.setTextColor(getColor(android.R.color.black));
                dateRowBinding.day.setTextColor(getColor(android.R.color.black));
            }
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

            dateRowBinding.getRoot().setOnClickListener(v -> {
                selectedDate = position;
                Objects.requireNonNull(binding.recyclerView.getAdapter()).notifyDataSetChanged();
            });
        }));
        binding.appointmentBtn.setOnClickListener(v -> {
            String samount = intent.getPrice();

            // rounding off the amount.
            int amount = Math.round(Float.parseFloat(samount) * 100);

            // initialize Razorpay account.
            Checkout checkout = new Checkout();

            // set your id as below
            checkout.setKeyID("rzp_test_j2LAXIVp2bNJ4Z");
            // initialize json object
            JSONObject object = new JSONObject();
            try {
                // to put name
                object.put("name", "Anurag");
                // put description
                object.put("description", "Test payment");
                // to set theme color
                object.put("theme.color", "");
                // put the currency
                object.put("currency", "INR");
                // put amount
                object.put("amount", amount);
                // put mobile number
                object.put("prefill.contact", "7011341103");
                // put email
                object.put("prefill.email", "ranurag378@gmail.com");
                // open razorpay to checkout activity
                checkout.open(this, object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView2.setAdapter(new GenericRecyclerAdapter<>(generateTimeSlots(startTime, endTime), R.layout.date_row, (view, item, position) -> {
            DateRowBinding dateRowBinding = DateRowBinding.bind(view);
            if (selectedTime == position) {
                dateRowBinding.container.setBackground(getDrawable(R.drawable.oval_purple_bg));
                dateRowBinding.time.setTextColor(getColor(android.R.color.white));

            } else {
                dateRowBinding.container.setBackground(getDrawable(R.drawable.curved_edit_box));
                dateRowBinding.time.setTextColor(getColor(android.R.color.black));
            }
            dateRowBinding.time.setText(item);
            dateRowBinding.getRoot().setOnClickListener(v -> {
                selectedTime = position;
                binding.recyclerView2.getAdapter().notifyDataSetChanged();
            });
        }));
    }

    public List<String> generateTimeSlots(String startTime, String endTime) {
        List<String> timeSlots = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        try {
            // Parse start and end times
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(timeFormat.parse(startTime));

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(timeFormat.parse(endTime));

            // Generate time slots
            while (startCalendar.before(endCalendar) || startCalendar.equals(endCalendar)) {
                timeSlots.add(timeFormat.format(startCalendar.getTime()));
                startCalendar.add(Calendar.MINUTE, 30); // Add 30 minutes
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeSlots;
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

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
        addToWhishList();
        // Navigate to HomeActivity and clear back stack
        Intent intent = new Intent(this, DashboardActivity.class); // Replace HomeActivity with your actual activity class
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void addToWhishList() {
        if (intent == null) {
            return;
        }
        intent.isFavourite = true;
        Gson gson = new Gson();
        String jsonInput = gson.toJson(intent);
//        binding.progress.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {

                URL url = new URL("https://674f657bbb559617b26f0d55.mockapi.io/api/v1/getBookings");
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
//                    mainThreadHandler.post(() -> parseAndLogJson(jsonData));
                }

            } catch (Exception e) {
                //               mainThreadHandler.post(() -> hideProgress());
                Log.e("PostData", "Error posting data", e);
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
    }
}