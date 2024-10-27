package fr.premier.regions.data;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.binary.impl.BinaryWhitelist;
import fr.premier.regions.region.Region;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final BinaryWhitelist whitelistedRegions;
    private Region currentRegion;
    private boolean saved;

    public PlayerData(UUID uuid, BinaryWhitelist whitelistedRegions) {
        this.uuid = uuid;
        this.whitelistedRegions = whitelistedRegions;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BinaryWhitelist getBinaryWhitelistedRegions() {
        return whitelistedRegions;
    }

    public Region getCurrentRegion() {
        return currentRegion;
    }

    public void setCurrentRegion(Region currentRegion) {
        this.currentRegion = currentRegion;
    }

    public void save() {
        if (this.saved) return;
        this.saved = true;
        RegionsPlugin.getInstance().getPlayerDataManager().getSaveQueue().add(this);
    }
}
