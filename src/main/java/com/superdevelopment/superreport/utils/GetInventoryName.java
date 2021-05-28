package com.superdevelopment.superreport.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GetInventoryName {
    public static String getName(InventoryClickEvent e) {
        if(Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
            return e.getInventory().getName();
        } else {
            return e.getView().getTitle();
        }
    }
    public static String getName(InventoryCloseEvent e) {
        if(Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
            return e.getInventory().getName();
        } else {
            return e.getView().getTitle();
        }
    }
}
