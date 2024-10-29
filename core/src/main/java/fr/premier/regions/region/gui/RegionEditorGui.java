package fr.premier.regions.region.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import fr.premier.regions.RegionsPlugin;
import fr.premier.regions.data.PlayerData;
import fr.premier.regions.region.Region;
import fr.premier.regions.util.ItemBuilder;
import fr.premier.regions.util.OfflinePlayerUtil;
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
        this.plugin.getStageManager().createChatMessageStage(event, (player, message) -> {
            if (this.plugin.getRegionManager().getRegion(this.region.getFirstLocation().getWorld(), message) != null) {
                player.sendMessage(Component.text("A region with that name already exists in this world").color(NamedTextColor.RED));
                return;
            }
            this.region.setName(message);
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getDatabase().updateRegionName(region));
        });
    }

    private void onWhitelistAdd(InventoryClickEvent event) {
        this.plugin.getStageManager().createChatMessageStage(event, (player, message) -> OfflinePlayerUtil.getOfflinePlayerByName(message, false, offlinePlayer -> {
            final PlayerData playerData = this.plugin.getPlayerDataManager().getDirectPlayerData(offlinePlayer.getUniqueId());
            if (playerData.getBinaryWhitelistedRegions().getValue().contains(this.region)) {
                player.sendMessage(Component.text("This player is already whitelisted to this region.").color(NamedTextColor.RED));
                return;
            }

            playerData.getBinaryWhitelistedRegions().getUpdateValue(regions -> {
                regions.add(this.region);
                playerData.save();
                player.sendMessage(Component.text("You whitelisted " + offlinePlayer.getName() + " to this region.").color(NamedTextColor.GREEN));
            });
        }, () -> player.sendMessage(Component.text("This player never connected to server").color(NamedTextColor.RED))));
    }

    private void onWhitelistRemove(InventoryClickEvent event) {
        this.plugin.getStageManager().createChatMessageStage(event, (player, message) -> OfflinePlayerUtil.getOfflinePlayerByName(message, false, offlinePlayer -> {
            final PlayerData playerData = this.plugin.getPlayerDataManager().getDirectPlayerData(offlinePlayer.getUniqueId());
            if (!playerData.getBinaryWhitelistedRegions().getValue().contains(this.region)) {
                player.sendMessage(Component.text("This player is not whitelisted to this region.").color(NamedTextColor.RED));
                return;
            }

            playerData.getBinaryWhitelistedRegions().getUpdateValue(regions -> {
                regions.remove(this.region);
                playerData.save();
                player.sendMessage(Component.text("You un-whitelisted " + offlinePlayer.getName() + " to this region.").color(NamedTextColor.RED));
            });
        }, () -> player.sendMessage(Component.text("This player never connected to server").color(NamedTextColor.RED))));
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
        new RegionFlagsGUI(this.region, this.plugin).show(event.getWhoClicked());
    }

}
