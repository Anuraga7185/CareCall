package com.example.carecall.entity;

public class ChatMessage {
    public String senderId;
    public String senderName;
    public String receiverName;
    public String receiverId;
    public String message;
    public long timestamp;
    public ChatMessage() {
    }
    public ChatMessage(String sender, String senderId, String receiverName, String receiverId, String message, long timestamp) {
        this.senderId = senderId;
        this.senderName = sender;
        this.message = message;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.timestamp = timestamp;
    }
}
