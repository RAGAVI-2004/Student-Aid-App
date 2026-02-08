package com.example.studentaid;

public class GuidanceMessage {
    private int messageId;
    private int senderId;
    private String senderName;
    private String senderRole; // "Student" or "Teacher"
    private String message;
    private String timestamp;
    private String audioPath;

    // Constructor
    public GuidanceMessage(int messageId, int senderId, String senderName,
                           String senderRole, String message, String timestamp, String audioPath) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.message = message;
        this.timestamp = timestamp;
        this.audioPath = audioPath;
    }

    // Getters
    public int getMessageId() { return messageId; }
    public int getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getSenderRole() { return senderRole; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public String getAudioPath() { return audioPath; }

    // Setters
    public void setMessageId(int messageId) { this.messageId = messageId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setAudioPath(String audioPath) { this.audioPath = audioPath; }
}
