package dev.aidanbooth.superreport.configs;

public class MuteReasonsConfig extends ReasonsConfig {
    public MuteReasonsConfig() {
        setup("Mute-Reasons.yml");
    }

    @Override
    protected void addDefaults() {
        menu("&8Mute » Pick a reason", "black_stained_glass_pane");
        info("writable_book", "&6&lMute Reasons",
                "&7Pick the rule this player broke.",
                "&7The reason is shown to them",
                "&7whenever they try to chat.");

        reason(GRID[0], "paper", "&6", "Spamming",
                "Flooding chat, repeating messages",
                "or typing in all caps.");
        reason(GRID[1], "name_tag", "&6", "Advertising",
                "Posting other servers, links",
                "or invites.");
        reason(GRID[2], "book", "&6", "Inappropriate Language",
                "Swearing past the server's limits",
                "or sexual messages.");
        reason(GRID[3], "anvil", "&6", "Discrimination",
                "Racism, sexism, homophobia or any",
                "hate aimed at who someone is.");
        reason(GRID[4], "skeleton_skull", "&6", "Harassment",
                "Targeting, bullying or following",
                "another player around.");
        reason(GRID[5], "golden_helmet", "&6", "Staff Disrespect",
                "Insulting staff or arguing with",
                "their decisions in public chat.");
        reason(GRID[6], "spider_eye", "&6", "Toxicity",
                "Flaming, raging or making chat",
                "unpleasant for everyone else.");
        reason(GRID[7], "map", "&6", "Sharing Personal Info",
                "Posting someone's real name, address,",
                "photos or other private details.");
        reason(GRID[8], "blaze_powder", "&6", "Threats",
                "Threatening to hack, DDoS or",
                "hurt another player.");
    }
}
