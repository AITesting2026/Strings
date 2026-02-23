package com.pedestriamc.strings.settings;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.settings.SettingsRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsImpl implements Settings {

    private final Strings plugin;
    private SettingsRegistry registry;

    public SettingsImpl(Strings plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        this.registry = new SettingsRegistry(builder -> {
            FileConfiguration config = plugin.getConfig();

            // Populate Bool options
            for (Option.Bool opt : Option.Bool.values()) {
                builder.put(opt, config.getBoolean(opt.key(), opt.defaultValue()));
            }

            // Populate Text options
            for (Option.Text opt : Option.Text.values()) {
                String val = config.getString(opt.key());
                builder.put(opt, val != null ? val : opt.defaultValue());
            }

            // Populate Double options
            for (Option.Double opt : Option.Double.values()) {
                if (config.contains(opt.key())) {
                    builder.put(opt, config.getDouble(opt.key()));
                } else {
                    builder.put(opt, opt.defaultValue());
                }
            }

            // Populate StringList options
            for (Option.StringList opt : Option.StringList.values()) {
                List<String> val = config.getStringList(opt.key());
                if (val != null && !val.isEmpty()) {
                    builder.put(opt, val);
                } else {
                    builder.put(opt, opt.defaultValue());
                }
            }
        });
    }

    @Override
    public @NotNull <E extends Enum<E> & Option.CoreKey<V>, V> V get(@NotNull E key) {
        return registry.get(key);
    }
}
