package com.pedestriamc.strings.paper.platform;

import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.user.UserManager;
import com.pedestriamc.strings.paper.StringsPaper;
import com.pedestriamc.strings.paper.user.PaperUserBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.pedestriamc.strings.api.channel.Channel;

public class PaperUserManager implements UserManager {

    private final StringsPaper plugin;
    private final Map<UUID, StringsUser> users = new ConcurrentHashMap<>();
    private final PaperFileManager fileManager;

    public PaperUserManager(StringsPaper plugin, PaperFileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    @Override
    public @NotNull StringsUser getUser(@NotNull UUID uuid) {
        return users.computeIfAbsent(uuid, this::loadUser);
    }

    private StringsUser loadUser(UUID uuid) {
        FileConfiguration config = fileManager.getConfig("users.yml");
        String path = uuid.toString();

        PaperUserBuilder paperBuilder = new PaperUserBuilder(plugin, uuid, !config.contains(path));
        var builder = paperBuilder.getBuilder();

        if (config.contains(path)) {
            String activeChannelName = config.getString(path + ".activeChannel");
            if (activeChannelName != null) {
                builder.activeChannel(plugin.getChannelLoader().getChannel(activeChannelName));
            }
            List<String> channels = config.getStringList(path + ".channels");
            Set<Channel> channelSet = new HashSet<>();
            for (String channelName : channels) {
                Channel channel = plugin.getChannelLoader().getChannel(channelName);
                if (channel != null) {
                    channelSet.add(channel);
                }
            }
            builder.channels(channelSet);
            builder.chatColor(config.getString(path + ".chatColor"));
            builder.ignoredPlayers(config.getStringList(path + ".ignoring").stream().map(UUID::fromString).collect(Collectors.toSet()));
        } else {
            builder.activeChannel(plugin.getChannelLoader().getDefaultChannel());
        }

        return builder.build();
    }

    @Override
    public void saveUser(@NotNull StringsUser user) {
        FileConfiguration config = fileManager.getConfig("users.yml");
        String path = user.getUniqueId().toString();
        config.set(path + ".name", user.getName());
        config.set(path + ".activeChannel", user.getActiveChannel().getName());
        config.set(path + ".channels", user.getChannels().stream().map(Channel::getName).collect(Collectors.toList()));
        config.set(path + ".chatColor", user.getChatColor());
        config.set(path + ".ignoring", user.getIgnoredPlayers().stream().map(UUID::toString).collect(Collectors.toList()));
        fileManager.saveConfig("users.yml");
    }

    @Override
    public @NotNull @UnmodifiableView Collection<StringsUser> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    public void unloadUser(UUID uuid) {
        StringsUser user = users.remove(uuid);
        if (user != null) {
            saveUser(user);
        }
    }
}
