package com.pedestriamc.strings.paper.tasks;

import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.paper.StringsPaper;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoBroadcastTask extends BukkitRunnable {

    private final StringsPaper plugin;
    private final List<String> messages = new ArrayList<>();
    private final Random random = new Random();

    public AutoBroadcastTask(StringsPaper plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        messages.clear();
        FileConfiguration config = ((com.pedestriamc.strings.paper.platform.PaperFileManager) plugin.files()).getConfig("config.yml");
        ConfigurationSection section = config.getConfigurationSection("auto-broadcasts");
        if (section != null && section.getBoolean("enable", false)) {
            messages.addAll(section.getStringList("messages"));
            long interval = section.getLong("interval", 300) * 20L;
            this.runTaskTimer(plugin, interval, interval);
        }
    }

    @Override
    public void run() {
        if (messages.isEmpty()) return;
        String message = messages.get(random.nextInt(messages.size()));
        String format = plugin.settings().get(Option.Text.BROADCAST_FORMAT);
        plugin.getServer().broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(format + message));
    }
}
