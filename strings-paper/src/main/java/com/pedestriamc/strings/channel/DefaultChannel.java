package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DefaultChannel extends AbstractChannel {

    private final ChannelLoader loader;

    public DefaultChannel(ChannelLoader loader) {
        super("default", "", Membership.DEFAULT, Integer.MIN_VALUE);
        this.loader = loader;
    }

    @Override
    public @NotNull Type getType() {
        return Type.DEFAULT;
    }

    @Override
    public @NotNull Channel resolve(@NotNull StringsUser user) {
        for (Channel channel : loader.getSortedChannelSet()) {
            if (channel.getType() == Type.DEFAULT) continue;
            if (channel.allows(user)) {
                return channel;
            }
        }
        return this;
    }

    @Override
    public Set<StringsUser> getRecipients(@NotNull StringsUser user) {
        Channel resolved = resolve(user);
        if (resolved == this) return Set.of();
        return resolved.getRecipients(user);
    }

    @Override
    public Set<StringsUser> getPlayersInScope() {
        return Set.of();
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        Channel resolved = resolve(user);
        if (resolved != this) {
            resolved.sendMessage(user, message);
        }
    }
}
