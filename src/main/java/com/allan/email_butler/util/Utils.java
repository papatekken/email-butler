package com.allan.email_butler.util;

public class Utils {

    private Utils() {}  // ← prevents new Utils()

    public static String formatBytes(long bytes) {
        if (bytes >= 1_073_741_824)
            return String.format("%.1f GB", bytes / 1_073_741_824.0);
        else if (bytes >= 1_048_576)
            return String.format("%.1f MB", bytes / 1_048_576.0);
        else if (bytes >= 1_024)
            return String.format("%.1f KB", bytes / 1_024.0);
        else
            return bytes + " B";
    }

    public static String formatDuration(long totalSeconds) {
        if (totalSeconds < 60) {
            return totalSeconds + " seconds";
        } else if (totalSeconds < 3600) {
            long mins = totalSeconds / 60;
            long secs = totalSeconds % 60;
            return mins + " min " + secs + " sec";
        } else {
            long hrs  = totalSeconds / 3600;
            long mins = (totalSeconds % 3600) / 60;
            return hrs + " hr " + mins + " min";
        }
    }
}