package fr.premier.regions.data;

import fr.premier.regions.RegionsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerDataListener implements Listener {

    private final RegionsPlugin plugin;

    public PlayerDataListener(RegionsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        this.plugin.getPlayerDataManager().getPlayerData(event.getUniqueId()).join();
    }
}
