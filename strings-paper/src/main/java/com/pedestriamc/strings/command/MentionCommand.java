package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MentionCommand implements CommandExecutor {

    private final Strings plugin;

    public MentionCommand(Strings plugin) {
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

        if (!user.hasPermission("strings.mention.toggle")) {
            plugin.getMessenger().sendMessage(Message.NO_PERMS, user);
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("enable")) {
                user.setMentionsEnabled(true);
                plugin.getMessenger().sendMessage(Message.MENTIONS_ENABLED, user);
                return true;
            } else if (args[0].equalsIgnoreCase("disable")) {
                user.setMentionsEnabled(false);
                plugin.getMessenger().sendMessage(Message.MENTIONS_DISABLED, user);
                return true;
            }
        }

        boolean newState = !user.isMentionsEnabled();
        user.setMentionsEnabled(newState);
        plugin.getMessenger().sendMessage(newState ? Message.MENTIONS_ENABLED : Message.MENTIONS_DISABLED, user);

        return true;
    }
}
