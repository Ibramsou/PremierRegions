package fr.premier.regions.util;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    public static ItemStack buildItem(Material material, String displayName) {
        return buildItem(material, displayName, null);
    }

    public static ItemStack buildItem(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(toItemSafeComponent(displayName));
        if (lore != null) meta.lore(toItemSafeComponnentList(lore));
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

    public static TextComponent toTextComponent(String input) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
    }

    public static TextComponent toItemSafeComponent(String input) {
        TextComponent component = toTextComponent(input);
        if (component.hasDecoration(TextDecoration.ITALIC)) {
            return component;
        }

        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static List<TextComponent> toItemSafeComponnentList(List<String> input) {
        final List<TextComponent> result = new ArrayList<>(input.size());
        input.forEach(s -> result.add(toItemSafeComponent(s)));
        return result;
    }

}
