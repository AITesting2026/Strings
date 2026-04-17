package com.pedestriamc.strings.paper.commands;

import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.paper.StringsPaper;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand implements CommandExecutor {

    private final StringsPaper plugin;

    public BroadcastCommand(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }

        String message = String.join(" ", args);
        String format = plugin.settings().get(Option.Text.BROADCAST_FORMAT);
        String broadcast = format + message;

        plugin.getServer().broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(broadcast));
        return true;
    }
}
