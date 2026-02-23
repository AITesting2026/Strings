package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.Locality;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChannelLoaderImpl implements ChannelLoader {
    private final Strings plugin;
    private final Map<String, Channel> channels = new ConcurrentHashMap<>();
    private final Map<String, Channel> symbols = new ConcurrentHashMap<>();
    private final File channelsFile;
    private final Channel defaultChannel;

    public ChannelLoaderImpl(Strings plugin) {
        this.plugin = plugin;
        this.channelsFile = new File(plugin.getDataFolder(), "channels.yml");
        this.defaultChannel = new DefaultChannel(this);
    }

    @Override
    public void register(@NotNull Channel channel) {
        channels.put(channel.getName().toLowerCase(), channel);
    }

    @Override
    public void unregister(@NotNull Channel channel) throws NoSuchElementException {
        if (channels.remove(channel.getName().toLowerCase()) == null) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void save(@NotNull Channel channel) {
        // Implementation for saving individual channel to file could go here
    }

    @Override
    public @Nullable Channel getChannel(@NotNull String name) {
        return channels.get(name.toLowerCase());
    }

    @Override
    public @NotNull Set<Channel> getChannels() {
        return new HashSet<>(channels.values());
    }

    @Override
    public void refresh() {
        channels.clear();
        symbols.clear();
        if (!channelsFile.exists()) {
            plugin.saveResource("channels.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(channelsFile);
        ConfigurationSection section = config.getConfigurationSection("channels");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection chanSec = section.getConfigurationSection(key);
                if (chanSec != null) {
                    Channel channel = parseChannel(key, chanSec);
                    if (channel != null) {
                        register(channel);
                        String symbol = chanSec.getString("symbol");
                        if (symbol != null && !symbol.isEmpty()) {
                            registerChannelSymbol(symbol, channel);
                        }
                    }
                }
            }
        }

        if (getChannel("global") == null) {
            Channel global = Channel.builder("global", "&8[&bGlobal&8] &7{displayname}&8: &f{message}", Membership.DEFAULT)
                    .setPriority(0)
                    .build(IChannelBuilder.Identifier.NORMAL);
            register(global);
            registerChannelSymbol("!", global);
        }
    }

    private Channel parseChannel(String name, ConfigurationSection sec) {
        String typeStr = sec.getString("type", "stringchannel");
        String format = sec.getString("format", "{displayname}: {message}");
        Membership membership;
        try {
            membership = Membership.valueOf(sec.getString("membership", "DEFAULT").toUpperCase());
        } catch (IllegalArgumentException e) {
            membership = Membership.DEFAULT;
        }
        int priority = sec.getInt("priority", 0);
        IChannelBuilder.Identifier identifier = IChannelBuilder.Identifier.of(typeStr);

        IChannelBuilder<?> builder;
        if (identifier == IChannelBuilder.Identifier.NORMAL || identifier == IChannelBuilder.Identifier.HELPOP) {
            builder = Channel.builder(name, format, membership);
        } else {
            Set<String> worldNames = new HashSet<>(sec.getStringList("worlds"));
            Set<Locality<World>> localities = worldNames.stream()
                    .map(Bukkit::getWorld)
                    .filter(java.util.Objects::nonNull)
                    .map(w -> Locality.of(w, w.getName()))
                    .collect(Collectors.toSet());
            builder = Channel.localBuilder(name, format, membership, localities);
            if (builder instanceof LocalChannelBuilder<?> localBuilder) {
                localBuilder.setDistance(sec.getDouble("distance", 100));
            }
        }

        builder.setPriority(priority)
               .setDoUrlFilter(sec.getBoolean("url-filter", false))
               .setDoProfanityFilter(sec.getBoolean("profanity-filter", false))
               .setDoCooldown(sec.getBoolean("cooldown", false))
               .setAllowMessageDeletion(sec.getBoolean("allow-deletion", true));

        return builder.build(identifier);
    }

    @Override
    public @NotNull SortedSet<Channel> getSortedChannelSet() {
        return new TreeSet<>(channels.values());
    }

    @Override
    public @NotNull Channel getDefaultChannel() {
        return defaultChannel;
    }

    @Override
    public @NotNull Map<String, Channel> getChannelSymbols() {
        return Collections.unmodifiableMap(symbols);
    }

    @Override
    public void registerChannelSymbol(@NotNull String symbol, @NotNull Channel channel) {
        symbols.put(symbol, channel);
    }
}
