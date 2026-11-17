package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.shared.NavBar;
import dev.aidanbooth.superreport.utils.GetDuration;
import dev.aidanbooth.superreport.utils.MenuItems;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

// How long a punishment lasts: add or take away time, pick a preset, or make it permanent
public class DurationInventory {
    public static final int[] UNIT_SECONDS = {60, 3600, 86400, 604800, 2678400};
    private static final String[] UNIT_NAMES = {"Minute", "Hour", "Day", "Week", "Month"};
    public static final int[] ADD_SLOTS = {10, 11, 12, 13, 14};
    public static final int[] REMOVE_SLOTS = {19, 20, 21, 22, 23};
    public static final int PERMANENT = 16;
    public static final int RESET = 25;

    public static final int[] PRESET_SLOTS = {28, 29, 30, 31, 32, 33, 34};
    public static final int[] PRESET_SECONDS = {86400, 259200, 604800, 1209600, 5356800, 16070400, 31536000};

    public Inventory create(InventoryHolder holder, PunishmentDraft data) {
        int duration = data.getDuration();
        boolean ban = data.getPunishmentType() == PunishmentType.BAN;
        String reported = data.getReportedName();
        String length = duration == 0 ? "Pick a length" : GetDuration.format(duration, 2);
        Inventory inventory = Bukkit.createInventory(holder, 54, MenuItems.title("&8" + (ban ? "Ban" : "Mute") + " » " + length, t -> t));

        inventory.setItem(4, MenuItems.head(reported, (ban ? "&c&lBanning &f" : "&6&lMuting &f") + reported,
                java.util.Arrays.asList(
                        "&7Reason: &f" + data.getReason(),
                        "&7Length: &f" + (duration == 0 ? "Not set yet" : GetDuration.format(duration)))));

        for (int i = 0; i < UNIT_SECONDS.length; i++) {
            String add = "&7Add one " + UNIT_NAMES[i].toLowerCase() + ".";
            inventory.setItem(ADD_SLOTS[i], duration == GetDuration.PERMANENT
                    ? MenuItems.item(XMaterial.LIME_WOOL, "&a&l+1 " + UNIT_NAMES[i], add, "&8Replaces permanent.")
                    : MenuItems.item(XMaterial.LIME_WOOL, "&a&l+1 " + UNIT_NAMES[i], add));
            inventory.setItem(REMOVE_SLOTS[i], MenuItems.item(XMaterial.RED_WOOL, "&c&l-1 " + UNIT_NAMES[i],
                    "&7Take away one " + UNIT_NAMES[i].toLowerCase() + "."));
        }
        inventory.setItem(PERMANENT, MenuItems.item(XMaterial.NETHER_STAR, "&4&lPermanent",
                duration == GetDuration.PERMANENT ? "&a» Currently selected" : "&7Never expires.",
                duration == GetDuration.PERMANENT ? "&7Click again to turn it off." : "&7Click to make it permanent."));
        inventory.setItem(RESET, MenuItems.item(XMaterial.WATER_BUCKET, "&e&lReset",
                "&7Clear the length and", "&7start again."));

        for (int i = 0; i < PRESET_SLOTS.length; i++) {
            String name = "&b&l" + GetDuration.format(PRESET_SECONDS[i]);
            inventory.setItem(PRESET_SLOTS[i], duration == PRESET_SECONDS[i]
                    ? MenuItems.item(XMaterial.CLOCK, name, "&a» Currently selected")
                    : MenuItems.item(XMaterial.CLOCK, name, "&7Set the length to exactly", "&7" + GetDuration.format(PRESET_SECONDS[i]) + "."));
        }

        String confirmLore = duration == GetDuration.PERMANENT
                ? "&7" + (ban ? "Ban " : "Mute ") + reported + " permanently."
                : "&7" + (ban ? "Ban " : "Mute ") + reported + " for " + GetDuration.format(duration) + ".";
        NavBar.add(inventory, "the reason", "Confirm", duration != 0, confirmLore);
        MenuItems.fill(inventory, XMaterial.BLACK_STAINED_GLASS_PANE);
        return inventory;
    }
}
