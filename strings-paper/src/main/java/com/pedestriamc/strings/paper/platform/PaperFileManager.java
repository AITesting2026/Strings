package com.pedestriamc.strings.paper.platform;

import com.pedestriamc.strings.api.files.FileManager;
import com.pedestriamc.strings.paper.StringsPaper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PaperFileManager implements FileManager {

    private final StringsPaper plugin;
    private final Map<String, FileConfiguration> configs = new HashMap<>();
    private final Map<String, File> files = new HashMap<>();

    public PaperFileManager(StringsPaper plugin) {
        this.plugin = plugin;
        loadFiles();
    }

    private void loadFiles() {
        createFile("config.yml");
        createFile("users.yml");
        createFile("channels.yml");
        createFile("messages.yml");
        createFile("emojis.yml");
    }

    private void createFile(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        files.put(name, file);
        configs.put(name, YamlConfiguration.loadConfiguration(file));
    }

    public FileConfiguration getConfig(String name) {
        return configs.get(name);
    }

    public void saveConfig(String name) {
        try {
            configs.get(name).save(files.get(name));
        } catch (IOException e) {
            plugin.warning("Could not save " + name);
        }
    }

    public void reload() {
        configs.clear();
        files.clear();
        loadFiles();
    }

    @Override
    public @NotNull File getEmojisFile() {
        return files.get("emojis.yml");
    }
}
