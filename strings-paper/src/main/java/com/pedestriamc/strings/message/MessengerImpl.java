package com.pedestriamc.strings.message;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.MessageContext;
import com.pedestriamc.strings.api.message.Messageable;
import com.pedestriamc.strings.api.message.Messenger;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessengerImpl implements Messenger {
    private final Strings plugin;
    private final Map<Message, String> messages = new HashMap<>();
    private String prefix;

    public MessengerImpl(Strings plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(messagesFile);
        prefix = config.getString("prefix", "&8[&bStrings&8] &r");
        for (Message m : Message.values()) {
            String val = config.getString(m.getKey());
            if (val != null) {
                messages.put(m, val);
            }
        }
    }

    @Override
    public void sendMessage(@NotNull Message message, @NotNull Messageable recipient, @NotNull Map<String, String> placeholders) {
        String msg = messages.getOrDefault(message, message.getKey());
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        recipient.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + msg));
    }

    @Override
    public void sendMessage(@NotNull Message message, @NotNull Messageable recipient) {
        sendMessage(message, recipient, Map.of());
    }

    @Override
    public void sendMessagePlain(@NotNull Message message, @NotNull Messageable recipient) {
        sendMessagePlain(message, recipient, Map.of());
    }

    @Override
    public void sendMessagePlain(@NotNull Message message, @NotNull Messageable recipient, @NotNull Map<String, String> placeholders) {
        String msg = messages.getOrDefault(message, message.getKey());
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        recipient.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
    }

    @Override
    public void batchSend(MessageContext @NotNull ... contexts) {
        for (MessageContext context : contexts) {
            Map<String, String> placeholders = context.placeholders();
            if (placeholders == null) placeholders = Map.of();
            if (context.usePrefix()) {
                sendMessage(context.message(), context.recipient(), placeholders);
            } else {
                sendMessagePlain(context.message(), context.recipient(), placeholders);
            }
        }
    }
}
