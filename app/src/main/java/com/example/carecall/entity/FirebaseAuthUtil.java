package com.example.carecall.entity;

import android.content.Context;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FirebaseAuthUtil {
    public static String getAccessToken(Context context) throws IOException {
        String jsonKey = loadJSONFromAsset(context, "serviceAccountKey.json");

        // Convert JSON string to InputStream
        InputStream serviceAccountStream = new ByteArrayInputStream(jsonKey.getBytes());

        // Initialize GoogleCredentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform");
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }

    public static String loadJSONFromAsset(Context context, String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        return new String(buffer, "UTF-8");
    }
}
