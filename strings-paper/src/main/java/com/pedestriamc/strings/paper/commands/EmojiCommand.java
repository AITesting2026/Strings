package com.pedestriamc.strings.paper.commands;

import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.paper.StringsPaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EmojiCommand implements CommandExecutor {

    private final StringsPaper plugin;

    public EmojiCommand(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("strings.chat.emoji")) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cNo permission."));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        player.sendMessage(plugin.settings().getComponent(Option.Text.EMOJI_COMMAND_HEADER));

        StringBuilder sb = new StringBuilder();
        plugin.emojiManager().mappings().forEach((name, unicode) -> {
            sb.append("&7:").append(name).append(": ").append(unicode).append("  ");
        });
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(sb.toString().trim()));

        player.sendMessage(plugin.settings().getComponent(Option.Text.EMOJI_COMMAND_FOOTER));
        return true;
    }
}
