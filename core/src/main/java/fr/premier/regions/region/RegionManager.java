package fr.premier.regions.region;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.binary.impl.BinaryFlags;
import fr.premier.regions.flag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RegionManager {

    private final RegionsPlugin plugin;
    private final Map<Integer, Region> regions = new HashMap<>();

    public RegionManager(RegionsPlugin plugin) {
        this.plugin = plugin;
    }

    public void setFlagState(Region region, Flag flag, FlagState state) {
        region.binaryFlags().getUpdateValue(map -> {
            if (flag.defaultState() == state) {
                map.remove(flag);
            } else {
                map.put(flag, state);
            }
        });
    }

    public FlagState getFlagState(Region region, Flag flag) {
        final FlagState currentState = region.binaryFlags().getValue().get(flag);
        if (currentState == null) {
            return flag.defaultState();
        }

        return currentState;
    }

    public int hashRegion(Region region) {
        return hashRegion(region.minLocation().getWorld(), region.name());
    }

    public int hashRegion(World world, String name) {
        return Objects.hash(world.getName(), name);
    }

    public Region getRegion(World world, String name) {
        return regions.get(hashRegion(world, name));
    }

    public Region getRegion(int hashcode) {
        return regions.get(hashcode);
    }

    public boolean containsRegion(final World world, final String regionName) {
        return regions.containsKey(hashRegion(world, regionName));
    }

    public CompletableFuture<Region> addRegion(String name, Location min, Location max) {
        if (this.regions.containsKey(hashRegion(min.getWorld(),name))) {
            return null;
        }

        final CompletableFuture<Region> future = new CompletableFuture<>();
        final Region region = new Region(UUID.randomUUID(), name, min, max, new BinaryFlags());
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.getDatabase().insertRegion(region);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> future.complete(region));
        });

        return future;
    }

    public void deleteRegion(Region region, Runnable removed) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.getDatabase().deleteRegion(region);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                this.regions.remove(hashRegion(region));
                removed.run();
            });
        });
    }

    public Map<Integer, Region> getRegions() {
        return regions;
    }
}
