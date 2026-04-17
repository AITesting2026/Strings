package com.pedestriamc.strings.paper.commands;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.paper.StringsPaper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChannelCommand implements CommandExecutor {

    private final StringsPaper plugin;

    public ChannelCommand(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        StringsUser target = plugin.users().getUser(player.getUniqueId());
        boolean other = false;

        if (args.length > 0) {
            String lastArg = args[args.length - 1];
            Player targetPlayer = Bukkit.getPlayer(lastArg);
            if (targetPlayer != null && args.length > 1) {
                if (!player.hasPermission("strings.channel.modifyplayers")) {
                    player.sendMessage("&cNo permission to modify other players.");
                    return true;
                }
                target = plugin.users().getUser(targetPlayer.getUniqueId());
                other = true;
            }
        }

        if (args.length == 0 || (other && args.length == 1)) {
            player.sendMessage("&7Active channel for " + target.getName() + ": &f" + target.getActiveChannel().getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("join")) {
            if (args.length < 2) return false;
            Channel channel = plugin.getChannelLoader().getChannel(args[1]);
            if (channel == null) {
                player.sendMessage("&cChannel not found.");
                return true;
            }
            target.joinChannel(channel);
            player.sendMessage("&7" + (other ? target.getName() + " joined" : "Joined") + " channel: &f" + channel.getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            if (args.length < 2) return false;
            Channel channel = plugin.getChannelLoader().getChannel(args[1]);
            if (channel == null) {
                player.sendMessage("&cChannel not found.");
                return true;
            }
            target.leaveChannel(channel);
            player.sendMessage("&7" + (other ? target.getName() + " left" : "Left") + " channel: &f" + channel.getName());
            return true;
        }

        Channel channel = plugin.getChannelLoader().getChannel(args[0]);
        if (channel == null) {
            player.sendMessage("&cChannel not found.");
            return true;
        }

        target.setActiveChannel(channel);
        player.sendMessage("&7Set active channel for " + (other ? target.getName() : "yourself") + " to: &f" + channel.getName());
        return true;
    }
}
