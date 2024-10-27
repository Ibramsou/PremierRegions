package fr.premier.regions.api.flag;

import fr.premier.regions.api.PreRegionsAPI;

public interface PreFlag {

    PreFlag BLOCK_BREAK = getDefault("block_break");
    PreFlag BLOCK_PLACE = getDefault("block_place");
    PreFlag INTERACT = getDefault("interact");
    PreFlag ENTITY_DAMAGE = getDefault("entity_damage");

    static PreFlag getDefault(String name) {
        return PreRegionsAPI.getInstance().getDefaultFlag(name);
    }

    /**
     * Get the name of the flag
     * @return String
     */
    String getName();

    /**
     * Get the flag's display name
     * @return String
     */
    String getDisplayName();

    /**
     * Get the state of flag by default while region is created
     * @return {@link FlagState}
     */
    FlagState getDefaultState();
}
