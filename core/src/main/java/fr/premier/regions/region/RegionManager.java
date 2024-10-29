package fr.premier.regions.region;

import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.api.region.PreRegionEventResult;
import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.binary.impl.BinaryFlags;
import fr.premier.regions.data.PlayerData;
import fr.premier.regions.flag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;

public class RegionManager {

    private final RegionsPlugin plugin;
    private final Map<Integer, Region> regions = new HashMap<>();
    private final Map<Chunk, List<Region>> chunkRegions = new HashMap<>();

    public RegionManager(RegionsPlugin plugin) {
        this.plugin = plugin;
    }

    public PreRegionEventResult getEventResult(@Nullable UUID uuid, Flag flag, Location location, boolean cancelled) {
        final Boolean shouldCancel;
        if (uuid == null) {
            shouldCancel = this.shouldCancel(flag, location);
        } else {
            shouldCancel = this.shouldCancel(uuid, flag, location);
        }
        if (shouldCancel == null) {
            return PreRegionEventResult.NONE;
        }
        if (shouldCancel && !cancelled) {
            return PreRegionEventResult.CANCEL;
        } else if (!shouldCancel && cancelled) {
            return PreRegionEventResult.ALLOW;
        }

        return PreRegionEventResult.NONE;
    }

    private Boolean shouldCancel(Flag flag, Location location, BiPredicate<Region, FlagState> predicate) {
        Chunk chunk = location.getChunk();
        List<Region> regions = chunkRegions.get(chunk);
        if (regions == null) return null;
        return regions.stream().filter(region -> {
            final Location first = region.getFirstLocation();
            final Location second = region.getSecondLocation();
            return location.getX() >= Math.min(first.getBlockX(), second.getBlockX()) && location.getX() <= Math.max(first.getBlockX(), second.getBlockX()) &&
                    location.getY() >= Math.min(first.getBlockY(), second.getBlockY()) && location.getY() <= Math.max(first.getBlockY(), second.getBlockY()) &&
                    location.getZ() >= Math.min(first.getBlockZ(), second.getBlockZ()) && location.getZ() <= Math.max(first.getBlockZ(), second.getBlockZ());
        }).anyMatch(region -> predicate.test(region, this.getFlagState(region, flag)));
    }

    private Boolean shouldCancel(Flag flag, Location location) {
        return this.shouldCancel(flag, location, (region, state) -> state == FlagState.NONE || state == FlagState.WHITELIST);
    }

    private Boolean shouldCancel(UUID uuid, Flag flag, Location location) {
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(uuid).join();
        return shouldCancel(flag, location, (region, state) -> {
            if (state == FlagState.NONE) return true;
            if (state == FlagState.EVERYONE) return false;
            if (state == FlagState.WHITELIST) return !playerData.getBinaryWhitelistedRegions().getValue().contains(region);
            return false;
        });
    }

    public void setFlagState(Region region, Flag flag, FlagState state) {
        region.getBinaryFlags().getUpdateValue(map -> {
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
        final FlagState currentState = region.getBinaryFlags().getValue().get(flag);
        if (currentState == null) {
            return flag.defaultState();
        }

        return currentState;
    }

    public int hashRegion(Region region) {
        return hashRegion(region.getFirstLocation().getWorld(), region.getName());
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
        final Location first = region.getFirstLocation();
        final Location second = region.getSecondLocation();
        final World world = first.getWorld();
        final int minX = Math.min(first.getBlockX(), second.getBlockX()) >> 4;
        final int minZ = Math.min(first.getBlockZ(), second.getBlockZ()) >> 4;
        final int maxX = Math.max(first.getBlockX(), second.getBlockX()) >> 4;
        final int maxZ = Math.max(first.getBlockZ(), second.getBlockZ()) >> 4;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Chunk chunk = world.getChunkAt(x, z, false);
                region.getChunks().add(chunk);
                this.chunkRegions.computeIfAbsent(chunk, chunk1 -> new ArrayList<>()).add(region);
            }
        }

        this.regions.put(hashRegion(region), region);
    }

    public Map<Integer, Region> getRegions() {
        return regions;
    }
}
