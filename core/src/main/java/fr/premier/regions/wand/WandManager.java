package fr.premier.regions.wand;

import fr.premier.regions.RegionsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WandManager {

    private final RegionsPlugin plugin;
    private final NamespacedKey key;
    private final Map<WandKey, WandSelection> selectionMap = new HashMap<>();

    public WandManager(RegionsPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "wand");
    }

    public void removeSelection(WandKey key) {
        this.selectionMap.remove(key);
    }

    public boolean createRegion(Player player, String name) {
        final WandKey key = WandKey.of(player);
        final WandSelection selection = this.selectionMap.get(key);
        if (selection == null) return false;
        if (selection.getFirst() == null || selection.getSecond() == null) return false;
        this.plugin.getRegionManager().addRegion(name, selection.getFirst(), selection.getSecond());
        this.selectionMap.remove(key);
        return true;
    }

    public void updateSelection(Player player, Location location, BiConsumer<WandSelection, Location> consumer) {
        final WandKey key = WandKey.of(player);
        consumer.accept(this.selectionMap.computeIfAbsent(key, key1 -> new WandSelection()), location);
    }

    public void giveWand(Player player) {
        final ItemStack itemStack = new ItemStack(Material.STICK);
        final ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(Component.text("Wand").color(NamedTextColor.AQUA));
        meta.getPersistentDataContainer().set(this.key, PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(meta);
        player.getInventory().addItem(itemStack).values().forEach(drop -> player.getLocation().getWorld().dropItemNaturally(player.getLocation(), drop));
    }

    public boolean isWand(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return false;
        final ItemMeta meta = itemStack.getItemMeta();
        return meta.getPersistentDataContainer().has(this.key, PersistentDataType.BOOLEAN);
    }
}
