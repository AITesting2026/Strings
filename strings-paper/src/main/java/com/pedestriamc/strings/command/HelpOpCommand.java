package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpOpCommand implements CommandExecutor {

    private final Strings plugin;

    public HelpOpCommand(Strings plugin) {
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

        if (!user.hasPermission("strings.helpop.use")) {
            plugin.getMessenger().sendMessage(Message.NO_PERMS, user);
            return true;
        }

        if (args.length < 1) {
            plugin.getMessenger().sendMessage(Message.INSUFFICIENT_ARGS, user);
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        String message = sb.toString().trim();

        String helpOpFormat = "&8[&cHelpOP&8] &7{player}&8: &f{message}"; // Should probably be in config

        String finalMessage = helpOpFormat.replace("{player}", user.getName()).replace("{message}", message);

        for (StringsUser onlineUser : plugin.getUserManager().getUsers()) {
            if (onlineUser.hasPermission("strings.helpop.receive")) {
                onlineUser.sendMessage(finalMessage);
            }
        }

        plugin.getMessenger().sendMessage(Message.HELPOP_SENT, user);
        return true;
    }
}
