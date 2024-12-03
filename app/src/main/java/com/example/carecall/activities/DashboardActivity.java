package com.example.carecall.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.carecall.R;
import com.example.carecall.adapter.GenericRecyclerAdapter;
import com.example.carecall.databinding.ChatListRowLayoutBinding;
import com.example.carecall.databinding.DashboardActivityBinding;
import com.example.carecall.databinding.DoctorRowBinding;
import com.example.carecall.databinding.DoctorSpecialistRowBinding;
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
    int whichBottomView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DashboardActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        whichBottomView = 0;

        setOnClickListeners();

        // Fetch Json Data From Server
        showDataOnScreen(whichBottomView);
        fetchHomeData();
    }

    private void setOnClickListeners() {
        binding.doctorSeeAll.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DoctorListActivity.class);
            intent.putExtra("doctors", dashboardDataList.get(0));
            Bundle bundle = new Bundle();
            bundle.putBoolean("isSpeciality", false);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        });
        binding.doctorSpecialitySeeAll.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DoctorListActivity.class);
            intent.putExtra("doctors", dashboardDataList.get(0));
            Bundle bundle = new Bundle();
            bundle.putBoolean("isSpeciality", true);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        });
        binding.home.setOnClickListener(v -> {
            if (whichBottomView != 0) {
                whichBottomView = 0;
                showDataOnScreen(0);
                fetchHomeData();
            }
        });
        binding.whishlist.setOnClickListener(v -> {
            if (whichBottomView != 1) {
                whichBottomView = 1;
                showDataOnScreen(1);
                fetchWishListData();
            }
        });
        binding.booking.setOnClickListener(v -> {
            if (whichBottomView != 2) {
                whichBottomView = 2;
                showDataOnScreen(2);
                fetchChatListData();
            }

        });
        binding.profile.setOnClickListener(v -> {
            if (whichBottomView != 3) {
                whichBottomView = 3;
                showDataOnScreen(3);
            }
        });
    }

    // To View Bookings
    private void fetchChatListData() {
        binding.bookingLoader.setVisibility(View.VISIBLE);
        binding.noDataBooking.setVisibility(View.GONE);
        executor.execute(() -> {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://674f657bbb559617b26f0d55.mockapi.io/api/v1/getBookings");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                String jsonData = result.toString();
                mainThreadHandler.post(() -> parseAndLogJsonBooking(jsonData));

            } catch (Exception e) {
                Log.e("FetchData", "Error fetching data", e);
            }
        });
    }

    private void parseAndLogJsonBooking(String jsonData) {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<DoctorData>>() {
            }.getType();
            List<DoctorData> doctorDataList = gson.fromJson(jsonData, listType);
            binding.noData.setVisibility(doctorDataList.isEmpty() ? View.VISIBLE : View.GONE);
            binding.chatList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.chatList.setAdapter(new GenericRecyclerAdapter<>(doctorDataList, R.layout.chat_list_row_layout, (view, item, position) -> {
                ChatListRowLayoutBinding listRowLayoutBinding = ChatListRowLayoutBinding.bind(view);
                Glide.with(this).load(item.Picture).error(R.drawable.doctor).into(listRowLayoutBinding.userImg);
                listRowLayoutBinding.userName.setText(item.Name);
                listRowLayoutBinding.getRoot().setOnClickListener(v -> {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("doctors", item);
                    startActivity(intent);
                });
            }));
            binding.bookingLoader.setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e("FetchData", "Error parsing JSON", e);
        }
    }

    // To View Wishlist
    private void fetchWishListData() {
        binding.whishlistLoader.setVisibility(View.VISIBLE);
        binding.noData.setVisibility(View.GONE);
        executor.execute(() -> {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://67440f77b4e2e04abea090b8.mockapi.io/api/v1/getFavourites");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                String jsonData = result.toString();
                mainThreadHandler.post(() -> parseAndLogJsonWishList(jsonData));

            } catch (Exception e) {
                Log.e("FetchData", "Error fetching data", e);
            }
        });
    }

    private void parseAndLogJsonWishList(String jsonData) {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<DoctorData>>() {
            }.getType();
            List<DoctorData> doctorDataList = gson.fromJson(jsonData, listType);
            binding.noData.setVisibility(doctorDataList.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerViewWishlist.setLayoutManager(new GridLayoutManager(this, 2));
            binding.recyclerViewWishlist.setAdapter(new GenericRecyclerAdapter<>(doctorDataList, R.layout.doctor_row, this::createRow));

            binding.whishlistLoader.setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e("FetchData", "Error parsing JSON", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (whichBottomView == 1) {
            showDataOnScreen(1);
            fetchWishListData();
        } else {
            showDataOnScreen(0);
            fetchHomeData();
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
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    // To View Home Screen
    private void fetchHomeData() {
        binding.loadPage.setVisibility(View.VISIBLE);
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
            doctorRowBinding.experience.setText(item.getExperience().toString() + " Year");
            Glide.with(this).load(item.Picture).error(R.drawable.doctor).into(doctorRowBinding.doctorImg);
            doctorRowBinding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(this, DetailActivity.class); // to start new activity
                intent.putExtra("doctor", item);
                startActivity(intent);
            });

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

    // Visibility on selected bottom view base--------------------------------------------------------------------------
    @SuppressLint("UseCompatLoadingForDrawables")
    private void showDataOnScreen(int position) {
        binding.home.setBackground(getDrawable(R.color.lightpurple));
        binding.whishlist.setBackground(getDrawable(R.color.lightpurple));
        binding.booking.setBackground(getDrawable(R.color.lightpurple));
        binding.profile.setBackground(getDrawable(R.color.lightpurple));

        binding.homeScreenContainer.setVisibility(View.GONE);
        binding.wishlistScreenContainer.setVisibility(View.GONE);
        binding.profileScreenContainer.setVisibility(View.GONE);
        binding.bookingScreenContainer.setVisibility(View.GONE);
        if (position == 1) {
            binding.whishlist.setBackground(getDrawable(R.drawable.curved_edit_box));
            binding.wishlistScreenContainer.setVisibility(View.VISIBLE);
        } else if (position == 2) {
            binding.booking.setBackground(getDrawable(R.drawable.curved_edit_box));
            binding.bookingScreenContainer.setVisibility(View.VISIBLE);
        } else if (position == 3) {
            binding.profile.setBackground(getDrawable(R.drawable.curved_edit_box));
            binding.profileScreenContainer.setVisibility(View.VISIBLE);
        } else {
            binding.home.setBackground(getDrawable(R.drawable.curved_edit_box));
            binding.homeScreenContainer.setVisibility(View.VISIBLE);
        }
    }
}
