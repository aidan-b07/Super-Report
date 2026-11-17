package dev.aidanbooth.superreport.configs;

import java.util.ArrayList;
import java.util.List;

public class ReportMenuConfig extends ConfigFile {
    public ReportMenuConfig() {
        setup("ReportMenu.yml");
    }

    @Override
    protected void addDefaults() {
        config().createSection("ReportMenu");

        config().set("ReportMenu.Size", 36);

        config().createSection("ReportMenu.Items.Slot");

        config().createSection("ReportMenu.Items.Slot.13");
        config().set("ReportMenu.Items.Slot.13.Material", "nether_star");
        config().set("ReportMenu.Items.Slot.13.Name", "&cReport &6{player}");
        List<String> lore1 = new ArrayList<>();
        lore1.add("&7Select what you want this player");
        lore1.add("&7to be reported for!");
        config().set("ReportMenu.Items.Slot.13.Lore", lore1);
        config().set("ReportMenu.Items.Slot.13.Amount", 1);
        config().set("ReportMenu.Items.Slot.13.Option", false);

        config().createSection("ReportMenu.Items.Slot.19");
        config().set("ReportMenu.Items.Slot.19.Material", "diamond");
        config().set("ReportMenu.Items.Slot.19.Name", "&cKill Aura");
        List<String> lore2 = new ArrayList<>();
        lore2.add("&7Click to report this player");
        lore2.add("&7for kill aura!");
        config().set("ReportMenu.Items.Slot.19.Lore", lore2);
        config().set("ReportMenu.Items.Slot.19.Amount", 1);
        config().set("ReportMenu.Items.Slot.19.Option", true);

        config().createSection("ReportMenu.Items.Slot.21");
        config().set("ReportMenu.Items.Slot.21.Material", "diamond");
        config().set("ReportMenu.Items.Slot.21.Name", "&cChat Abuse");
        List<String> lore3 = new ArrayList<>();
        lore3.add("&7Click to report this player");
        lore3.add("&7for chat abuse!");
        config().set("ReportMenu.Items.Slot.21.Lore", lore3);
        config().set("ReportMenu.Items.Slot.21.Amount", 1);
        config().set("ReportMenu.Items.Slot.21.Option", true);

        config().createSection("ReportMenu.Items.Slot.23");
        config().set("ReportMenu.Items.Slot.23.Material", "diamond");
        config().set("ReportMenu.Items.Slot.23.Name", "&cCross-Teaming");
        List<String> lore4 = new ArrayList<>();
        lore4.add("&7Click to report this player");
        lore4.add("&7for cross-teaming!");
        config().set("ReportMenu.Items.Slot.23.Lore", lore4);
        config().set("ReportMenu.Items.Slot.23.Amount", 1);
        config().set("ReportMenu.Items.Slot.23.Option", true);

        config().createSection("ReportMenu.Items.Slot.25");
        config().set("ReportMenu.Items.Slot.25.Material", "diamond");
        config().set("ReportMenu.Items.Slot.25.Name", "&cTeam Griefing");
        List<String> lore5 = new ArrayList<>();
        lore5.add("&7Click to report this player");
        lore5.add("&7for team griefing!");
        config().set("ReportMenu.Items.Slot.25.Lore", lore5);
        config().set("ReportMenu.Items.Slot.25.Amount", 1);
        config().set("ReportMenu.Items.Slot.25.Option", true);

        config().createSection("ReportMenu.Items.Fill");
        config().set("ReportMenu.Items.Fill.Material", "gray_stained_glass_pane");
        config().set("ReportMenu.Items.Fill.Name", " ");

        save();
    }
}
