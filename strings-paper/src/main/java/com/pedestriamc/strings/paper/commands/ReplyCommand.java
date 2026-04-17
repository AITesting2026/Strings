package com.pedestriamc.strings.paper.commands;

import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.paper.StringsPaper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReplyCommand implements CommandExecutor {

    private final StringsPaper plugin;

    public ReplyCommand(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        StringsUser s = plugin.users().getUser(player.getUniqueId());
        String message = String.join(" ", args);

        plugin.getDirectMessageManager().reply(s, message);
        return true;
    }
}
