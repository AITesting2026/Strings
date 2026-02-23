package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RulesCommand implements CommandExecutor {

    private final Strings plugin;

    public RulesCommand(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String rules = plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Text.RULES_MESSAGE);
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(rules));
        return true;
    }
}
