package dev.aidanbooth.superreport.database;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Dates are worked out in Java rather than with SQL functions so SQLite and MySQL behave the same
public final class Timestamps {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 1.x stored permanent punishments with this expiry date
    static final String PERMANENT = "2000-01-01 00:00:00";
    private static final Timestamp PERMANENT_TIMESTAMP = Timestamp.valueOf(PERMANENT);

    private Timestamps() {
    }

    public static String now() {
        return LocalDateTime.now().format(FORMAT);
    }

    public static String secondsFromNow(long seconds) {
        return LocalDateTime.now().plusSeconds(seconds).format(FORMAT);
    }

    static Timestamp parse(String stored) {
        return stored == null ? null : Timestamp.valueOf(stored.trim());
    }

    static boolean isPermanent(Timestamp timestamp) {
        return PERMANENT_TIMESTAMP.equals(timestamp);
    }
}
