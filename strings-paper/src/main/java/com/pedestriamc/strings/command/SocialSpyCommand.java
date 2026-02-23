package com.pedestriamc.strings.command;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SocialSpyCommand implements CommandExecutor {

    private final Strings plugin;
    private final Set<UUID> spying = new HashSet<>();

    public SocialSpyCommand(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        StringsUser user = plugin.getUserManager().getUser(player.getUniqueId());
        if (user == null) return true;

        if (!user.hasPermission("strings.socialspy")) {
            plugin.getMessenger().sendMessage(Message.NO_PERMS, user);
            return true;
        }

        if (spying.contains(player.getUniqueId())) {
            spying.remove(player.getUniqueId());
            plugin.getMessenger().sendMessage(Message.SOCIAL_SPY_OFF, user);
        } else {
            spying.add(player.getUniqueId());
            plugin.getMessenger().sendMessage(Message.SOCIAL_SPY_ON, user);
        }

        return true;
    }

    public boolean isSpying(UUID uuid) {
        return spying.contains(uuid);
    }
}
