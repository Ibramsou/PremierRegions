package fr.premier.regions.api;

import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.api.flag.PreFlag;
import fr.premier.regions.api.region.PreRegionEventResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PreRegionsAPI {

    static PreRegionsAPI getInstance() {
        return PreRegionsProvider.getApi();
    }

    @Nullable
    default PreFlag registerFlag(Plugin plugin, String name, String displayName) {
        return registerFlag(plugin, name, displayName, FlagState.NONE);
    }

    @Nullable PreFlag registerFlag(Plugin plugin, String name, String displayName, FlagState defaultState);

    @Nullable PreFlag getDefaultFlag(String name);

    @Nullable PreFlag getFlag(Plugin plugin, String name);

    default PreRegionEventResult getPlayerEventResult(Player player, PreFlag flag, boolean cancelled) {
        return getEventResult(player.getUniqueId(), flag, player.getLocation(), cancelled);
    }

    default PreRegionEventResult getPlayerEventResult(Player player, PreFlag flag, Location location, boolean cancelled) {
        return getEventResult(player.getUniqueId(), flag, location, cancelled);
    }

    default PreRegionEventResult getEventResult(PreFlag flag, Location location, boolean cancelled) {
        return getEventResult(null, flag, location, cancelled);
    }

    PreRegionEventResult getEventResult(@Nullable UUID uuid, PreFlag flag, Location location, boolean cancelled);

}
