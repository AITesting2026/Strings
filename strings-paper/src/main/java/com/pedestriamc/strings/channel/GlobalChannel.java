package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GlobalChannel extends AbstractChannel {

    public GlobalChannel(String name, String format, Membership membership, int priority) {
        super(name, format, membership, priority);
    }

    @Override
    public @NotNull Type getType() {
        return Type.NORMAL;
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
            return new HashSet<>(plugin.getUserManager().getUsers());
        }
        return Set.of();
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        // This is typically handled by the ChatListener in Paper
    }
}
