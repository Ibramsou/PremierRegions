package fr.premier.regions.region.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.region.Region;
import fr.premier.regions.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class RegionPagesGUI extends ChestGui {

    private final RegionsPlugin plugin;

    public RegionPagesGUI(RegionsPlugin plugin) {
        super(6, "");
        this.plugin = plugin;
        final List<GuiItem> items = new ArrayList<>();
        RegionsPlugin.getInstance().getRegionManager().getRegions().values().forEach(region -> {
            GuiItem guiItem = ItemBuilder.buildGuiItem(Material.CHEST, "&a" + region.getName());
            guiItem.setAction(event -> this.onRegionClick(region, event));
            items.add(guiItem);
        });
        final PaginatedPane pane = new PaginatedPane(0, 0, 6, 9);
        pane.populateWithGuiItems(items);
        this.addPane(pane);
    }

    private void onRegionClick(Region region, InventoryClickEvent event) {
        new RegionEditorGui(region, this.plugin).show(event.getWhoClicked());
    }
}
