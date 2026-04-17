package com.pedestriamc.strings.paper.commands;

import com.pedestriamc.strings.paper.StringsPaper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StringsCommand implements CommandExecutor {

    private final StringsPaper plugin;

    public StringsCommand(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Strings version " + plugin.getDescription().getVersion());
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("strings.reload")) {
                sender.sendMessage("No permission.");
                return true;
            }
            ((com.pedestriamc.strings.paper.platform.PaperFileManager) plugin.files()).reload();
            sender.sendMessage("Strings reloaded.");
            return true;
        }

        return false;
    }
}
