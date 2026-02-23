package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ChatColorCommand implements CommandExecutor {

    private final Strings plugin;

    public ChatColorCommand(Strings plugin) {
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

        if (!user.hasPermission("strings.chat.chatcolor")) {
            plugin.getMessenger().sendMessage(Message.NO_PERMS, user);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /chatcolor <color>");
            return true;
        }

        String color = args[0];
        // Basic validation - check if it's a valid color code or hex
        if (!color.startsWith("&") && !color.startsWith("#")) {
             color = "&" + color;
        }

        user.setChatColor(color);
        plugin.getMessenger().sendMessage(Message.CHATCOLOR_SET, user, Map.of("color", color));

        return true;
    }
}
