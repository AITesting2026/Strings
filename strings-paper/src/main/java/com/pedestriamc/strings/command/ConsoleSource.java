package com.pedestriamc.strings.command;

import com.pedestriamc.strings.api.command.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ConsoleSource implements Source {

    private static final ConsoleSource INSTANCE = new ConsoleSource();

    private ConsoleSource() {}

    public static ConsoleSource getInstance() {
        return INSTANCE;
    }

    @Override
    public @NotNull String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(@NotNull String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
