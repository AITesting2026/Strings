package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StringsCommand implements CommandExecutor {

    private final Strings plugin;

    public StringsCommand(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("version")) {
            sender.sendMessage("Strings version " + plugin.getDescription().getVersion() + " by " + String.join(", ", plugin.getDescription().getAuthors()));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("strings.reload")) {
                sender.sendMessage("No permission.");
                return true;
            }
            plugin.reloadConfig();
            ((com.pedestriamc.strings.message.MessengerImpl)plugin.getMessenger()).loadMessages();
            ((com.pedestriamc.strings.text.EmojiManagerImpl)plugin.getEmojiManager()).loadEmojis();
            plugin.getChannelLoader().refresh();
            sender.sendMessage("Strings reloaded.");
            return true;
        }

        if (args[0].equalsIgnoreCase("delete") && args.length > 1) {
            if (!sender.hasPermission("strings.chat.delete-messages")) {
                sender.sendMessage("No permission.");
                return true;
            }
            for (int i = 0; i < 100; i++) {
                Bukkit.broadcast(net.kyori.adventure.text.Component.text(""));
            }
            Bukkit.broadcast(net.kyori.adventure.text.Component.text("A message was deleted by " + sender.getName(), net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }

        return true;
    }
}
