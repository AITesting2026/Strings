package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MsgCommand implements CommandExecutor {

    private final Strings plugin;
    private final Map<UUID, UUID> lastMessaged = new ConcurrentHashMap<>();
    private SocialSpyCommand socialSpyCommand;

    public MsgCommand(Strings plugin) {
        this.plugin = plugin;
    }

    public void setSocialSpyCommand(SocialSpyCommand socialSpyCommand) {
        this.socialSpyCommand = socialSpyCommand;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        StringsUser user = plugin.getUserManager().getUser(player.getUniqueId());
        if (user == null) return true;

        if (!user.hasPermission("strings.chat.msg")) {
            plugin.getMessenger().sendMessage(Message.NO_PERMS, user);
            return true;
        }

        if (args.length < 2) {
            plugin.getMessenger().sendMessage(Message.INSUFFICIENT_ARGS, user);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessenger().sendMessage(Message.PLAYER_OFFLINE, user);
            return true;
        }

        StringsUser targetUser = plugin.getUserManager().getUser(target.getUniqueId());
        if (targetUser == null) return true;

        if (targetUser.getUniqueId().equals(user.getUniqueId())) {
            plugin.getMessenger().sendMessage(Message.SELF_MESSAGE, user);
            return true;
        }

        if (targetUser.isIgnoring(user) && !user.hasPermission("strings.chat.msg.admin")) {
            plugin.getMessenger().sendMessage(Message.PLAYER_IGNORED, user);
            return true;
        }

        if (!targetUser.hasDirectMessagesEnabled() && !user.hasPermission("strings.chat.msg.admin")) {
            plugin.getMessenger().sendMessage(Message.NO_PERMS_DIRECT_MESSAGE, user);
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String message = sb.toString().trim();

        sendPrivateMessage(user, targetUser, message);
        return true;
    }

    public void sendPrivateMessage(StringsUser sender, StringsUser recipient, String message) {
        String formatOut = plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Text.DIRECT_MESSAGE_FORMAT_OUT);
        String formatIn = plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Text.DIRECT_MESSAGE_FORMAT_IN);

        sender.sendMessage(formatOut.replace("{recipient_username}", recipient.getName()).replace("{message}", message));
        recipient.sendMessage(formatIn.replace("{sender_username}", sender.getName()).replace("{message}", message));

        lastMessaged.put(sender.getUniqueId(), recipient.getUniqueId());
        lastMessaged.put(recipient.getUniqueId(), sender.getUniqueId());

        // Social Spy
        String spyFormat = plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Text.SOCIAL_SPY_FORMAT);
        String spyMsg = spyFormat.replace("{sender_username}", sender.getName())
                                 .replace("{recipient_username}", recipient.getName())
                                 .replace("{message}", message);

        for (StringsUser onlineUser : plugin.getUserManager().getUsers()) {
            if (socialSpyCommand != null && socialSpyCommand.isSpying(onlineUser.getUniqueId()) && !onlineUser.getUniqueId().equals(sender.getUniqueId()) && !onlineUser.getUniqueId().equals(recipient.getUniqueId())) {
                onlineUser.sendMessage(spyMsg);
            }
        }
    }

    public Map<UUID, UUID> getLastMessaged() {
        return lastMessaged;
    }
}
