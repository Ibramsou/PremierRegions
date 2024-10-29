package fr.premier.regions.util;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

    public static ItemStack buildItem(Material material, String displayName) {
        return buildItem(material, displayName, null);
    }

    public static ItemStack buildItem(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(displayName));
        if (lore != null) meta.lore(lore.stream().map(s -> LegacyComponentSerializer.legacyAmpersand().deserialize(displayName)).toList());
        item.setItemMeta(meta);
        return item;
    }

    public static GuiItem buildGuiItem(Material material, String displayName) {
        return buildGuiItem(material, displayName, null);
    }

    public static GuiItem buildGuiItem(Material material, String displayName, List<String> lore) {
        ItemStack item = buildItem(material, displayName, lore);
        return new GuiItem(item);
    }
}
