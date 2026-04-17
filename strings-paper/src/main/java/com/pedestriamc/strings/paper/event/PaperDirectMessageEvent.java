package com.pedestriamc.strings.paper.event;

import com.pedestriamc.strings.api.event.DirectMessageEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

public class PaperDirectMessageEvent implements DirectMessageEvent {

    private final StringsUser sender;
    private final StringsUser recipient;
    private String message;
    private boolean cancelled;

    public PaperDirectMessageEvent(@NotNull StringsUser sender, @NotNull StringsUser recipient, @NotNull String message) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
    }

    @Override
    public @NotNull StringsUser getSender() {
        return sender;
    }

    @Override
    public @NotNull StringsUser getRecipient() {
        return recipient;
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
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
