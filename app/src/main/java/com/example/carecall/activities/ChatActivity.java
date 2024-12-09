package com.example.carecall.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.TimeUtils;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carecall.MyApplication;
import com.example.carecall.R;
import com.example.carecall.adapter.GenericRecyclerAdapter;
import com.example.carecall.databinding.ActivityChatBinding;
import com.example.carecall.databinding.ChatRowLayoutBinding;
import com.example.carecall.entity.ChatMessage;
import com.example.carecall.entity.CurrentUser;
import com.example.carecall.entity.DoctorData;
import com.example.carecall.entity.FcmNotificationSender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {
    DoctorData intent;
    ActivityChatBinding binding;
    ArrayList<ChatMessage> messageArrayList = new ArrayList<>();
    DatabaseReference chatRef;
    CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = (DoctorData) getIntent().getSerializableExtra("doctors");
        currentUser = MyApplication.currentUser;
        String chatId = currentUser.uniqieId + "_" + intent.uniqueId;
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
        binding.sendButton.setOnClickListener(v -> {
            String message = binding.messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                String messageId = chatRef.push().getKey();
                ChatMessage chatMessage = new ChatMessage(currentUser.name, currentUser.uniqieId, intent.Name, intent.uniqueId, message, System.currentTimeMillis());
                chatRef.child("messages").child(messageId).setValue(chatMessage);

                messageArrayList.add(chatMessage);
                // Send notification in background
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    try {
                        FcmNotificationSender.sendNotification(
                                ChatActivity.this,
                                intent.fcmToken,
                                "CareCall",
                                chatMessage.message
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                executorService.shutdown();
                binding.messageEditText.setText("");
            }
        });
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.chatRecyclerView.setAdapter(new GenericRecyclerAdapter<>(messageArrayList, R.layout.chat_row_layout, (view, item, position) -> {
            ChatRowLayoutBinding binding1 = ChatRowLayoutBinding.bind(view);
            binding1.txtMessage.setText(item.message);

            long timestampMs = item.timestamp;
            // Convert to Date
            Date date = new Date(timestampMs);

            // Format the date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(date);
            binding1.time.setText(formattedDate);
            binding1.txtInfo.setText(item.senderName);
        }));
        loadChatMessages();

    }

    private void loadChatMessages() {
        chatRef.child("messages").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                    if (message != null) {
                        messageArrayList.add(message);
                    }
                }
                binding.chatRecyclerView.getAdapter().notifyDataSetChanged();
                if (!messageArrayList.isEmpty()) {
                    binding.chatRecyclerView.scrollToPosition(messageArrayList.size() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Chat", "Failed to load messages.", error.toException());
            }
        });
    }

}