package com.pedestriamc.strings.settings;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class SettingsImpl implements Settings {
    private final Strings plugin;

    public SettingsImpl(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <E extends Enum<E> & Option.CoreKey<V>, V> V get(@NotNull E key) {
        FileConfiguration config = plugin.getConfig();
        String path = key.key();
        if (config.contains(path)) {
            Object val = config.get(path);
            if (val != null) {
                return (V) val;
            }
        }
        return key.defaultValue();
    }
}
