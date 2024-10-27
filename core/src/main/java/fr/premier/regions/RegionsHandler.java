package fr.premier.regions;

import fr.premier.regions.api.PreRegionsAPI;
import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.api.flag.PreFlag;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RegionsHandler implements PreRegionsAPI {

    private final RegionsPlugin plugin = RegionsPlugin.getPlugin(RegionsPlugin.class);

    @Override
    public @Nullable PreFlag registerFlag(Plugin plugin, String name, String displayName, FlagState defaultState) {
        return this.plugin.getFlagManager().register(plugin, name, displayName, defaultState);
    }

    @Override
    public @Nullable PreFlag getDefaultFlag(String name) {
        return this.plugin.getFlagManager().getDefaultFlag(name);
    }

    @Override
    public @Nullable PreFlag getFlag(Plugin plugin, String name) {
        return this.plugin.getFlagManager().getFlag(plugin, name);
    }
}
