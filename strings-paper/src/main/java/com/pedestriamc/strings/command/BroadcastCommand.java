package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.pedestriamc.strings.api.settings.Option;

public class BroadcastCommand implements CommandExecutor {

    private final Strings plugin;

    public BroadcastCommand(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("strings.chat.broadcast")) {
            sender.sendMessage("No permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /broadcast <text>");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        String message = sb.toString().trim();

        String broadcastFormat = plugin.getSettings().get(Option.Text.BROADCAST_FORMAT);
        Bukkit.broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(broadcastFormat + message));

        return true;
    }
}
