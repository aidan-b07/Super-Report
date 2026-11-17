package dev.aidanbooth.superreport.configs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Shared layout for Ban-Reasons.yml and Mute-Reasons.yml
public abstract class ReasonsConfig extends ConfigFile {

    // Reasons sit in a 3x3 grid in the middle of a 5 row menu
    protected static final int[] GRID = {11, 13, 15, 20, 22, 24, 29, 31, 33};

    protected void menu(String title, String fillMaterial) {
        config().set("Title", title);
        config().set("Size", 45);
        config().set("Fill.Material", fillMaterial);
        config().set("Fill.Name", " ");
    }

    // The item in the top middle that explains the menu. Clicking it does nothing
    protected void info(String material, String name, String... lore) {
        config().set("Info.Slot", 4);
        config().set("Info.Material", material);
        config().set("Info.Name", name);
        config().set("Info.Lore", Arrays.asList(lore));
    }

    // reason is what gets saved and shown on the ban or mute screen
    protected void reason(int slot, String material, String colour, String reason, String... description) {
        String path = "Items.Slot." + slot;
        List<String> lore = new ArrayList<>();
        for (String line : description) lore.add("&7" + line);
        lore.add("");
        lore.add("&8Recorded as: &f" + reason);
        lore.add("&e» Click to select");

        config().set(path + ".Material", material);
        config().set(path + ".Name", colour + "&l" + reason);
        config().set(path + ".Reason", reason);
        config().set(path + ".Lore", lore);
        config().set(path + ".Amount", 1);
    }

    @Override
    protected boolean isRequired(String key) {
        // Not the reasons or the info item, so admins can remove them
        return key.equals("Title") || key.equals("Size") || key.startsWith("Fill.");
    }
}
