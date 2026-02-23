package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ReplyCommand implements CommandExecutor {

    private final Strings plugin;
    private final MsgCommand msgCommand;

    public ReplyCommand(Strings plugin, MsgCommand msgCommand) {
        this.plugin = plugin;
        this.msgCommand = msgCommand;
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

        if (args.length < 1) {
            plugin.getMessenger().sendMessage(Message.INSUFFICIENT_ARGS, user);
            return true;
        }

        UUID targetUuid = msgCommand.getLastMessaged().get(player.getUniqueId());
        if (targetUuid == null) {
            plugin.getMessenger().sendMessage(Message.NO_REPLY, user);
            return true;
        }

        StringsUser targetUser = plugin.getUserManager().getUser(targetUuid);
        if (targetUser == null) {
            plugin.getMessenger().sendMessage(Message.PLAYER_OFFLINE, user);
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        String message = sb.toString().trim();

        msgCommand.sendPrivateMessage(user, targetUser, message);
        return true;
    }
}
