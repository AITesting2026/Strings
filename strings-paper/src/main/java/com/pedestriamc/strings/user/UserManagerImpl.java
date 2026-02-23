package com.pedestriamc.strings.user;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.discord.Snowflake;
import com.pedestriamc.strings.api.text.format.StringsComponent;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.user.UserManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class UserManagerImpl implements UserManager {

    private final Strings plugin;
    private final Map<UUID, StringsUser> onlineUsers = new HashMap<>();
    private final File usersFolder;

    public UserManagerImpl(Strings plugin) {
        this.plugin = plugin;
        this.usersFolder = new File(plugin.getDataFolder(), "users");
        if (!usersFolder.exists()) {
            usersFolder.mkdirs();
        }
    }

    @Override
    public @Nullable StringsUser getUser(@NotNull UUID uuid) {
        return onlineUsers.get(uuid);
    }

    @Override
    public void saveUser(@NotNull StringsUser user) {
        File userFile = new File(usersFolder, user.getUniqueId() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("name", user.getName());
        config.set("prefix", user.getPrefix());
        config.set("suffix", user.getSuffix());
        config.set("display-name", user.getDisplayName());
        config.set("chat-color", user.getChatColorComponent().toString());
        config.set("mentions-enabled", user.isMentionsEnabled());
        config.set("direct-messages-enabled", user.hasDirectMessagesEnabled());
        config.set("ignored-players", user.getIgnoredPlayers().stream().map(UUID::toString).collect(Collectors.toList()));
        if (user.isDiscordLinked()) {
            config.set("discord-id", user.getDiscordId().get());
        }
        config.set("active-channel", user.getActiveChannel() != null ? user.getActiveChannel().getName() : null);
        config.set("joined-channels", user.getChannels().stream().map(com.pedestriamc.strings.api.channel.Channel::getName).collect(Collectors.toList()));

        try {
            config.save(userFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save user data for " + user.getName(), e);
        }
    }

    public StringsUser loadUser(UUID uuid, String name) {
        File userFile = new File(usersFolder, uuid + ".yml");
        StringsUserImpl user = new StringsUserImpl(uuid, name);
        if (userFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(userFile);
            user.setPrefix(config.getString("prefix", ""));
            user.setSuffix(config.getString("suffix", ""));
            user.setDisplayName(config.getString("display-name", name));
            user.setChatColor(config.getString("chat-color", "&f"));
            user.setMentionsEnabled(config.getBoolean("mentions-enabled", true));
            user.setDirectMessagesEnabled(config.getBoolean("direct-messages-enabled", true));
            user.setNew(false);

            for (String ignoredUuid : config.getStringList("ignored-players")) {
                try {
                    user.ignore(new OfflineStringsUser(UUID.fromString(ignoredUuid)));
                } catch (IllegalArgumentException ignored) {}
            }

            long discordId = config.getLong("discord-id", 0);
            if (discordId != 0) {
                user.setDiscordId(Snowflake.ofOrEmpty(discordId));
            }

            String activeChannelName = config.getString("active-channel");
            if (activeChannelName != null) {
                com.pedestriamc.strings.api.channel.Channel activeChan = plugin.getChannelLoader().getChannel(activeChannelName);
                if (activeChan != null) {
                    user.setActiveChannel(activeChan);
                }
            }

            for (String chanName : config.getStringList("joined-channels")) {
                com.pedestriamc.strings.api.channel.Channel chan = plugin.getChannelLoader().getChannel(chanName);
                if (chan != null) {
                    user.joinChannel(chan);
                }
            }
        } else {
            user.setNew(true);
        }
        onlineUsers.put(uuid, user);
        return user;
    }

    public void unloadUser(UUID uuid) {
        StringsUser user = onlineUsers.remove(uuid);
        if (user != null) {
            saveUser(user);
        }
    }

    @Override
    public @NotNull @UnmodifiableView Set<StringsUser> getUsers() {
        return Collections.unmodifiableSet(new HashSet<>(onlineUsers.values()));
    }

    private static class OfflineStringsUser implements StringsUser {
        private final UUID uuid;
        OfflineStringsUser(UUID uuid) { this.uuid = uuid; }
        @Override public @NotNull UUID getUniqueId() { return uuid; }
        @Override public @NotNull String getName() { return ""; }
        @Override public @Nullable String getChatColor() { return null; }
        @Override public void setChatColor(String chatColor) {}
        @Override public StringsComponent getChatColorComponent() { return null; }
        @Override public void setChatColorComponent(StringsComponent chatColor) {}
        @Override public @NotNull String getPrefix() { return ""; }
        @Override public void setPrefix(@NotNull String prefix) {}
        @Override public @NotNull String getSuffix() { return ""; }
        @Override public void setSuffix(@NotNull String suffix) {}
        @Override public @NotNull String getDisplayName() { return ""; }
        @Override public void setDisplayName(@NotNull String displayName) {}
        @Override public @NotNull com.pedestriamc.strings.api.channel.Channel getActiveChannel() { return null; }
        @Override public void setActiveChannel(@NotNull com.pedestriamc.strings.api.channel.Channel channel) {}
        @Override public @NotNull Set<com.pedestriamc.strings.api.channel.Channel> getChannels() { return Collections.emptySet(); }
        @Override public void joinChannel(@NotNull com.pedestriamc.strings.api.channel.Channel channel) {}
        @Override public void leaveChannel(@NotNull com.pedestriamc.strings.api.channel.Channel channel) {}
        @Override public boolean memberOf(@NotNull com.pedestriamc.strings.api.channel.Channel channel) { return false; }
        @Override public boolean isMentionsEnabled() { return false; }
        @Override public void setMentionsEnabled(boolean mentionsEnabled) {}
        @Override public boolean isIgnoring(@NotNull StringsUser other) { return false; }
        @Override public void ignore(@NotNull StringsUser user) {}
        @Override public void stopIgnoring(@NotNull StringsUser user) {}
        @Override public Set<UUID> getIgnoredPlayers() { return Collections.emptySet(); }
        @Override public boolean isMonitoring(@NotNull com.pedestriamc.strings.api.channel.Monitorable monitorable) { return false; }
        @Override public void monitor(@NotNull com.pedestriamc.strings.api.channel.Monitorable monitorable) {}
        @Override public void unmonitor(@NotNull com.pedestriamc.strings.api.channel.Monitorable monitorable) {}
        @Override public @NotNull Set<com.pedestriamc.strings.api.channel.Channel> getMonitoredChannels() { return Collections.emptySet(); }
        @Override public void muteChannel(@NotNull com.pedestriamc.strings.api.channel.Channel channel) {}
        @Override public void unmuteChannel(@NotNull com.pedestriamc.strings.api.channel.Channel channel) {}
        @Override public @NotNull Set<com.pedestriamc.strings.api.channel.Channel> getMutedChannels() { return Collections.emptySet(); }
        @Override public boolean hasChannelMuted(@NotNull com.pedestriamc.strings.api.channel.Channel channel) { return false; }
        @Override public boolean hasDirectMessagesEnabled() { return false; }
        @Override public void setDirectMessagesEnabled(boolean msgEnabled) {}
        @Override public boolean isDiscordLinked() { return false; }
        @Override public @NotNull Snowflake getDiscordId() { return Snowflake.empty(); }
        @Override public void setDiscordId(@NotNull Snowflake snowflake) {}
        @Override public boolean isNew() { return false; }
        @Override public boolean hasPermission(@NotNull String permission) { return false; }
        @Override public void sendMessage(@NotNull String message) {}
        @Override public void sendMessage(@NotNull net.kyori.adventure.text.Component message) {}
    }
}
