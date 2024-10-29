package fr.premier.regions.data;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.region.Region;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerData {

    private final UUID uuid;
    private final Set<Region> whitelistedRegions;
    private final Set<Region> copyRegions = new HashSet<>();
    private Region currentRegion;
    private boolean waitingSave;

    public PlayerData(UUID uuid, Set<Region> whitelistedRegions) {
        this.uuid = uuid;
        this.whitelistedRegions = whitelistedRegions;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<Region> getCopyRegions() {
        return copyRegions;
    }

    public Set<Region> getWhitelistedRegions() {
        return whitelistedRegions;
    }

    public Region getCurrentRegion() {
        return currentRegion;
    }

    public void setCurrentRegion(Region currentRegion) {
        this.currentRegion = currentRegion;
    }

    public void editWhitelist(Consumer<Set<Region>> consumer) {
        if (!this.waitingSave) {
            this.waitingSave = true;
            this.copyRegions.clear();
            this.copyRegions.addAll(this.whitelistedRegions);
            RegionsPlugin.getInstance().getPlayerDataManager().getSaveQueue().add(this);
        }
        consumer.accept(this.whitelistedRegions);
    }

    public void setWaitingSave(boolean waitingSave) {
        this.waitingSave = waitingSave;
    }
}
