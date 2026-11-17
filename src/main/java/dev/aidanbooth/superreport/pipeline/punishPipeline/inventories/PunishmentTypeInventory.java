package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.shared.NavBar;
import dev.aidanbooth.superreport.utils.MenuItems;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Ban or mute
public class PunishmentTypeInventory {
    public static final int BAN = 11;
    public static final int MUTE = 15;

    public Inventory create(InventoryHolder holder, PunishmentDraft data) {
        String reported = PlayerUtils.GetUsername(data.getReported());
        Inventory inventory = Bukkit.createInventory(holder, 36, MenuItems.title("&8Punish » " + reported, t -> t));

        List<String> headLore = new ArrayList<>();
        headLore.add("&7Choose how to punish this player.");
        if (data.getReport() != null) {
            headLore.add("");
            headLore.add("&7Reported for: &f" + data.getReport().reason());
        }
        inventory.setItem(4, MenuItems.head(reported, "&c&lPunishing &f" + reported, headLore));

        PunishmentType chosen = data.getPunishmentType();
        inventory.setItem(BAN, MenuItems.item(XMaterial.IRON_AXE, "&c&lBan", selected(chosen == PunishmentType.BAN,
                "&7Remove " + reported + " from the",
                "&7server for a set time or forever.",
                "&7They're kicked straight away if online.",
                "",
                "&e» Click to ban")));
        inventory.setItem(MUTE, MenuItems.item(XMaterial.WRITABLE_BOOK, "&6&lMute", selected(chosen == PunishmentType.MUTE,
                "&7Stop " + reported + " from chatting",
                "&7for a set time or forever.",
                "&7They can still play as normal.",
                "",
                "&e» Click to mute")));

        NavBar.add(inventory, "the report", "Continue", chosen != null, "&7Pick a reason for the " + (chosen == PunishmentType.MUTE ? "mute" : "ban") + ".");
        MenuItems.fill(inventory, XMaterial.BLACK_STAINED_GLASS_PANE);
        return inventory;
    }

    private static List<String> selected(boolean selected, String... lore) {
        List<String> lines = new ArrayList<>(Arrays.asList(lore));
        if (selected) lines.add("&a» Currently selected");
        return lines;
    }
}
