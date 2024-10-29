package fr.premier.regions.region.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.flag.Flag;
import fr.premier.regions.region.Region;
import fr.premier.regions.util.ItemBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class RegionFlagsGUI extends ChestGui {

    private final Region region;
    private final RegionsPlugin plugin;

    public RegionFlagsGUI(Region region, RegionsPlugin plugin) {
        super(6, "§bRegions Flags");
        this.region = region;
        this.plugin = plugin;

        final PaginatedPane pane = new PaginatedPane(0, 0, 6, 9);
        final List<GuiItem> items = new ArrayList<>();
        this.plugin.getFlagManager().getFlags().values().forEach(flag -> {
            GuiItem guiItem = ItemBuilder.buildGuiItem(Material.PAPER, "&7" + flag.getDisplayName(), this.buildStateLore(flag));
            guiItem.setAction(event -> {
                this.onFlagClick(flag);
                guiItem.setItem(ItemBuilder.buildItem(Material.PAPER, "&7" + flag.getDisplayName(), this.buildStateLore(flag)));
                this.update();
            });
            items.add(guiItem);
        });
        pane.populateWithGuiItems(items);
        this.addPane(pane);
    }

    public void onFlagClick(Flag flag) {
        final FlagState currentState = this.plugin.getRegionManager().getFlagState(region, flag);
        final int ordinal = currentState.ordinal();
        int next = 0;
        if (ordinal != FlagState.values().length - 1) {
            next = ordinal + 1;
        }
        this.plugin.getRegionManager().setFlagState(region, flag, FlagState.values()[next]);
    }

    private List<String> buildStateLore(Flag flag) {
        return List.of(
                "",
                "&7State: &a" + this.plugin.getRegionManager().getFlagState(this.region, flag));
    }
}
