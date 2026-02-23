package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractChannel implements Channel {

    protected String name;
    protected String format;
    protected String broadcastFormat;
    protected Membership membership;
    protected int priority;
    protected boolean urlFiltering = false;
    protected boolean profanityFiltering = false;
    protected boolean cooldownEnabled = false;
    protected boolean allowMessageDeletion = true;
    protected Sound broadcastSound;
    protected String defaultColor = "&f";

    protected final Set<StringsUser> members = Collections.newSetFromMap(new ConcurrentHashMap<>());

    protected AbstractChannel(String name, String format, Membership membership, int priority) {
        this.name = name;
        this.format = format;
        this.membership = membership;
        this.priority = priority;
        this.broadcastFormat = "&8[&c" + name + "&8] &f{message}";
    }

    @Override
    public int compareTo(@NotNull Channel o) {
        int priorityCompare = Integer.compare(o.getPriority(), this.priority);
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        return this.name.compareTo(o.getName());
    }

    @Override
    public @NotNull Channel resolve(@NotNull StringsUser user) {
        return this;
    }

    @Override
    public @NotNull String getFormat() {
        return format;
    }

    @Override
    public @NotNull String getBroadcastFormat() {
        return broadcastFormat;
    }

    @Override
    public void setBroadcastSound(@Nullable Sound sound) {
        this.broadcastSound = sound;
    }

    @Override
    public @Nullable Sound getBroadcastSound() {
        return broadcastSound;
    }

    @Override
    public void setFormat(@NotNull String format) {
        this.format = format;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Override
    public String getDefaultColor() {
        return defaultColor;
    }

    @Override
    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public boolean isUrlFiltering() {
        return urlFiltering;
    }

    @Override
    public void setUrlFilter(boolean doUrlFilter) {
        this.urlFiltering = doUrlFilter;
    }

    @Override
    public boolean isProfanityFiltering() {
        return profanityFiltering;
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {
        this.profanityFiltering = doProfanityFilter;
    }

    @Override
    public boolean isCooldownEnabled() {
        return cooldownEnabled;
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {
        this.cooldownEnabled = doCooldown;
    }

    @Override
    public boolean allowsMessageDeletion() {
        return allowMessageDeletion;
    }

    @Override
    public void setAllowMessageDeletion(boolean allowMessageDeletion) {
        this.allowMessageDeletion = allowMessageDeletion;
    }

    @Override
    public void addMember(@NotNull StringsUser user) {
        members.add(user);
    }

    @Override
    public void removeMember(@NotNull StringsUser user) {
        members.remove(user);
    }

    @Override
    public Set<StringsUser> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    @Override
    public Membership getMembership() {
        return membership;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean allows(@NotNull StringsUser user) {
        if (membership == Membership.DEFAULT) return true;
        if (membership == Membership.PERMISSION) {
            return user.hasPermission("strings.channels." + name.toLowerCase()) || user.hasPermission("strings.channels.*");
        }
        return false;
    }

    @Override
    public boolean callsEvents() {
        return true;
    }

    @Override
    public void broadcast(@NotNull String message) {
        String formatted = broadcastFormat.replace("{message}", message);
        broadcastPlain(LegacyComponentSerializer.legacyAmpersand().deserialize(formatted));
        if (broadcastSound != null) {
            for (StringsUser user : getPlayersInScope()) {
                Player player = Bukkit.getPlayer(user.getUniqueId());
                if (player != null) {
                    player.playSound(broadcastSound);
                }
            }
        }
    }

    @Override
    public void broadcast(@NotNull Component message) {
        broadcastPlain(message);
    }

    @Override
    public void broadcastPlain(@NotNull String message) {
        broadcastPlain(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }

    @Override
    public void broadcastPlain(@NotNull Component message) {
        for (StringsUser user : getPlayersInScope()) {
            user.sendMessage(message);
        }
    }

    @Override
    public Map<String, Object> getData() {
        return Map.of(
            "name", name,
            "format", format,
            "membership", membership.name(),
            "priority", priority,
            "url-filter", urlFiltering,
            "profanity-filter", profanityFiltering,
            "cooldown", cooldownEnabled,
            "allow-deletion", allowMessageDeletion
        );
    }
}
