package com.allan.email_butler.model;

import com.allan.email_butler.util.Utils;

public class ScanEstimate {

    private int totalMessages;
    private int apiCallsRequired;
    private long estimatedSeconds;
    private String estimatedDuration;
    private String tip;

    public ScanEstimate(int totalMessages) {
        // Gmail API: 1 call to list 100 message IDs + 1 call per message for metadata
        // = totalMessages list calls (batched per 100) + totalMessages metadata calls
        // Realistic average: ~100ms per metadata call (network + quota)
        int listCalls = (int) Math.ceil(totalMessages / 100.0);
        this.totalMessages = totalMessages;
        this.apiCallsRequired = listCalls + totalMessages;
        this.estimatedSeconds = Math.round(totalMessages * 0.1); // ~100ms per message
        this.estimatedDuration = Utils.formatDuration(estimatedSeconds);
        this.tip = "Tip: call /api/gmail/summary?scanLimit=1000 to get a quick top-10 from your latest 1000 emails instead.";
    }

    public int getTotalMessages()    { return totalMessages; }
    public int getApiCallsRequired() { return apiCallsRequired; }
    public long getEstimatedSeconds(){ return estimatedSeconds; }
    public String getEstimatedDuration() { return estimatedDuration; }
    public String getTip()           { return tip; }
}
