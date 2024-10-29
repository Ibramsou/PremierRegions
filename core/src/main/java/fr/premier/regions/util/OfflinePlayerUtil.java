package fr.premier.regions.util;

import fr.premier.regions.RegionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class OfflinePlayerUtil {

    private static final ExecutorService service = Executors.newSingleThreadExecutor();

    public static void getOfflinePlayerByName(String name, Consumer<OfflinePlayer> consumer) {
        getOfflinePlayerByName(name, true, consumer, null);
    }

    public static void getOfflinePlayerByName(String name, Consumer<OfflinePlayer> consumer, Runnable orElse) {
        getOfflinePlayerByName(name, true, consumer, orElse);
    }

    public static void getOfflinePlayerByName(String name, boolean synchronize, Consumer<OfflinePlayer> consumer) {
        getOfflinePlayerByName(name, synchronize, consumer, null);
    }

    public static void getOfflinePlayerByName(String name, boolean synchronize, Consumer<OfflinePlayer> consumer, Runnable orElse) {
        service.execute(() -> {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if (!offlinePlayer.isOnline() && !offlinePlayer.hasPlayedBefore()) {
                if (orElse == null) return;
                if (synchronize) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RegionsPlugin.getInstance(), orElse);
                } else {
                    orElse.run();
                }
                return;
            }

            if (synchronize) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(RegionsPlugin.getInstance(), () ->  consumer.accept(offlinePlayer));
            } else {
                consumer.accept(offlinePlayer);
            }
        });
    }
}
