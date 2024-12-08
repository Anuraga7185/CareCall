package com.example.carecall.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.TimeUtils;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carecall.R;
import com.example.carecall.adapter.GenericRecyclerAdapter;
import com.example.carecall.databinding.ActivityChatBinding;
import com.example.carecall.databinding.ChatRowLayoutBinding;
import com.example.carecall.entity.ChatMessage;
import com.example.carecall.entity.DoctorData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    DoctorData intent;
    ActivityChatBinding binding;
    ArrayList<ChatMessage> messageArrayList = new ArrayList<>();
    DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = (DoctorData) getIntent().getSerializableExtra("doctors");

        String chatId = "aditya" + "_" + intent.Mobile;
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
        binding.sendButton.setOnClickListener(v -> {
            String message = binding.messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                String messageId = chatRef.push().getKey();
                ChatMessage chatMessage = new ChatMessage("aditya", "aditya", intent.Name, intent.Mobile, message, System.currentTimeMillis());
                chatRef.child("messages").child(messageId).setValue(chatMessage);

                messageArrayList.add(chatMessage);
                binding.messageEditText.setText("");
            }
        });
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.chatRecyclerView.setAdapter(new GenericRecyclerAdapter(messageArrayList, R.layout.chat_row_layout, (view, item, position) -> {
            ChatMessage chatMessage = (ChatMessage) item;
            ChatRowLayoutBinding binding1 = ChatRowLayoutBinding.bind(view);
            binding1.txtMessage.setText(chatMessage.message);

            long timestampMs = chatMessage.timestamp;

            // Convert to Date
            Date date = new Date(timestampMs);

            // Format the date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(date);
            binding1.time.setText(formattedDate);
            binding1.txtInfo.setText(chatMessage.senderName);
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