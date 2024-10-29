package fr.premier.regions.region;

import fr.premier.regions.api.flag.PreFlag;
import fr.premier.regions.api.region.PreRegionUtils;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        PreRegionUtils.handleCancelablePlayerEvent(event, event.getPlayer(), event.getBlock().getLocation(), PreFlag.BLOCK_BREAK);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        PreRegionUtils.handleCancelablePlayerEvent(event, event.getPlayer(), event.getBlock().getLocation(), PreFlag.BLOCK_PLACE);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        PreRegionUtils.handleCancelablePlayerEvent(event, event.getPlayer(), PreFlag.INTERACT);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        PreRegionUtils.handleCancelableEvent(event, null, event.getEntity().getLocation(), PreFlag.ENTITY_DAMAGE);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        final Location location = event.getEntity().getLocation();
        if (event.getDamager() instanceof Player player) {
            PreRegionUtils.handleCancelablePlayerEvent(event, player, location, PreFlag.ENTITY_DAMAGE);
        } else {
            PreRegionUtils.handleCancelableEvent(event, null, location, PreFlag.ENTITY_DAMAGE);
        }
    }
}
