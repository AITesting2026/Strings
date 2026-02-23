package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.message.MessengerImpl;
import com.pedestriamc.strings.settings.SettingsImpl;
import com.pedestriamc.strings.text.EmojiManagerImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StringsCommand implements CommandExecutor {

    private final Strings plugin;

    public StringsCommand(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(Component.text("Strings version " + plugin.getDescription().getVersion() + " by " + String.join(", ", plugin.getDescription().getAuthors())));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("strings.reload")) {
                sender.sendMessage(Component.text("No permission."));
                return true;
            }
            plugin.reloadConfig();
            ((SettingsImpl) plugin.getSettings()).reload();
            ((MessengerImpl) plugin.getMessenger()).loadMessages();
            ((EmojiManagerImpl) plugin.getEmojiManager()).loadEmojis();
            plugin.getChannelLoader().refresh();
            sender.sendMessage(Component.text("Strings reloaded."));
            return true;
        }

        if (args[0].equalsIgnoreCase("delete") && args.length > 1) {
            if (!sender.hasPermission("strings.chat.delete-messages")) {
                sender.sendMessage(Component.text("No permission."));
                return true;
            }
            // Clear chat for all to "delete" the message
            for (int i = 0; i < 100; i++) {
                Bukkit.broadcast(Component.text(""));
            }
            Bukkit.broadcast(Component.text("A message was deleted by " + sender.getName(), NamedTextColor.RED));
            return true;
        }

        return true;
    }
}
