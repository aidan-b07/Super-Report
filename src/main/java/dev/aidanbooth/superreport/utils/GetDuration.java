package dev.aidanbooth.superreport.utils;


public final class GetDuration {
    public static final int PERMANENT = -1;

    // Same unit lengths as the duration menu's buttons, where a month is 31 days
    private static final long[] UNIT_SECONDS = {31536000, 2678400, 604800, 86400, 3600, 60, 1};
    private static final String[] UNIT_NAMES = {"year", "month", "week", "day", "hour", "minute", "second"};

    private GetDuration() {
    }

    // 93784 -> "1 day, 2 hours, 3 minutes", -1 -> "Permanent"
    public static String format(long seconds) {
        return format(seconds, 3);
    }

    // Shows the largest unit and the next (maxUnits - 1) after it, skipping any that are zero
    public static String format(long seconds, int maxUnits) {
        if (seconds == PERMANENT) return "Permanent";
        if (seconds <= 0) return "0 seconds";

        int first = 0;
        while (seconds < UNIT_SECONDS[first]) first++;

        StringBuilder out = new StringBuilder();
        long left = seconds;
        for (int i = first; i < UNIT_SECONDS.length && i < first + maxUnits; i++) {
            long amount = left / UNIT_SECONDS[i];
            left -= amount * UNIT_SECONDS[i];
            if (amount == 0) continue;
            if (out.length() > 0) out.append(", ");
            out.append(amount).append(' ').append(UNIT_NAMES[i]).append(amount == 1 ? "" : "s");
        }
        return out.toString();
    }

    // "3 hours ago" for a time in epoch milliseconds
    public static String ago(long millis) {
        long seconds = (System.currentTimeMillis() - millis) / 1000;
        return seconds < 60 ? "just now" : format(seconds, 1) + " ago";
    }

    // Time left until expiresAt (epoch milliseconds), null means permanent
    public static String until(Long expiresAt) {
        if (expiresAt == null) return "Permanent";
        long millisLeft = expiresAt - System.currentTimeMillis();
        // Round up so a ban with half a second left doesn't read "0 seconds"
        return format(Math.max(0, (millisLeft + 999) / 1000));
    }
}
