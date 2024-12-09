package com.example.carecall.entity;

import android.content.Context;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FcmNotificationSender {
    public static void sendNotification(Context context, String fcmToken, String title, String body) throws Exception {
        // Get Bearer Token
        String accessToken = FirebaseAuthUtil.getAccessToken(context);

        // FCM API URL
        String url = "https://fcm.googleapis.com/v1/projects/carecall-d28ba/messages:send";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        // Set Headers
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Content-Type", "application/json");

        // JSON Payload
        String payload = "{"
                + "\"message\": {"
                + "\"token\": \"" + fcmToken + "\","
                + "\"notification\": {"
                + "\"title\": \"" + title + "\","
                + "\"body\": \"" + body + "\""
                + "}"
                + "}"
                + "}";

        // Send Request
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.write(payload.getBytes());
        os.flush();
        os.close();

        // Get Response Code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
    }
}
