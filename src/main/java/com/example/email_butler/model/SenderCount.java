package com.example.email_butler.model;

public class SenderCount {
    private String sender;
    private long count;

    public SenderCount(String sender, long count) {
        this.sender = sender;
        this.count = count;
    }

    public String getSender() { return sender; }
    public long getCount() { return count; }
}