package com.pedestriamc.strings.text;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.text.EmojiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EmojiManagerImpl implements EmojiManager {
    private final Strings plugin;
    private final Map<String, String> emojis = new HashMap<>();

    public EmojiManagerImpl(Strings plugin) {
        this.plugin = plugin;
        loadEmojis();
    }

    public void loadEmojis() {
        File emojisFile = new File(plugin.getDataFolder(), "emojis.yml");
        if (!emojisFile.exists()) {
            // Default emojis if file doesn't exist
            emojis.put(":smile:", "☺");
            emojis.put(":heart:", "❤");
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(emojisFile);
        ConfigurationSection section = config.getConfigurationSection("emojis");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                emojis.put(":" + key + ":", section.getString(key));
            }
        }
    }

    @Override
    public @NotNull String applyEmojis(@NotNull String input) {
        String output = input;
        for (Map.Entry<String, String> entry : emojis.entrySet()) {
            output = output.replace(entry.getKey(), entry.getValue());
        }
        return output;
    }

    @Override
    public @NotNull Component applyEmojis(@NotNull Component input) {
        Component output = input;
        for (Map.Entry<String, String> entry : emojis.entrySet()) {
            output = output.replaceText(TextReplacementConfig.builder()
                    .matchLiteral(entry.getKey())
                    .replacement(entry.getValue())
                    .build());
        }
        return output;
    }

    @Override
    public @Unmodifiable @NotNull Map<String, String> mappings() {
        return Collections.unmodifiableMap(emojis);
    }
}
