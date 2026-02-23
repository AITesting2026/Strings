package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.channel.GlobalChannel;
import com.pedestriamc.strings.channel.ProximityChannel;
import com.pedestriamc.strings.channel.WorldChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BuildableRegistrar {

    @SuppressWarnings("unchecked")
    public static void register(@NotNull StringsPlatform platform) {
        BuilderRegistry.registerPlatform(platform);

        BuilderRegistry.registerBuildable(IChannelBuilder.Identifier.NORMAL, (plt, builder) ->
            new GlobalChannel(builder.getName(), builder.getFormat(), builder.getMembership(), builder.getPriority())
        );

        BuilderRegistry.registerLocalBuildable(IChannelBuilder.Identifier.WORLD, (plt, builder) -> {
            WorldChannel wc = new WorldChannel(builder.getName(), builder.getFormat(), builder.getMembership(), builder.getPriority());
            wc.setWorlds((Set) builder.getWorlds());
            return wc;
        });

        BuilderRegistry.registerLocalBuildable(IChannelBuilder.Identifier.WORLD_STRICT, (plt, builder) -> {
            WorldChannel wc = new WorldChannel(builder.getName(), builder.getFormat(), builder.getMembership(), builder.getPriority());
            wc.setWorlds((Set) builder.getWorlds());
            return wc;
        });

        BuilderRegistry.registerLocalBuildable(IChannelBuilder.Identifier.PROXIMITY, (plt, builder) -> {
            ProximityChannel pc = new ProximityChannel(builder.getName(), builder.getFormat(), builder.getMembership(), builder.getPriority(), builder.getDistance());
            pc.setWorlds((Set) builder.getWorlds());
            return pc;
        });

        BuilderRegistry.registerLocalBuildable(IChannelBuilder.Identifier.PROXIMITY_STRICT, (plt, builder) -> {
            ProximityChannel pc = new ProximityChannel(builder.getName(), builder.getFormat(), builder.getMembership(), builder.getPriority(), builder.getDistance());
            pc.setWorlds((Set) builder.getWorlds());
            return pc;
        });
    }
}
