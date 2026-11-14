package dev.aidanbooth.superreport.utils;

import com.cryptomorin.xseries.XMaterial;
import dev.aidanbooth.superreport.Main;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class ConfigMaterials {

    public static final XMaterial FALLBACK = XMaterial.BARRIER;

    private static final Set<String> warned = new HashSet<>();

    private ConfigMaterials() {
    }

    public static XMaterial get(String name, String where) {
        if (name == null || name.trim().isEmpty()) {
            return fallback(where, "no material is set");
        }

        Optional<XMaterial> match = XMaterial.matchXMaterial(name.trim());
        if (!match.isPresent()) {
            return fallback(where, "'" + name + "' is not a valid material");
        }
        if (!match.get().isSupported()) {
            return fallback(where, "'" + name + "' doesn't exist on this server version");
        }
        return match.get();
    }

    private static XMaterial fallback(String where, String problem) {
        if (warned.add(where + '|' + problem)) {
            Main.getPlugin(Main.class).getLogger().warning(
                    where + ": " + problem + ". Showing " + FALLBACK.name() + " instead.");
        }
        return FALLBACK;
    }
}
