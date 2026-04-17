package com.pedestriamc.strings.paper.commands;

import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.paper.StringsPaper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IgnoreCommand implements CommandExecutor {

    private final StringsPaper plugin;

    public IgnoreCommand(StringsPaper plugin) {
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

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        StringsUser user = plugin.users().getUser(player.getUniqueId());
        StringsUser targetUser = plugin.users().getUser(target.getUniqueId());

        if (user.isIgnoring(targetUser)) {
            user.stopIgnoring(targetUser);
            user.sendMessage("&7No longer ignoring &f" + targetUser.getName());
        } else {
            user.ignore(targetUser);
            user.sendMessage("&7Now ignoring &f" + targetUser.getName());
        }
        return true;
    }
}
