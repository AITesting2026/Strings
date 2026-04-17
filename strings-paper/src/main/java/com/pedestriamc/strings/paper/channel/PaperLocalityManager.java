package com.pedestriamc.strings.paper.channel;

import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.channel.local.LocalityManager;
import com.pedestriamc.strings.paper.StringsPaper;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class PaperLocalityManager implements LocalityManager<World> {

    private final StringsPaper plugin;

    public PaperLocalityManager(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public Locality<World> get(Object object) {
        if (object instanceof World world) {
            return new PaperLocality(plugin, world);
        }
        throw new IllegalArgumentException("Expected World object");
    }
}
