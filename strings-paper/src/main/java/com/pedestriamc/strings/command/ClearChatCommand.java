package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearChatCommand implements CommandExecutor {

    private final Strings plugin;

    public ClearChatCommand(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean all = args.length > 0 && args[0].equalsIgnoreCase("all");

        if (all) {
            if (!sender.hasPermission("strings.chat.clear.others")) {
                sender.sendMessage("No permission to clear everyone's chat.");
                return true;
            }
            for (StringsUser user : plugin.getUserManager().getUsers()) {
                clear(user);
                plugin.getMessenger().sendMessage(Message.CHAT_CLEARED_ALL, user);
            }
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Console must specify 'all'.");
                return true;
            }
            StringsUser user = plugin.getUserManager().getUser(player.getUniqueId());
            if (user == null) return true;
            if (!user.hasPermission("strings.chat.clear")) {
                plugin.getMessenger().sendMessage(Message.NO_PERMS, user);
                return true;
            }
            clear(user);
            plugin.getMessenger().sendMessage(Message.CHAT_CLEARED, user);
        }

        return true;
    }

    private void clear(StringsUser user) {
        for (int i = 0; i < 100; i++) {
            user.sendMessage("");
        }
    }
}
