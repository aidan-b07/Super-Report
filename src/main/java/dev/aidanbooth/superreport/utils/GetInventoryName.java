package dev.aidanbooth.superreport.utils;

import org.bukkit.event.inventory.InventoryEvent;

public class GetInventoryName {
    public static String getName(InventoryEvent e) {
        return e.getView().getTitle();
    }
}
