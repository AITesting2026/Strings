package com.pedestriamc.strings.paper.commands;

import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.paper.StringsPaper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SocialSpyCommand implements CommandExecutor {

    private final StringsPaper plugin;
    private final Set<UUID> spying = new HashSet<>();

    public SocialSpyCommand(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        UUID uuid = player.getUniqueId();
        if (spying.contains(uuid)) {
            spying.remove(uuid);
            player.sendMessage("&7SocialSpy &cdisabled&7.");
        } else {
            spying.add(uuid);
            player.sendMessage("&7SocialSpy &aenabled&7.");
        }
        return true;
    }

    public boolean isSpying(UUID uuid) {
        return spying.contains(uuid);
    }
}
