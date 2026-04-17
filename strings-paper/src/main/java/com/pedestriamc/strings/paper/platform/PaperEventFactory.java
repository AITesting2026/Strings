package com.pedestriamc.strings.paper.platform;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.DirectMessageEvent;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.event.moderation.MessageDeletionEvent;
import com.pedestriamc.strings.api.event.strings.user.*;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.event.events.*;
import com.pedestriamc.strings.paper.event.PaperChannelChatEvent;
import com.pedestriamc.strings.paper.event.PaperDirectMessageEvent;
import com.pedestriamc.strings.paper.event.PaperMessageDeletionEvent;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class PaperEventFactory implements EventFactory {

    @Override
    public @NotNull ChannelChatEvent chatEvent(boolean async, boolean cancellable, @NotNull StringsUser sender, @NotNull String message, @NotNull Set<StringsUser> recipients, @NotNull Channel channel, @Nullable SignedMessage signedMessage) {
        return new PaperChannelChatEvent(async, cancellable, sender, message, recipients, channel, signedMessage);
    }

    @Override
    public @NotNull DirectMessageEvent directMessage(@NotNull StringsUser sender, @NotNull StringsUser recipient, @NotNull String message) {
        return new PaperDirectMessageEvent(sender, recipient, message);
    }

    @Override
    public @NotNull MessageDeletionEvent messageDeletion(@NotNull SignedMessage signedMessage) {
        return new PaperMessageDeletionEvent(signedMessage);
    }

    @Override
    public @NotNull ChannelJoinEvent channelJoin(@NotNull StringsUser user, @NotNull Channel channel) {
        return new UserChannelJoinEvent(channel, user);
    }

    @Override
    public @NotNull ChannelLeaveEvent channelLeave(@NotNull StringsUser user, @NotNull Channel channel) {
        return new UserChannelLeaveEvent(channel, user);
    }

    @Override
    public @NotNull ChannelMonitorEvent channelMonitor(@NotNull StringsUser user, @NotNull Channel channel) {
        return new UserChannelMonitorEvent(channel, user);
    }

    @Override
    public @NotNull ChannelUnmonitorEvent channelUnmonitor(@NotNull StringsUser user, @NotNull Channel channel) {
        return new UserChannelUnmonitorEvent(channel, user);
    }

    @Override
    public @NotNull ActiveChannelUpdateEvent channelActive(@NotNull StringsUser user, @NotNull Channel channel) {
        return new UserActiveChannelUpdateEvent(channel, user);
    }
}
