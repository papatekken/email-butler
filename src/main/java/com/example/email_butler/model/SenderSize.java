package com.example.email_butler.model;


import com.example.email_butler.util.Utils;

public class SenderSize {
    private String sender;
    private long totalBytes;
    private String totalSize;

    public SenderSize(String sender, long totalBytes) {
        this.sender = sender;
        this.totalBytes = totalBytes;
        this.totalSize = Utils.formatBytes(totalBytes);
    }


    public String getSender()    { return sender; }
    public long getTotalBytes()  { return totalBytes; }
    public String getTotalSize() { return totalSize; }
}
