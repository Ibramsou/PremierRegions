package fr.premier.regions.region;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.binary.impl.BinaryFlags;
import fr.premier.regions.data.PlayerData;
import fr.premier.regions.flag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RegionManager {

    private final RegionsPlugin plugin;
    private final Map<Integer, Region> regions = new HashMap<>();
    private final Map<Chunk, List<Region>> chunkRegions = new HashMap<>();

    public RegionManager(RegionsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean matchFlagState(Player player, Flag flag) {
        return matchFlagState(player, flag, player.getLocation());
    }

    public boolean matchFlagState(Player player, Flag flag, Location location) {
        return matchFlagState(player.getUniqueId(), flag, location);
    }

    public boolean matchFlagState(Flag flag, Location location) {
        Chunk chunk = location.getChunk();
        List<Region> regions = chunkRegions.get(chunk);
        if (regions == null) return true;
        return regions.stream().anyMatch(region -> {
            FlagState flagState = this.getFlagState(region, flag);
            return flagState == FlagState.EVERYONE || flagState == FlagState.WHITELIST;
        });
    }

    public boolean matchFlagState(UUID uuid, Flag flag, Location location) {
        Chunk chunk = location.getChunk();
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(uuid).join();
        List<Region> regions = chunkRegions.get(chunk);
        if (regions == null) return true;
        return regions.stream().anyMatch(region -> {
            FlagState flagState = this.getFlagState(region, flag);
            if (flagState == null) return false;
            if (flagState == FlagState.NONE) return false;
            if (flagState == FlagState.EVERYONE) return true;
            if (flagState == FlagState.WHITELIST) return playerData.getBinaryWhitelistedRegions().getValue().contains(region);
            return false;
        });
    }

    public void setFlagState(Region region, Flag flag, FlagState state) {
        region.binaryFlags().getUpdateValue(map -> {
            if (map.get(flag) == state) return;
            if (flag.defaultState() == state) {
                map.remove(flag);
            } else {
                map.put(flag, state);
            }
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getDatabase().updateRegionFlag(region));
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
        final CompletableFuture<Region> future = new CompletableFuture<>();
        final Region region = new Region(UUID.randomUUID(), name, min, max, new BinaryFlags(), new ArrayList<>());
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.getDatabase().insertRegion(region);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                this.loadRegion(region);
                future.complete(region);
            });
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

    public void loadRegion(Region region) {
        final Location min = region.minLocation();
        final Location max = region.maxLocation();
        final World world = min.getWorld();
        for (int x = min.getBlockX() << 4; x <= max.getBlockX() << 4; x++) {
            for (int z = min.getBlockZ() << 4; z <= max.getBlockZ() << 4; z++) {
                Chunk chunk = world.getChunkAt(x, z, false);
                region.chunks().add(chunk);
                this.chunkRegions.computeIfAbsent(chunk, chunk1 -> new ArrayList<>()).add(region);
            }
        }

        this.regions.put(hashRegion(region), region);
    }

    public Map<Integer, Region> getRegions() {
        return regions;
    }
}
