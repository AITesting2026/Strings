package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldChannel extends AbstractChannel implements LocalChannel<World> {

    protected Set<Locality<World>> worlds = new HashSet<>();

    public WorldChannel(String name, String format, Membership membership, int priority) {
        super(name, format, membership, priority);
    }

    @Override
    public @NotNull Type getType() {
        return Type.WORLD;
    }

    @Override
    public boolean containsInScope(@NotNull StringsUser user) {
        Player player = Bukkit.getPlayer(user.getUniqueId());
        return player != null && containsWorld(player.getWorld());
    }

    @Override
    public Set<? extends Locality<World>> getWorlds() {
        return Collections.unmodifiableSet(worlds);
    }

    @Override
    public void setWorlds(@NotNull Set<Locality<World>> worlds) {
        this.worlds = new HashSet<>(worlds);
    }

    @Override
    public boolean containsWorld(@NotNull World world) {
        return worlds.stream().anyMatch(l -> l.get().equals(world));
    }

    @Override
    public double getProximity() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("WorldChannel does not support proximity.");
    }

    @Override
    public void setProximity(@Range(from = -1, to = Integer.MAX_VALUE) double proximity) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("WorldChannel does not support proximity.");
    }

    @Override
    public Set<StringsUser> getRecipients(@NotNull StringsUser user) {
        return getPlayersInScope().stream()
                .filter(recipient -> !recipient.hasChannelMuted(this))
                .filter(recipient -> !recipient.isIgnoring(user))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<StringsUser> getPlayersInScope() {
        Strings plugin = Strings.getInstance();
        if (plugin != null && plugin.getUserManager() != null) {
            return plugin.getUserManager().getUsers().stream()
                    .filter(this::containsInScope)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
    }
}
