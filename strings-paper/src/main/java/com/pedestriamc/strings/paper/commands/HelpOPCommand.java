package com.pedestriamc.strings.paper.commands;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.paper.StringsPaper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpOPCommand implements CommandExecutor {

    private final StringsPaper plugin;

    public HelpOPCommand(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        StringsUser user = plugin.users().getUser(player.getUniqueId());
        Channel helpopChannel = plugin.getChannelLoader().getChannel("helpop");

        if (helpopChannel == null) {
            user.sendMessage("&cHelpOP is not enabled.");
            return true;
        }

        String message = String.join(" ", args);
        helpopChannel.sendMessage(user, message);
        return true;
    }
}
