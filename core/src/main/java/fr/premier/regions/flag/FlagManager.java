package fr.premier.regions.flag;

import fr.premier.regions.api.flag.FlagState;
import org.bukkit.plugin.Plugin;

import java.util.LinkedHashMap;
import java.util.Map;

public class FlagManager {

    private final Map<String, Flag> flags = new LinkedHashMap<>();

    public FlagManager() {
        // Register default flags
        register("block_break", "Block Break", FlagState.WHITELIST);
        register("block_place", "Block Place", FlagState.WHITELIST);
        register("interact", "Interact", FlagState.WHITELIST);
    }

    public Flag register(Plugin plugin, String name, String displayName, FlagState defaultState) {
        return this.register(plugin.getName().toLowerCase() + "_" + name, displayName, defaultState);
    }

    private Flag register(String name, String displayName, FlagState defaultState) {
        if (flags.containsKey(name)) {
            return null;
        }

        final Flag flag = new Flag(name, displayName, defaultState);
        this.flags.put(name, flag);
        return flag;
    }

    public Flag getDefaultFlag(String name) {
        return this.flags.get(name);
    }

    public Flag getFlag(Plugin plugin, String name) {
        return flags.get(plugin.getName().toLowerCase() + "_" + name);
    }

    public Map<String, Flag> getFlags() {
        return flags;
    }
}
