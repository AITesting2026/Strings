package com.pedestriamc.strings.paper.commands;

import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.paper.StringsPaper;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearChatCommand implements CommandExecutor {

    private final StringsPaper plugin;

    public ClearChatCommand(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            if (!sender.hasPermission("strings.chat.clear.others")) {
                sender.sendMessage("No permission.");
                return true;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                clear(player);
            }
            Bukkit.broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize("&7Chat has been cleared by &f" + sender.getName()));
        } else {
            if (sender instanceof Player player) {
                clear(player);
                player.sendMessage("You cleared your chat.");
            }
        }
        return true;
    }

    private void clear(Player player) {
        for (int i = 0; i < 100; i++) {
            player.sendMessage("");
        }
    }
}
