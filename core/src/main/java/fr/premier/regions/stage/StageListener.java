package fr.premier.regions.stage;

import fr.premier.regions.RegionsPlugin;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StageListener implements Listener {

    private final RegionsPlugin plugin;

    public StageListener(RegionsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        this.plugin.getStageManager().executeStage(event.getPlayer().getUniqueId(), event);
    }
}
