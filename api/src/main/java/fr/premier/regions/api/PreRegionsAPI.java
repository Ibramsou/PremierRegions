package fr.premier.regions.api;

import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.api.flag.PreFlag;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

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
}
