package dev.aidanbooth.superreport;

import org.bukkit.entity.Player;

import java.util.*;

public class GetValues {

    /////////////////////////REPORT HASHMAPS////////////////////////////////
    //HASHMAP<PLAYER,UUID> chosenReport
    public static Map<Player, UUID> chosenReport = new LinkedHashMap<Player,UUID>();
    public static Map getChosenReport() {return chosenReport;}
    public static void addChosenReport(Player player, UUID uuid) {chosenReport.put(player,uuid);}
    public static void removeChosenReport(Player player) {chosenReport.remove(player);}

    //HASHMAP<PLAYER,STRING> punishmentType
    public static Map<Player, String> punishmentType = new LinkedHashMap<>();
    public static Map getPunishmentType() {return punishmentType;}
    public static void addPunishmentType(Player player, String type) {punishmentType.put(player,type);}
    public static void removePunishmentType(Player player) {chosenReport.remove(player);}

    //HASHMAP<PLAYER,INT> time
    public static Map<Player, Integer> time = new LinkedHashMap<>();
    public static Map getTime() {return time;}
    public static void addTime(Player player, Integer newTime) {time.put(player, newTime);}
    public static void removeTime(Player player) {time.remove(player);}

    //HASHMAP<PLAYER,UUID> punishingPlayer
    public static Map<Player, UUID> punishingPlayer = new LinkedHashMap<>();
    public static Map getPunishingPlayer() {return punishingPlayer;}
    public static void addPunishingPlayer(Player player, UUID newUuid) {punishingPlayer.put(player, newUuid);}
    public static void removePunishingPlayer(Player player) {punishingPlayer.remove(player);}
}
