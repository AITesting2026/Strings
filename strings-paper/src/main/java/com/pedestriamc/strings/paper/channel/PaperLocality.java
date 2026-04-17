package com.pedestriamc.strings.paper.channel;

import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.paper.StringsPaper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PaperLocality implements Locality<World> {

    private final StringsPaper plugin;
    private final World world;

    public PaperLocality(StringsPaper plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }

    @Override
    public @NotNull World get() {
        return world;
    }

    @Override
    public @NotNull String getName() {
        return world.getName();
    }

    @Override
    public boolean contains(@NotNull StringsUser user) {
        return world.getPlayers().stream().anyMatch(p -> p.getUniqueId().equals(user.getUniqueId()));
    }

    @Override
    public @NotNull Set<StringsUser> getUsers() {
        return world.getPlayers().stream()
                .map(p -> plugin.users().getUser(p.getUniqueId()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaperLocality that = (PaperLocality) o;
        return Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world);
    }
}
