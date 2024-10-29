package fr.premier.regions;

import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.data.PlayerData;
import fr.premier.regions.flag.Flag;
import fr.premier.regions.region.Region;
import fr.premier.regions.region.gui.RegionEditorGui;
import fr.premier.regions.region.gui.RegionPagesGUI;
import fr.premier.regions.util.OfflinePlayerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RegionsCommand extends Command {

    private final RegionsPlugin plugin;

    public RegionsCommand(RegionsPlugin plugin) {
        super("regions");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {

            if (args.length == 0) {
                new RegionPagesGUI(RegionsPlugin.getInstance()).show(player);
                return false;
            }

            final String argument = args[0].toLowerCase();

            final Region guiRegion = this.plugin.getRegionManager().getRegion(player.getWorld(), argument);

            if (guiRegion != null) {
                new RegionEditorGui(guiRegion, this.plugin).show(player);
                return false;
            }

            if (argument.equals("wand")) {
                this.plugin.getWandManager().giveWand(player);
                return false;
            }

            if (args.length < 2) {
                sendHelp(sender);
                return false;
            }

            final String name = args[1].toLowerCase();
            final Region region =  this.plugin.getRegionManager().getRegion(player.getWorld(), name);;

            if (argument.equals("create")) {
                if (region != null) {
                    sender.sendMessage("§cThis region already exists");
                    return false;
                }
            } else if (region == null) {
                sender.sendMessage("§cThis region doesn't exists");
                return false;
            }

            switch (argument) {
                case "create": {
                    this.plugin.getWandManager().getRemoveSelection(player, wandSelection -> {
                        this.plugin.getRegionManager().addRegion(name, wandSelection.getFirst(), wandSelection.getSecond()).thenAccept(createdRegion -> {
                            sender.sendMessage("§aYou created the region " + createdRegion.getName());
                        });
                    }, () -> {
                        sender.sendMessage("§cPlease select a region first.");
                    });
                }
                break;
                case "delete": {
                    this.plugin.getRegionManager().deleteRegion(region, () -> sender.sendMessage("§cYou deleted the region " + region.getName()));
                }
                break;
                case "remove":
                case "add": {
                    if (args.length < 3) {
                        sendHelp(sender);
                        return false;
                    }

                    OfflinePlayerUtil.getOfflinePlayerByName(args[2], false, offlinePlayer -> {
                        final PlayerData playerData = this.plugin.getPlayerDataManager().getDirectPlayerData(offlinePlayer.getUniqueId());
                        boolean contains = playerData.getWhitelistedRegions().contains(region);
                        final boolean add = argument.equals("add");
                        if (contains == add) {
                            sender.sendMessage(add ? "§cThis player already whitelsted to this region" : "§cThis player is not whitelisted to this region");
                            return;
                        }

                        playerData.editWhitelist(regions -> {
                            if (add) {
                                regions.add(region);
                                sender.sendMessage("§aYou added " + offlinePlayer.getName() + " from the region " + region.getName());
                            } else {
                                regions.remove(region);
                                sender.sendMessage("§aYou removed " + offlinePlayer.getName() + " from the region " + region.getName());
                            }
                        });
                    }, () -> sender.sendMessage("§cThis player never connected the server."));
                }
                case "flag": {
                    if (args.length < 4) {
                        sendHelp(sender);
                        return false;
                    }

                    final Flag flag = this.plugin.getFlagManager().getDefaultFlag(args[2]);
                    if (flag == null) {
                        sender.sendMessage("§cThis flag doesn't exists.");
                        return false;
                    }

                    final FlagState state;
                    try {
                        state = FlagState.valueOf(args[3].toUpperCase());
                    } catch (Exception exception) {
                        sender.sendMessage("§cIncorrect flag state.");
                        return false;
                    }

                    this.plugin.getRegionManager().setFlagState(region, flag, state);
                    sender.sendMessage("§aUpdate flag " + flag.getDisplayName() + " of region " + region.getName() + " to " + state.name());
                }
                break;
                case "whitelist": {

                }
                break;
            }
        }
        return false;
    }

    private void sendHelp(CommandSender sender) {

    }
}
