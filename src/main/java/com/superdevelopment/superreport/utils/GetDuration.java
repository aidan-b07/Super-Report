package com.superdevelopment.superreport.utils;

import java.sql.Timestamp;
import java.util.Date;

public class GetDuration {
    public static String getDuration(Date a, Timestamp b) {
        Date d1;
        Timestamp d2;
        try {
            d1 = a;
            d2 = b;

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            return diffDays + " days, " + diffHours + " hours, " + diffMinutes + " minutes, " + diffSeconds + " seconds.";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getDuration(int seconds) {
        try {

            long numberOfDays = seconds / 86400;
            long numberOfHours = (seconds % 86400 ) / 3600;
            long numberOfMinutes = (seconds % 3600 ) / 60;
            long numberOfSeconds = ((seconds % 86400 ) % 3600 ) % 60;

            return numberOfDays + " days, " + numberOfHours + " hours, " + numberOfMinutes + " minutes, " + numberOfSeconds + " seconds.";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
