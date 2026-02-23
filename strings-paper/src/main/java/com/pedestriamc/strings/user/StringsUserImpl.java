package com.pedestriamc.strings.user;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.discord.Snowflake;
import com.pedestriamc.strings.api.text.format.StringsComponent;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StringsUserImpl implements StringsUser {

    private final UUID uuid;
    private final String name;
    private StringsComponent chatColor;
    private String prefix = "";
    private String suffix = "";
    private String displayName;
    private Channel activeChannel;
    private final Set<Channel> channels = new HashSet<>();
    private boolean mentionsEnabled = true;
    private final Set<UUID> ignoredPlayers = new HashSet<>();
    private final Set<Channel> monitoredChannels = new HashSet<>();
    private final Set<Channel> mutedChannels = new HashSet<>();
    private boolean directMessagesEnabled = true;
    private Snowflake discordId = Snowflake.empty();
    private boolean isNew = true;

    public StringsUserImpl(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.displayName = name;
        this.chatColor = StringsComponent.fromString("&f");
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    @Deprecated
    public @Nullable String getChatColor() {
        return chatColor.toString();
    }

    @Override
    @Deprecated
    public void setChatColor(String chatColor) {
        this.chatColor = StringsComponent.fromString(chatColor);
    }

    @Override
    public StringsComponent getChatColorComponent() {
        return chatColor;
    }

    @Override
    public void setChatColorComponent(StringsComponent chatColor) {
        this.chatColor = chatColor;
    }

    @Override
    public @NotNull String getPrefix() {
        if (prefix == null || prefix.isEmpty()) {
            com.pedestriamc.strings.Strings plugin = com.pedestriamc.strings.Strings.getInstance();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (plugin.getLuckPerms() != null) {
                    net.luckperms.api.model.user.User lpUser = plugin.getLuckPerms().getUserManager().getUser(uuid);
                    if (lpUser != null) {
                        String lpPrefix = lpUser.getCachedData().getMetaData().getPrefix();
                        if (lpPrefix != null) return lpPrefix;
                    }
                }
                if (plugin.getVaultChat() != null) {
                    return plugin.getVaultChat().getPlayerPrefix(player);
                }
            }
        }
        return prefix != null ? prefix : "";
    }

    @Override
    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
    }

    @Override
    public @NotNull String getSuffix() {
        if (suffix == null || suffix.isEmpty()) {
            com.pedestriamc.strings.Strings plugin = com.pedestriamc.strings.Strings.getInstance();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (plugin.getLuckPerms() != null) {
                    net.luckperms.api.model.user.User lpUser = plugin.getLuckPerms().getUserManager().getUser(uuid);
                    if (lpUser != null) {
                        String lpSuffix = lpUser.getCachedData().getMetaData().getSuffix();
                        if (lpSuffix != null) return lpSuffix;
                    }
                }
                if (plugin.getVaultChat() != null) {
                    return plugin.getVaultChat().getPlayerSuffix(player);
                }
            }
        }
        return suffix != null ? suffix : "";
    }

    @Override
    public void setSuffix(@NotNull String suffix) {
        this.suffix = suffix;
    }

    @Override
    public @NotNull String getDisplayName() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && (displayName == null || displayName.equals(name))) {
            return player.getDisplayName();
        }
        return displayName != null ? displayName : name;
    }

    @Override
    public void setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
    }

    @Override
    public @NotNull Channel getActiveChannel() {
        return activeChannel;
    }

    @Override
    public void setActiveChannel(@NotNull Channel channel) {
        this.activeChannel = channel;
    }

    @Override
    public @NotNull Set<Channel> getChannels() {
        return Collections.unmodifiableSet(channels);
    }

    @Override
    public void joinChannel(@NotNull Channel channel) {
        channels.add(channel);
        channel.addMember(this);
    }

    @Override
    public void leaveChannel(@NotNull Channel channel) {
        channels.remove(channel);
        channel.removeMember(this);
    }

    @Override
    public boolean memberOf(@NotNull Channel channel) {
        return channels.contains(channel);
    }

    @Override
    public boolean isMentionsEnabled() {
        return mentionsEnabled;
    }

    @Override
    public void setMentionsEnabled(boolean mentionsEnabled) {
        this.mentionsEnabled = mentionsEnabled;
    }

    @Override
    public boolean isIgnoring(@NotNull StringsUser other) {
        return ignoredPlayers.contains(other.getUniqueId());
    }

    @Override
    public void ignore(@NotNull StringsUser user) {
        ignoredPlayers.add(user.getUniqueId());
    }

    @Override
    public void stopIgnoring(@NotNull StringsUser user) {
        ignoredPlayers.remove(user.getUniqueId());
    }

    @Override
    public Set<UUID> getIgnoredPlayers() {
        return Collections.unmodifiableSet(ignoredPlayers);
    }

    @Override
    public boolean isMonitoring(@NotNull Monitorable monitorable) {
        if (monitorable instanceof Channel channel) {
            return monitoredChannels.contains(channel);
        }
        return false;
    }

    @Override
    public void monitor(@NotNull Monitorable monitorable) {
        if (monitorable instanceof Channel channel) {
            monitoredChannels.add(channel);
        }
    }

    @Override
    public void unmonitor(@NotNull Monitorable monitorable) {
        if (monitorable instanceof Channel channel) {
            monitoredChannels.remove(channel);
        }
    }

    @Override
    public @NotNull Set<Channel> getMonitoredChannels() {
        return Collections.unmodifiableSet(monitoredChannels);
    }

    @Override
    public void muteChannel(@NotNull Channel channel) {
        mutedChannels.add(channel);
        leaveChannel(channel);
        if (channel instanceof Monitorable monitorable) {
            unmonitor(monitorable);
        }
    }

    @Override
    public void unmuteChannel(@NotNull Channel channel) {
        mutedChannels.remove(channel);
    }

    @Override
    public @NotNull Set<Channel> getMutedChannels() {
        return Collections.unmodifiableSet(mutedChannels);
    }

    @Override
    public boolean hasChannelMuted(@NotNull Channel channel) {
        return mutedChannels.contains(channel);
    }

    @Override
    public boolean hasDirectMessagesEnabled() {
        return directMessagesEnabled;
    }

    @Override
    public void setDirectMessagesEnabled(boolean msgEnabled) {
        this.directMessagesEnabled = msgEnabled;
    }

    @Override
    public boolean isDiscordLinked() {
        return discordId.isPresent();
    }

    @Override
    public @NotNull Snowflake getDiscordId() {
        return discordId;
    }

    @Override
    public void setDiscordId(@NotNull Snowflake snowflake) {
        this.discordId = snowflake;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        Player player = Bukkit.getPlayer(uuid);
        return player != null && player.hasPermission(permission);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(message);
        }
    }
}
