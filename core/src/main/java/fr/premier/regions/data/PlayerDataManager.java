package fr.premier.regions.data;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.region.Region;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;

public class PlayerDataManager {

    private final RegionsPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private final Queue<PlayerData> saveQueue = new LinkedBlockingQueue<>();
    private final ExecutorService dataService = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("PreRegions PlayerData Thread-%d").build());
    private boolean shutdown;

    public PlayerDataManager(RegionsPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getOnlinePlayers().forEach(player -> this.getPlayerData(player.getUniqueId(), false));
    }

    public void disable() {
        this.shutdown = true;
        this.dataService.shutdownNow().forEach(Runnable::run);
    }

    public PlayerData getDirectPlayerData(UUID uuid) {
        return this.getPlayerData(uuid).join();
    }

    public CompletableFuture<PlayerData> getPlayerData(final UUID uuid) {
        return this.getPlayerData(uuid, true);
    }

    public CompletableFuture<PlayerData> getPlayerData(final UUID uuid, boolean synchronize) {
        final CompletableFuture<PlayerData> future = new CompletableFuture<>();
        final PlayerData playerData = playerDataMap.get(uuid);
        if (playerData == null) {
            Runnable load = () -> {
                final PlayerData result = this.plugin.getDatabase().loadUser(uuid);
                if (synchronize && Bukkit.isPrimaryThread()) {
                    future.complete(result);
                    this.playerDataMap.put(uuid, result);
                } else {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                        future.complete(result);
                        this.playerDataMap.put(uuid, result);
                    });
                }
            };
            if (shutdown) {
                load.run();
            } else {
                this.dataService.execute(load);
            }
        } else {
            future.complete(playerData);
        }

        return future;
    }

    public Queue<PlayerData> getSaveQueue() {
        return saveQueue;
    }
}
