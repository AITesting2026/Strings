package com.pedestriamc.strings;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.command.Source;
import com.pedestriamc.strings.api.files.FileManager;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.user.UserManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class PaperStringsPlatform implements StringsPlatform {
    private final Strings plugin;

    public PaperStringsPlatform(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ChannelLoader getChannelLoader() {
        return plugin.getChannelLoader();
    }

    @Override
    public @NotNull UserManager users() {
        return plugin.getUserManager();
    }

    @Override
    public @NotNull Settings getSettings() {
        return plugin.getSettings();
    }

    @Override
    public @NotNull FileManager files() {
        return plugin.getFileManager();
    }

    @Override
    public @NotNull Source serverSource() {
        return new Source() {
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
        };
    }

    @Override
    public void async(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void info(@NotNull String message) {
        plugin.getLogger().info(message);
    }

    @Override
    public void warning(@NotNull String message) {
        plugin.getLogger().warning(message);
    }
}
