package com.pedestriamc.strings.message;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHistory {
    private static final Map<UUID, Component> history = new ConcurrentHashMap<>();

    public static void addMessage(@NotNull UUID id, @NotNull Component message) {
        history.put(id, message);
    }

    public static Component getMessage(@NotNull UUID id) {
        return history.get(id);
    }

    public static void removeMessage(@NotNull UUID id) {
        history.remove(id);
    }
}
