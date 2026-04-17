package com.pedestriamc.strings.paper.commands;

import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.paper.StringsPaper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatColorCommand implements CommandExecutor {

    private final StringsPaper plugin;

    public ChatColorCommand(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        StringsUser user = plugin.users().getUser(player.getUniqueId());
        String color = args[0];

        user.setChatColor(color);
        user.sendMessage("&7Chat color set to: " + color + "This is a test message.");
        return true;
    }
}
