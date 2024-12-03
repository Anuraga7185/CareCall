package com.example.carecall;

import android.app.Application;

public class MyApplication extends Application {
    private static final String APP_ID = "YOUR_APP_ID"; // Replace with your CometChat App ID
    private static final String REGION = "YOUR_REGION"; // Replace with your CometChat Region

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize CometChat
//        CometChat.init(this, APP_ID, REGION, new CometChat.CallbackListener<String>() {
//            @Override
//            public void onSuccess(String successMessage) {
//                // Initialization successful
//                System.out.println("CometChat initialized successfully");
//            }
//
//            @Override
//            public void onError(CometChatException e) {
//                // Initialization failed
//                e.printStackTrace();
//            }
//        });
    }
}
