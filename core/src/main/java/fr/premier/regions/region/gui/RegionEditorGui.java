package fr.premier.regions.region.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.region.Region;
import fr.premier.regions.stage.StageResult;
import fr.premier.regions.stage.impl.ChatStage;
import fr.premier.regions.util.ItemBuilder;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RegionEditorGui extends ChestGui {

    private final Region region;
    private final RegionsPlugin plugin;

    public RegionEditorGui(Region region, RegionsPlugin plugin) {
        super(1, "§a" + region.getName());
        this.region = region;
        this.plugin = plugin;

        final StaticPane pane = new StaticPane(0, 0, 9, 1);
        final GuiItem rename = ItemBuilder.buildGuiItem(Material.NAME_TAG, "&eRename");
        final GuiItem whitelistAdd = ItemBuilder.buildGuiItem(Material.SLIME_BALL, "&aAdd player");
        final GuiItem whitelistRemove = ItemBuilder.buildGuiItem(Material.REDSTONE, "&cRemove player");
        final GuiItem redefineLocation = ItemBuilder.buildGuiItem(Material.ARMOR_STAND, "&bRedefine location");
        final GuiItem editFlags = ItemBuilder.buildGuiItem(Material.PAPER, "&3Edit flags");

        rename.setAction(this::onRename);
        whitelistAdd.setAction(this::onWhitelistAdd);
        whitelistRemove.setAction(this::onWhitelistRemove);
        redefineLocation.setAction(this::onRedefineLocation);
        editFlags.setAction(this::onOpenFlagEditor);

        pane.addItem(rename, Slot.fromIndex(0));
        pane.addItem(whitelistAdd, Slot.fromIndex(1));
        pane.addItem(whitelistRemove, Slot.fromIndex(2));
        pane.addItem(redefineLocation, Slot.fromIndex(3));
        pane.addItem(editFlags, Slot.fromIndex(4));
    }

    private void onRename(InventoryClickEvent event) {
        this.plugin.getStageManager().addStage((Player) event.getWhoClicked(), (ChatStage) (chatEvent, message) -> {
            if (message.split(" ", 2).length > 1) {
                chatEvent.getPlayer().sendMessage(Component.text("Please type a correct name").color(NamedTextColor.RED));
                return StageResult.CANCELLED;
            }

            this.region.setName(message);
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getDatabase().updateRegionName(region));
            return StageResult.DONE;
        });
    }

    private void onWhitelistAdd(InventoryClickEvent event) {

    }

    private void onWhitelistRemove(InventoryClickEvent event) {

    }

    private void onRedefineLocation(InventoryClickEvent event) {
        this.plugin.getWandManager().getRemoveSelection((Player) event.getWhoClicked(), this.region.getName(), wandSelection -> {
            this.region.setFirstLocation(wandSelection.getFirst());
            this.region.setSecondLocation(wandSelection.getSecond());
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getDatabase().updateRegionPositions(region));
        }, () -> {
            event.getWhoClicked().sendMessage("§cPlease select a region first with /wand");
        });
    }

    private void onOpenFlagEditor(InventoryClickEvent event) {

    }
}
