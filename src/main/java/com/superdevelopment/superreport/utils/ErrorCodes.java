package com.superdevelopment.superreport.utils;

import org.bukkit.ChatColor;

public class ErrorCodes {
    public static String getTemplate(String error) {
        return ChatColor.translateAlternateColorCodes('&',
                "\n&9&l------------[Super-Report]------------\n" +
                        "&c   There was an error in the plugin!            \n" +
                        "&c             Error Code - " + error + "         \n" +
                        "&1     Join the discord for extra help            \n" +
                        "&1        https://discord.gg/3wqbfmX74P              \n" +
                        "&9&l-----------------------------------");
    }
    public static String getDisableMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                "\n&9&l------------[Super-Report]------------\n" +
                        "&c    There was a fatal error while enabling the   \n" +
                        "&c             plugin! Error code - 999            \n" +
                        "&c              Disabling SuperReport              \n" +
                        "&9&l-----------------------------------");
    }
    public static String generalMessage() {
        return ChatColor.translateAlternateColorCodes('&', "&cThere was an error while attempting this! Check the console for more details.");
    }

    //Error code 001 - Error while adding report to active reports                                              ==ReportCommand==
    //Error code 002 - Error while adding report to player data                                                 ==ReportCommand==
    //Error code 003 - Error while adding reported to player data                                               ==ReportCommand==
    //Error code 004 - Error while checking if report already exists                                            ==ReportCommand==
    //Error code 005 - Error while checking if reported player is online                                        ==ReportCommand==
    //Error code 006 - Error while checking if player exists in table 'playerdata'                              ==AddPlayerdata==
    //Error code 007 - Error while creating player data in db                                                   ==AddPlayerdata==
    //Error code 008 - Error while getting data for reports menu                                                ==ReportsCommand==



}
