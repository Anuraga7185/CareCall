package com.example.carecall;

import android.app.Application;

import com.example.carecall.entity.CurrentUser;


public class MyApplication extends Application {
    public static final String APP_ID = "267378a1e936f982";

    public static final String AUTH_KEY = "852a43a137a9872eb37afbe382066897ea00db3a";

    public static final String REGION = "IN";
    public static CurrentUser currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void setCurrentUser(CurrentUser user) {
        currentUser = user;
    }
}
