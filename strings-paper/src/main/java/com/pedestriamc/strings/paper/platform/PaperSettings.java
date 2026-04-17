package com.pedestriamc.strings.paper.platform;

import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.paper.StringsPaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class PaperSettings implements Settings {

    private final StringsPaper plugin;
    private final PaperFileManager fileManager;

    public PaperSettings(StringsPaper plugin, PaperFileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    @Override
    public @NotNull <E extends Enum<E> & Option.CoreKey<V>, V> V get(@NotNull E key) {
        FileConfiguration config = fileManager.getConfig("config.yml");
        String path = key.key();
        V value = (V) config.get(path);
        if (value == null) {
            return key.defaultValue();
        }
        return value;
    }

    @Override
    public Component getComponent(@NotNull Option.Text option) {
        String value = get(option);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(value);
    }
}
