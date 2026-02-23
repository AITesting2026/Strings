package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class ProximityChannel extends WorldChannel {

    private double proximity;

    public ProximityChannel(String name, String format, Membership membership, int priority, double proximity) {
        super(name, format, membership, priority);
        this.proximity = proximity;
    }

    @Override
    public @NotNull Type getType() {
        return Type.PROXIMITY;
    }

    @Override
    public double getProximity() {
        return proximity;
    }

    @Override
    public void setProximity(double proximity) {
        this.proximity = proximity;
    }

    @Override
    public Set<StringsUser> getRecipients(@NotNull StringsUser user) {
        Player sender = Bukkit.getPlayer(user.getUniqueId());
        if (sender == null) return Set.of();

        return super.getRecipients(user).stream()
                .filter(recipient -> {
                    Player recPlayer = Bukkit.getPlayer(recipient.getUniqueId());
                    return recPlayer != null && recPlayer.getWorld().equals(sender.getWorld()) && recPlayer.getLocation().distanceSquared(sender.getLocation()) <= proximity * proximity;
                })
                .collect(Collectors.toSet());
    }
}
