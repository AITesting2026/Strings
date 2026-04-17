package com.pedestriamc.strings.paper.platform;

import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.MessageContext;
import com.pedestriamc.strings.api.message.Messageable;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.paper.StringsPaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PaperMessenger implements Messenger {

    private final StringsPaper plugin;
    private final PaperFileManager fileManager;

    public PaperMessenger(StringsPaper plugin, PaperFileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    private String getPrefix() {
        return fileManager.getConfig("config.yml").getString("prefix", "&8[&bStrings&8] &r");
    }

    private String getMessageString(Message message) {
        FileConfiguration config = fileManager.getConfig("messages.yml");
        return config.getString(message.name().toLowerCase().replace("_", "-"), "Missing message: " + message.name());
    }

    private String replacePlaceholders(String text, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    @Override
    public void sendMessage(@NotNull Message message, @NotNull Messageable recipient, @NotNull Map<String, String> placeholders) {
        String msg = getPrefix() + replacePlaceholders(getMessageString(message), placeholders);
        recipient.sendMessage(msg);
    }

    @Override
    public void sendMessage(@NotNull Message message, @NotNull Messageable recipient) {
        String msg = getPrefix() + getMessageString(message);
        recipient.sendMessage(msg);
    }

    @Override
    public void sendMessagePlain(@NotNull Message message, @NotNull Messageable recipient) {
        recipient.sendMessage(getMessageString(message));
    }

    @Override
    public void sendMessagePlain(@NotNull Message message, @NotNull Messageable recipient, @NotNull Map<String, String> placeholders) {
        recipient.sendMessage(replacePlaceholders(getMessageString(message), placeholders));
    }

    @Override
    public void batchSend(MessageContext @NotNull ... contexts) {
        for (MessageContext context : contexts) {
            sendMessage(context.message(), context.recipient(), context.placeholders());
        }
    }
}
