package com.pedestriamc.strings.paper.platform;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.paper.StringsPaper;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaperPlatformAdapter implements PlatformAdapter {

    private final StringsPaper plugin;

    public PaperPlatformAdapter(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Collection<StringsUser> getOnlineUsers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(player -> plugin.users().getUser(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull Collection<StringsUser> getOperators() {
        return Bukkit.getOperators().stream()
                .map(offlinePlayer -> plugin.users().getUser(offlinePlayer.getUniqueId()))
                .collect(Collectors.toList());
    }

    @Override
    public String colorHex(@NotNull String input) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(input));
    }

    @Override
    public String translateBukkitColor(@NotNull String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    @Override
    public String stripBukkitColor(@NotNull String input) {
        return ChatColor.stripColor(input);
    }

    @Override
    public String applyPlaceholders(@NotNull StringsUser source, @NotNull String input) {
        if (plugin.isUsingPlaceholderAPI()) {
            Player player = Bukkit.getPlayer(source.getUniqueId());
            if (player != null) {
                return PlaceholderAPI.setPlaceholders(player, input);
            }
        }
        return input;
    }

    @Override
    public String processMentions(@NotNull StringsUser sender, @NotNull Channel channel, @NotNull String str) {
        // This is typically handled by MessageProcessor/Mentioner
        return str;
    }

    @Override
    public String setPlaceholders(@NotNull StringsUser user, @NotNull String input) {
        return applyPlaceholders(user, input);
    }

    @Override
    public @Nullable TextColor parseColor(@NotNull String input) {
        if (input.startsWith("#")) {
            return TextColor.fromHexString(input);
        }
        return switch (input.toLowerCase()) {
            case "black" -> TextColor.color(0, 0, 0);
            case "dark_blue" -> TextColor.color(0, 0, 170);
            case "dark_green" -> TextColor.color(0, 170, 0);
            case "dark_aqua" -> TextColor.color(0, 170, 170);
            case "dark_red" -> TextColor.color(170, 0, 0);
            case "dark_purple" -> TextColor.color(170, 0, 170);
            case "gold" -> TextColor.color(255, 170, 0);
            case "gray" -> TextColor.color(170, 170, 170);
            case "dark_gray" -> TextColor.color(85, 85, 85);
            case "blue" -> TextColor.color(85, 85, 255);
            case "green" -> TextColor.color(85, 255, 85);
            case "aqua" -> TextColor.color(85, 255, 255);
            case "red" -> TextColor.color(255, 85, 85);
            case "light_purple" -> TextColor.color(255, 85, 255);
            case "yellow" -> TextColor.color(255, 255, 85);
            case "white" -> TextColor.color(255, 255, 255);
            default -> null;
        };
    }

    @Override
    public void removePermission(@NotNull String... permission) {
    }

    @Override
    public void addPermission(@NotNull String... permission) {
    }

    @Override
    public void print(@NotNull String message) {
        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }

    @Override
    public boolean isOnline(@NotNull UUID uuid) {
        return Bukkit.getPlayer(uuid) != null;
    }

    @Override
    public boolean isOnline(@NotNull StringsUser user) {
        return isOnline(user.getUniqueId());
    }
}
