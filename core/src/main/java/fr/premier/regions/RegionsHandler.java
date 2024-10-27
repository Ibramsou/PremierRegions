package fr.premier.regions;

import fr.premier.regions.api.PreRegionsAPI;
import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.api.flag.PreFlag;
import fr.premier.regions.flag.Flag;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

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

    @Override
    public boolean matchFlag(Player player, PreFlag flag) {
        return this.plugin.getRegionManager().matchFlagState(player, (Flag) flag);
    }

    @Override
    public boolean matchFlag(Player player, PreFlag flag, Location location) {
        return this.plugin.getRegionManager().matchFlagState(player, (Flag) flag, location);
    }

    @Override
    public boolean matchFlag(UUID uuid, PreFlag flag, Location location) {
        return this.plugin.getRegionManager().matchFlagState(uuid, (Flag) flag, location);
    }

    @Override
    public boolean matchFlag(PreFlag flag, Location location) {
        return this.plugin.getRegionManager().matchFlagState((Flag) flag, location);
    }
}
