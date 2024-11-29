package com.example.carecall.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.carecall.R;
import com.example.carecall.adapter.GenericRecyclerAdapter;
import com.example.carecall.databinding.ActivityBookAppointmentBinding;
import com.example.carecall.databinding.DateRowBinding;
import com.example.carecall.entity.DoctorData;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class BookAppointmentActivity extends AppCompatActivity implements PaymentResultListener {
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
            binding.appointmentBtn.setText("Pay "+intent.getPrice());
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
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
    }
}