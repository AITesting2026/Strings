package com.pedestriamc.strings.paper.platform;

import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.user.AbstractUser;
import com.pedestriamc.strings.common.user.UserBuilder;
import com.pedestriamc.strings.paper.StringsPaper;
import com.pedestriamc.strings.paper.channel.PaperLocality;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PaperStringsUser extends AbstractUser {

    private final StringsPaper plugin;

    public PaperStringsUser(UserBuilder<PaperStringsUser> builder) {
        super(builder);
        this.plugin = (StringsPaper) builder.getStrings();
    }

    @Override
    public @NotNull String getName() {
        Player player = Bukkit.getPlayer(getUniqueId());
        return player != null ? player.getName() : "Unknown";
    }

    @Override
    public @NotNull String getPrefix() {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player == null) return "";
        String prefix = plugin.getLuckPermsHook().getPrefix(player);
        if (prefix.isEmpty()) {
            prefix = plugin.getVaultHook().getPrefix(player);
        }
        return prefix;
    }

    @Override
    public void setPrefix(@NotNull String prefix) {
    }

    @Override
    public @NotNull String getSuffix() {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player == null) return "";
        String suffix = plugin.getLuckPermsHook().getSuffix(player);
        if (suffix.isEmpty()) {
            suffix = plugin.getVaultHook().getSuffix(player);
        }
        return suffix;
    }

    @Override
    public void setSuffix(@NotNull String suffix) {
    }

    @Override
    public @NotNull String getDisplayName() {
        Player player = Bukkit.getPlayer(getUniqueId());
        return player != null ? LegacyComponentSerializer.legacyAmpersand().serialize(player.displayName()) : getName();
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {
    }

    @Override
    public @NotNull Audience audience() {
        Player player = Bukkit.getPlayer(getUniqueId());
        return player != null ? player : Audience.empty();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        }
    }

    @Override
    public void sendMessage(@NotNull Component component) {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            player.sendMessage(component);
        }
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        Player player = Bukkit.getPlayer(getUniqueId());
        return player != null && player.hasPermission(permission);
    }

    @Override
    public boolean isOperator() {
        Player player = Bukkit.getPlayer(getUniqueId());
        return player != null && player.isOp();
    }

    @Override
    public double distanceSquared(@NotNull StringsUser user) {
        Player player = Bukkit.getPlayer(getUniqueId());
        Player other = Bukkit.getPlayer(user.getUniqueId());
        if (player != null && other != null && player.getWorld().equals(other.getWorld())) {
            return player.getLocation().distanceSquared(other.getLocation());
        }
        return Double.MAX_VALUE;
    }

    @Override
    public @NotNull String getChatColor() {
        return getChatColorComponent().toString();
    }

    @Override
    public Locality<?> getLocality() {
        Player player = Bukkit.getPlayer(getUniqueId());
        return player != null ? new PaperLocality(plugin, player.getWorld()) : null;
    }

    @Override
    public double getX() {
        Player player = Bukkit.getPlayer(getUniqueId());
        return player != null ? player.getLocation().getX() : 0;
    }

    @Override
    public double getY() {
        Player player = Bukkit.getPlayer(getUniqueId());
        return player != null ? player.getLocation().getY() : 0;
    }

    @Override
    public double getZ() {
        Player player = Bukkit.getPlayer(getUniqueId());
        return player != null ? player.getLocation().getZ() : 0;
    }
}
