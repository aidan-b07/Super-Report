package dev.aidanbooth.superreport.pipeline.punishPipeline.inventories;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.utils.GetDuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class DurationInventory {
    public Inventory create(InventoryHolder holder, PunishmentDraft draft) {
        Inventory inventory = Bukkit.createInventory(holder, 45, ChatColor.GOLD + GetDuration.getDuration(draft.getDuration()));
        addItems(inventory);
        return inventory;
    }

    private void addItems(Inventory inv) {
        ItemStack addMinute = XMaterial.GREEN_WOOL.parseItem();
        ItemMeta addMinuteMeta = addMinute.getItemMeta();
        addMinuteMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "+ 1 Minute");
        addMinute.setItemMeta(addMinuteMeta);

        ItemStack addHour = XMaterial.GREEN_WOOL.parseItem();
        ItemMeta addHourMeta = addHour.getItemMeta();
        addHourMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "+ 1 Hour");
        addHour.setItemMeta(addHourMeta);

        ItemStack addDay = XMaterial.GREEN_WOOL.parseItem();
        ItemMeta addDayMeta = addDay.getItemMeta();
        addDayMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "+ 1 Day");
        addDay.setItemMeta(addDayMeta);

        ItemStack addWeek = XMaterial.GREEN_WOOL.parseItem();
        ItemMeta addWeekMeta = addWeek.getItemMeta();
        addWeekMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "+ 1 Week");
        addWeek.setItemMeta(addWeekMeta);

        ItemStack addMonth = XMaterial.GREEN_WOOL.parseItem();
        ItemMeta addMonthMeta = addMonth.getItemMeta();
        addMonthMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "+ 1 Month");
        addMonth.setItemMeta(addMonthMeta);

        ItemStack setPermanent = XMaterial.GREEN_WOOL.parseItem();
        ItemMeta setPermanentMeta = setPermanent.getItemMeta();
        setPermanentMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Set Permanent");
        setPermanent.setItemMeta(setPermanentMeta);


        ItemStack removeMinute = XMaterial.RED_WOOL.parseItem();
        ItemMeta removeMinuteMeta = removeMinute.getItemMeta();
        removeMinuteMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "- 1 Minute");
        removeMinute.setItemMeta(removeMinuteMeta);

        ItemStack removeHour = XMaterial.RED_WOOL.parseItem();
        ItemMeta removeHourMeta = removeHour.getItemMeta();
        removeHourMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "- 1 Hour");
        removeHour.setItemMeta(removeHourMeta);

        ItemStack removeDay = XMaterial.RED_WOOL.parseItem();
        ItemMeta removeDayMeta = removeDay.getItemMeta();
        removeDayMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "- 1 Day");
        removeDay.setItemMeta(removeDayMeta);

        ItemStack removeWeek = XMaterial.RED_WOOL.parseItem();
        ItemMeta removeWeekMeta = removeWeek.getItemMeta();
        removeWeekMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "- 1 Week");
        removeWeek.setItemMeta(removeWeekMeta);

        ItemStack removeMonth = XMaterial.RED_WOOL.parseItem();
        ItemMeta removeMonthMeta = removeMonth.getItemMeta();
        removeMonthMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "- 1 Month");
        removeMonth.setItemMeta(removeMonthMeta);

        ItemStack removePermanent = XMaterial.RED_WOOL.parseItem();
        ItemMeta removePermanentMeta = removePermanent.getItemMeta();
        removePermanentMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Remove Permanent");
        removePermanent.setItemMeta(removePermanentMeta);


        ItemStack set1Day = XMaterial.BLUE_WOOL.parseItem();
        ItemMeta set1DayMeta = set1Day.getItemMeta();
        set1DayMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Preset - 1 day");
        set1DayMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to instantly set", ChatColor.GRAY + "the duration to 1 day!"));
        set1Day.setItemMeta(set1DayMeta);

        ItemStack set3Day = XMaterial.BLUE_WOOL.parseItem();
        ItemMeta set3DayMeta = set3Day.getItemMeta();
        set3DayMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Preset - 3 days");
        set3DayMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to instantly set", ChatColor.GRAY + "the duration to 3 days!"));
        set3Day.setItemMeta(set3DayMeta);

        ItemStack set1Week = XMaterial.BLUE_WOOL.parseItem();
        ItemMeta set1WeekMeta = set1Week.getItemMeta();
        set1WeekMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Preset - 1 week");
        set1WeekMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to instantly set", ChatColor.GRAY + "the duration to 1 week!"));
        set1Week.setItemMeta(set1WeekMeta);

        ItemStack set2Weeks = XMaterial.BLUE_WOOL.parseItem();
        ItemMeta set2WeeksMeta = set2Weeks.getItemMeta();
        set2WeeksMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Preset - 2 weeks");
        set2WeeksMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to instantly set", ChatColor.GRAY + "the duration to 2 weeks!"));
        set2Weeks.setItemMeta(set2WeeksMeta);

        ItemStack set2Months = XMaterial.BLUE_WOOL.parseItem();
        ItemMeta set2MonthsMeta = set2Months.getItemMeta();
        set2MonthsMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Preset - 2 months");
        set2MonthsMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to instantly set", ChatColor.GRAY + "the duration to 2 months!"));
        set2Months.setItemMeta(set2MonthsMeta);

        ItemStack set6Months = XMaterial.BLUE_WOOL.parseItem();
        ItemMeta set6MonthsMeta = set6Months.getItemMeta();
        set6MonthsMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Preset - 6 months");
        set6MonthsMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to instantly set", ChatColor.GRAY + "the duration to 6 months!"));
        set6Months.setItemMeta(set6MonthsMeta);

        ItemStack set1Year = XMaterial.BLUE_WOOL.parseItem();
        ItemMeta set1YearMeta = set1Year.getItemMeta();
        set1YearMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Preset - 1 year");
        set1YearMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click here to instantly set", ChatColor.GRAY + "the duration to 1 year!"));
        set1Year.setItemMeta(set1YearMeta);


        ItemStack fillItem = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.setDisplayName(" ");
        fillItem.setItemMeta(fillItemMeta);

        ItemStack count = XMaterial.NETHER_STAR.parseItem();
        ItemMeta countMeta = count.getItemMeta();
        countMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Click here to confirm!");
        count.setItemMeta(countMeta);


        for (int x = 0; x < inv.getSize(); x++) {
            if (inv.getItem(x) == null) {
                inv.setItem(x, fillItem);
            }
        } //Fill items

        inv.setItem(10, addMinute);
        inv.setItem(11, addHour);
        inv.setItem(12, addDay);
        inv.setItem(19, addWeek);
        inv.setItem(20, addMonth);
        inv.setItem(21, setPermanent);

        inv.setItem(14, removeMinute);
        inv.setItem(15, removeHour);
        inv.setItem(16, removeDay);
        inv.setItem(23, removeWeek);
        inv.setItem(24, removeMonth);
        inv.setItem(25, removePermanent);

        inv.setItem(28, set1Day);
        inv.setItem(29, set3Day);
        inv.setItem(30, set1Week);
        inv.setItem(31, set2Weeks);
        inv.setItem(32, set2Months);
        inv.setItem(33, set6Months);
        inv.setItem(34, set1Year);

        inv.setItem(22, count);
    }
}
