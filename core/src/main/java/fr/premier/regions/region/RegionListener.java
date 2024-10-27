package fr.premier.regions.region;

import fr.premier.regions.api.PreRegionsAPI;
import fr.premier.regions.api.flag.PreFlag;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class RegionListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (!PreRegionsAPI.getInstance().matchFlag(event.getPlayer(), PreFlag.BLOCK_BREAK)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        if (!PreRegionsAPI.getInstance().matchFlag(event.getPlayer(), PreFlag.BLOCK_PLACE)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        if (!PreRegionsAPI.getInstance().matchFlag(event.getPlayer(), PreFlag.INTERACT)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (!PreRegionsAPI.getInstance().matchFlag(PreFlag.ENTITY_DAMAGE, event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        final Location location = event.getEntity().getLocation();
        if (event.getDamager() instanceof Player player) {
            if (!PreRegionsAPI.getInstance().matchFlag(player, PreFlag.ENTITY_DAMAGE, location)) {
                event.setCancelled(true);
            }
        } else if (!PreRegionsAPI.getInstance().matchFlag(PreFlag.ENTITY_DAMAGE, location)) {
            event.setCancelled(true);
        }
    }
}
