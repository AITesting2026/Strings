package com.pedestriamc.strings.files;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.files.FileManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FileManagerImpl implements FileManager {
    private final Strings plugin;

    public FileManagerImpl(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull File getEmojisFile() {
        return new File(plugin.getDataFolder(), "emojis.yml");
    }
}
