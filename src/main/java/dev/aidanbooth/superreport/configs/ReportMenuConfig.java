package dev.aidanbooth.superreport.configs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportMenuConfig extends ConfigFile {
    public ReportMenuConfig() {
        setup("ReportMenu.yml");
    }

    @Override
    protected void addDefaults() {
        config().set("ReportMenu.Title", "&8Report » {player}");
        config().set("ReportMenu.Size", 45);
        config().set("ReportMenu.Items.Fill.Material", "black_stained_glass_pane");
        config().set("ReportMenu.Items.Fill.Name", " ");

        // Option: false makes an item display only. Head: shows that player's skin on a player_head
        String info = "ReportMenu.Items.Slot.4";
        config().set(info + ".Material", "player_head");
        config().set(info + ".Head", "{player}");
        config().set(info + ".Name", "&b&lReporting &f{player}");
        config().set(info + ".Lore", Arrays.asList(
                "&7Pick what {player} did wrong.",
                "&7Staff are told straight away.",
                "",
                "&8False reports can be punished."));
        config().set(info + ".Amount", 1);
        config().set(info + ".Option", false);

        int[] grid = ReasonsConfig.GRID;
        reason(grid[0], "diamond_sword", "Hacking",
                "Kill aura, reach, fly, speed or",
                "any other unfair advantage.");
        reason(grid[1], "diamond_ore", "X-Ray",
                "Heading straight for ores or",
                "seeing through blocks.");
        reason(grid[2], "flint_and_steel", "Griefing",
                "Breaking, burning or stealing",
                "from someone else's build.");
        reason(grid[3], "tnt", "Bug Exploiting",
                "Using glitches or duplicating",
                "items.");
        reason(grid[4], "leather_chestplate", "Cross-Teaming",
                "Teaming up with other players",
                "or teams when they shouldn't.");
        reason(grid[5], "paper", "Spamming",
                "Flooding chat or repeating",
                "the same message.");
        reason(grid[6], "book", "Inappropriate Chat",
                "Swearing, slurs, advertising",
                "or sexual messages.");
        reason(grid[7], "skeleton_skull", "Harassment",
                "Targeting, bullying or",
                "threatening someone.");
        reason(grid[8], "gold_ingot", "Scamming",
                "Tricking players out of items",
                "or money.");
    }

    // reason is what staff see in /reports and on Discord
    private void reason(int slot, String material, String reason, String... description) {
        String path = "ReportMenu.Items.Slot." + slot;
        List<String> lore = new ArrayList<>();
        for (String line : description) lore.add("&7" + line);
        lore.add("");
        lore.add("&e» Click to report {player}");

        config().set(path + ".Material", material);
        config().set(path + ".Name", "&b&l" + reason);
        config().set(path + ".Reason", reason);
        config().set(path + ".Lore", lore);
        config().set(path + ".Amount", 1);
        config().set(path + ".Option", true);
    }

    @Override
    protected boolean isRequired(String key) {
        // Not the reason slots, so admins can remove reasons
        return key.equals("ReportMenu.Title") || key.equals("ReportMenu.Size") || key.startsWith("ReportMenu.Items.Fill.");
    }
}
