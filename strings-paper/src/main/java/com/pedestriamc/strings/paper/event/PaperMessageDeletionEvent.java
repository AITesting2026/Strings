package com.pedestriamc.strings.paper.event;

import com.pedestriamc.strings.api.event.moderation.MessageDeletionEvent;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.chat.SignedMessage.Signature;
import org.jetbrains.annotations.NotNull;

public class PaperMessageDeletionEvent implements MessageDeletionEvent {

    private final SignedMessage signedMessage;

    public PaperMessageDeletionEvent(@NotNull SignedMessage signedMessage) {
        this.signedMessage = signedMessage;
    }

    @Override
    public SignedMessage getMessage() {
        return signedMessage;
    }

    @Override
    public Signature getSignature() {
        return signedMessage.signature();
    }
}
