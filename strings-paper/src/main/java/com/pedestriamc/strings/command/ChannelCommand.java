package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ChannelCommand implements CommandExecutor {

    private final Strings plugin;

    public ChannelCommand(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        StringsUser user = plugin.getUserManager().getUser(player.getUniqueId());
        if (user == null) return true;

        if (args.length == 0) {
            plugin.getMessenger().sendMessage(Message.CHANNEL_HELP, user);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "join":
                handleJoin(user, args);
                break;
            case "leave":
                handleLeave(user, args);
                break;
            case "list":
                handleList(user);
                break;
            default:
                handleSetActive(user, args[0]);
                break;
        }

        return true;
    }

    private void handleJoin(StringsUser user, String[] args) {
        if (args.length < 2) {
            plugin.getMessenger().sendMessage(Message.INSUFFICIENT_ARGS, user);
            return;
        }

        Channel channel = plugin.getChannelLoader().getChannel(args[1]);
        if (channel == null) {
            plugin.getMessenger().sendMessage(Message.UNKNOWN_CHANNEL, user);
            return;
        }

        if (!channel.allows(user)) {
            plugin.getMessenger().sendMessage(Message.NO_PERMS_CHANNEL, user);
            return;
        }

        if (user.memberOf(channel)) {
            plugin.getMessenger().sendMessage(Message.ALREADY_MEMBER, user);
            return;
        }

        user.joinChannel(channel);
        plugin.getMessenger().sendMessage(Message.CHANNEL_JOINED, user, Map.of("channel", channel.getName()));
    }

    private void handleLeave(StringsUser user, String[] args) {
        if (args.length < 2) {
            plugin.getMessenger().sendMessage(Message.INSUFFICIENT_ARGS, user);
            return;
        }

        Channel channel = plugin.getChannelLoader().getChannel(args[1]);
        if (channel == null) {
            plugin.getMessenger().sendMessage(Message.UNKNOWN_CHANNEL, user);
            return;
        }

        if (channel.getName().equalsIgnoreCase("global")) {
            plugin.getMessenger().sendMessage(Message.CANT_LEAVE_DEFAULT, user);
            return;
        }

        if (!user.memberOf(channel)) {
            plugin.getMessenger().sendMessage(Message.NOT_CHANNEL_MEMBER, user);
            return;
        }

        user.leaveChannel(channel);
        plugin.getMessenger().sendMessage(Message.LEFT_CHANNEL, user, Map.of("channel", channel.getName()));

        if (user.getActiveChannel().equals(channel)) {
            user.setActiveChannel(plugin.getChannelLoader().getChannel("global"));
        }
    }

    private void handleList(StringsUser user) {
        plugin.getMessenger().sendMessagePlain(Message.CHANNEL_LIST_HEADER, user);
        for (Channel channel : plugin.getChannelLoader().getChannels()) {
            if (channel.allows(user)) {
                plugin.getMessenger().sendMessagePlain(Message.CHANNEL_LIST_ENTRY, user, Map.of("channel", channel.getName()));
            }
        }
    }

    private void handleSetActive(StringsUser user, String channelName) {
        Channel channel = plugin.getChannelLoader().getChannel(channelName);
        if (channel == null) {
            plugin.getMessenger().sendMessage(Message.UNKNOWN_CHANNEL, user);
            return;
        }

        if (!channel.allows(user)) {
            plugin.getMessenger().sendMessage(Message.NO_PERMS_CHANNEL, user);
            return;
        }

        if (!user.memberOf(channel)) {
            user.joinChannel(channel);
        }

        user.setActiveChannel(channel);
        plugin.getMessenger().sendMessage(Message.CHANNEL_ACTIVE, user, Map.of("channel", channel.getName()));
    }
}
