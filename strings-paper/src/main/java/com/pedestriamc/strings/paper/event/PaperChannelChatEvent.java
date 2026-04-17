package com.pedestriamc.strings.paper.event;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public class PaperChannelChatEvent implements ChannelChatEvent {

    private boolean cancelled;
    private final StringsUser sender;
    private String message;
    private final Set<StringsUser> recipients;
    private final Channel channel;
    private final SignedMessage signedMessage;

    public PaperChannelChatEvent(boolean async, boolean cancellable, @NotNull StringsUser sender, @NotNull String message, @NotNull Set<StringsUser> recipients, @NotNull Channel channel, @Nullable SignedMessage signedMessage) {
        this.sender = sender;
        this.message = message;
        this.recipients = recipients;
        this.channel = channel;
        this.signedMessage = signedMessage;
    }

    @Override
    public @NotNull Channel getChannel() {
        return channel;
    }

    @Override
    public @NotNull Set<StringsUser> getMessageRecipients() {
        return recipients;
    }

    @Override
    public @NotNull StringsUser getSender() {
        return sender;
    }

    @Override
    public @NotNull String getMessage() {
        return message;
    }

    @Override
    public void setMessage(@NotNull String message) {
        this.message = message;
    }

    @Override
    public @NotNull Optional<SignedMessage> getSignedMessage() {
        return Optional.ofNullable(signedMessage);
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

}
