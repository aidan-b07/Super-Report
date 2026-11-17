package dev.aidanbooth.superreport.configs;

public class BanReasonsConfig extends ReasonsConfig {
    public BanReasonsConfig() {
        setup("Ban-Reasons.yml");
    }

    @Override
    protected void addDefaults() {
        menu("&8Ban » Pick a reason", "black_stained_glass_pane");
        info("book", "&c&lBan Reasons",
                "&7Pick the rule this player broke.",
                "&7The reason is shown on their ban",
                "&7screen and saved to their history.");

        reason(GRID[0], "diamond_sword", "&c", "Hacking",
                "Kill aura, reach, fly, speed or",
                "any other unfair client or mod.");
        reason(GRID[1], "diamond_ore", "&c", "X-Ray",
                "X-ray texture packs, ore finders",
                "or seeing through blocks.");
        reason(GRID[2], "tnt", "&c", "Bug Exploiting",
                "Abusing glitches, duplicating",
                "items or crashing the server.");
        reason(GRID[3], "flint_and_steel", "&c", "Griefing",
                "Breaking, burning or stealing",
                "from builds that aren't theirs.");
        reason(GRID[4], "leather_chestplate", "&c", "Cross-Teaming",
                "Teaming up with other players",
                "or teams against the rules.");
        reason(GRID[5], "gold_ingot", "&c", "Scamming",
                "Tricking players out of items,",
                "money or accounts.");
        reason(GRID[6], "painting", "&c", "Inappropriate Content",
                "Offensive builds, skins, names",
                "or signs.");
        reason(GRID[7], "iron_door", "&c", "Ban Evasion",
                "Playing on another account",
                "while banned.");
        reason(GRID[8], "redstone", "&c", "Repeated Offences",
                "Kept breaking the rules after",
                "earlier mutes or bans.");
    }
}
