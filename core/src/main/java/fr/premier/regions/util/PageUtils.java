package fr.premier.regions.util;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import org.bukkit.Material;

public class PageUtils {

    public static void addNavigationItems(ChestGui gui, PaginatedPane pane) {
        final StaticPane staticPane = new StaticPane(0, 0, 9, 6);
        final GuiItem previousPageItem = ItemBuilder.buildGuiItem(Material.STICK, "&7Previous Page");
        previousPageItem.setAction(event -> {
            if (pane.getPage() > 0) {
                pane.setPage(pane.getPage() - 1);
                gui.update();
            }
        });
        final GuiItem nextPageItem = ItemBuilder.buildGuiItem(Material.STICK, "&7Next Page");
        nextPageItem.setAction(event -> {
            if (pane.getPage() < pane.getPages() - 1) {
                pane.setPage(pane.getPage() + 1);
                gui.update();
            }
        });
        staticPane.addItem(previousPageItem, Slot.fromIndex(45));
        staticPane.addItem(nextPageItem, Slot.fromIndex(53));
        gui.addPane(staticPane);
    }
}
