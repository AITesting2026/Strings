package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EmojiCommand implements CommandExecutor {

    private final Strings plugin;

    public EmojiCommand(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String header = plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Text.EMOJI_COMMAND_HEADER);
        String footer = plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Text.EMOJI_COMMAND_FOOTER);

        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(header));
        for (Map.Entry<String, String> entry : plugin.getEmojiManager().mappings().entrySet()) {
            sender.sendMessage(entry.getKey() + " -> " + entry.getValue());
        }
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(footer));

        return true;
    }
}
