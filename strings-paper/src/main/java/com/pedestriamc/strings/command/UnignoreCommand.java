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

public class UnignoreCommand implements CommandExecutor {

    private final Strings plugin;

    public UnignoreCommand(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /unignore <player-to-unignore> <player-ignoring (optional)>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        StringsUser targetUser = plugin.getUserManager().getUser(target.getUniqueId());
        if (targetUser == null) return true;

        StringsUser subject;
        if (args.length >= 2) {
            if (!sender.hasPermission("strings.chat.ignore.other")) {
                sender.sendMessage("No permission to unignore for others.");
                return true;
            }
            Player subjectPlayer = Bukkit.getPlayer(args[1]);
            if (subjectPlayer == null) {
                sender.sendMessage("Subject player not found.");
                return true;
            }
            subject = plugin.getUserManager().getUser(subjectPlayer.getUniqueId());
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Console must specify a subject player.");
                return true;
            }
            subject = plugin.getUserManager().getUser(player.getUniqueId());
        }

        if (subject == null) return true;

        if (!subject.isIgnoring(targetUser)) {
            plugin.getMessenger().sendMessage(Message.NOT_IGNORED, subject);
            return true;
        }

        subject.stopIgnoring(targetUser);
        plugin.getMessenger().sendMessage(Message.PLAYER_UNIGNORED, subject, Map.of("player", targetUser.getName()));

        return true;
    }
}
