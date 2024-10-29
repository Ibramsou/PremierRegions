package fr.premier.regions.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {

    public static final Map<JavaPlugin, List<Command>> commandMap = new HashMap<>();

    public static void register(final JavaPlugin plugin, final Command... commands) {
        for (Command command : commands) {
            Bukkit.getCommandMap().register(plugin.getName().toLowerCase(), command);
            commandMap.computeIfAbsent(plugin, plugin1 -> new ArrayList<>()).add(command);
        }
    }

    public static void unregister(JavaPlugin plugin) {
        List<Command> commands = commandMap.remove(plugin);
        if (commands == null) return;
        commands.forEach(abstractCommand -> {
            Bukkit.getCommandMap().getKnownCommands().remove(abstractCommand.getName());
            Bukkit.getCommandMap().getKnownCommands().remove(plugin.getName().toLowerCase() + ":" + abstractCommand.getName());
            for (String alias : abstractCommand.getAliases()) {
                Bukkit.getCommandMap().getKnownCommands().remove(alias);
                Bukkit.getCommandMap().getKnownCommands().remove(plugin.getName().toLowerCase() + ":" + alias);
            }
        });
    }
}
