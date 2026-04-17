package com.pedestriamc.strings.paper.listeners;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.chat.MessageProcessor;
import com.pedestriamc.strings.paper.StringsPaper;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.stream.Collectors;

public class PaperChatListener implements Listener {

    private final StringsPaper plugin;

    public PaperChatListener(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        StringsUser sender = plugin.users().getUser(event.getPlayer().getUniqueId());
        Channel channel = sender.resolveActiveChannel();
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.message());

        MessageProcessor processor = new MessageProcessor(plugin, channel);
        String processedMessage = processor.processMessage(sender, rawMessage);

        ChannelChatEvent chatEvent = plugin.eventFactory().chatEvent(
                true,
                true,
                sender,
                processedMessage,
                channel.getRecipients(sender),
                channel,
                event.signedMessage()
        );

        plugin.eventManager().dispatch(chatEvent);

        if (chatEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        String template = processor.generateTemplate(sender);
        String formattedMessage = template.replace("{message}", chatEvent.getMessage());
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(formattedMessage);

        for (StringsUser recipient : chatEvent.getMessageRecipients()) {
            recipient.sendMessage(component);
        }

        plugin.serverSource().sendMessage(component);
    }
}
