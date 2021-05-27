package me.Superbility.SuperReport.Utils;

import java.util.UUID;

public class GenerateUUID {
    public static String getNewUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
}
