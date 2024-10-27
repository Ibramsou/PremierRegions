package fr.premier.regions.wand;

import fr.premier.regions.RegionsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class WandListener implements Listener {

    private final RegionsPlugin plugin;

    public WandListener(RegionsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        this.plugin.getWandManager().removeSelection(WandKey.of(event.getPlayer()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        final ItemStack inHand = event.getItem();
        if (!this.plugin.getWandManager().isWand(inHand)) return;
        event.setCancelled(true);
        this.plugin.getWandManager().updateSelection(event.getPlayer(), event.getClickedBlock().getLocation(), (wandSelection, location) -> {
            if (event.getAction().isLeftClick()) {
                wandSelection.setFirst(location);
                event.getPlayer().sendMessage(Component.text("Selected first position.").color(NamedTextColor.GREEN));
            } else if (event.getAction().isRightClick()) {
                wandSelection.setSecond(location);
                event.getPlayer().sendMessage(Component.text("Selected second position.").color(NamedTextColor.GREEN));
            }
        });
    }
}
