package com.pedestriamc.strings.paper.platform;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.common.channel.AbstractChannelLoader;
import com.pedestriamc.strings.paper.StringsPaper;
import com.pedestriamc.strings.paper.channel.PaperLocality;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class PaperChannelLoader extends AbstractChannelLoader {

    private final StringsPaper plugin;
    private final PaperFileManager fileManager;

    public PaperChannelLoader(@NotNull StringsPaper plugin, PaperFileManager fileManager) {
        super(plugin);
        this.plugin = plugin;
        this.fileManager = fileManager;
        loadChannels();
    }

    private void loadChannels() {
        FileConfiguration config = fileManager.getConfig("channels.yml");
        ConfigurationSection section = config.getConfigurationSection("channels");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection chanSection = section.getConfigurationSection(key);
            if (chanSection == null) continue;

            String typeStr = chanSection.getString("type", "GLOBAL");
            String format = chanSection.getString("format", "[{channel}] {player}: {message}");
            int priority = chanSection.getInt("priority", 0);
            String permission = chanSection.getString("permission");
            String symbol = chanSection.getString("symbol");
            Membership membership = Membership.valueOf(chanSection.getString("membership", "DEFAULT").toUpperCase());

            Channel channel;
            if (typeStr.equalsIgnoreCase("WORLD") || typeStr.equalsIgnoreCase("PROXIMITY")) {
                LocalChannelBuilder builder = new LocalChannelBuilder(key, format, membership, plugin.getServer().getWorlds().stream().map(w -> new PaperLocality(plugin, w)).collect(java.util.stream.Collectors.toSet()));
                builder.setPriority(priority);
                if (typeStr.equalsIgnoreCase("PROXIMITY")) {
                    builder.setDistance(chanSection.getDouble("radius", 100.0));
                    channel = builder.build(IChannelBuilder.Identifier.PROXIMITY);
                } else {
                    channel = builder.build(IChannelBuilder.Identifier.WORLD);
                }
            } else {
                ChannelBuilder builder = new ChannelBuilder(key, format, membership);
                builder.setPriority(priority);
                channel = builder.build(IChannelBuilder.Identifier.NORMAL);
            }

            register(channel);
            if (symbol != null && !symbol.isEmpty()) {
                registerChannelSymbol(symbol, channel);
            }
        }
    }

    @Override
    public void save(@NotNull Channel channel) {
        // Implement channel saving if needed
    }
}
