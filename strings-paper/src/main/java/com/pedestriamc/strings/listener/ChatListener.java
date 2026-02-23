package com.pedestriamc.strings.listener;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.message.MessageHistory;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.pedestriamc.strings.api.settings.Option;

import java.util.Set;
import java.util.UUID;

import me.clip.placeholderapi.PlaceholderAPI;

public class ChatListener implements Listener {

    private final Strings plugin;

    public ChatListener(Strings plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        StringsUser user = plugin.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;

        Channel channel = user.resolveActiveChannel();
        if (!channel.allows(user)) {
            Channel global = plugin.getChannelLoader().getChannel("global");
            if (global != null) {
                user.setActiveChannel(global);
                channel = user.resolveActiveChannel();
            }
        }

        String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());

        // Apply chat color if permission
        if (user.hasPermission("strings.chat.colormsg")) {
            rawMessage = LegacyComponentSerializer.legacyAmpersand().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(rawMessage));
        }

        // Apply Emojis
        if (user.hasPermission("strings.chat.emojis")) {
            rawMessage = plugin.getEmojiManager().applyEmojis(rawMessage);
        }

        // Apply placeholders in message if permission
        if (user.hasPermission("strings.chat.placeholdermsg") && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            rawMessage = PlaceholderAPI.setPlaceholders(player, rawMessage);
        }

        Component messageComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(user.getChatColorComponent().toString() + rawMessage);

        // Mentions
        handleMentions(user, rawMessage);

        // Formatting
        String format = channel.getFormat();

        String prefix = user.getPrefix();
        String suffix = user.getSuffix();
        String displayName = user.getDisplayName();

        Component formattedMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(format
                .replace("{prefix}", prefix)
                .replace("{suffix}", suffix)
                .replace("{displayname}", displayName)
                .replace("{message}", "")
        ).append(messageComponent);

        // Deletion button
        if (channel.allowsMessageDeletion()) {
            UUID messageId = UUID.randomUUID();
            MessageHistory.addMessage(messageId, formattedMessage);

            String deletionFormat = plugin.getSettings().get(Option.Text.DELETION_BUTTON_FORMAT);
            String deletionHover = plugin.getSettings().get(Option.Text.DELETION_BUTTON_HOVER);

            Component deletionButton = LegacyComponentSerializer.legacyAmpersand().deserialize(deletionFormat)
                    .hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(deletionHover)))
                    .clickEvent(ClickEvent.runCommand("/strings delete " + messageId));

            formattedMessage = deletionButton.append(Component.space()).append(formattedMessage);
        }

        Set<StringsUser> recipients = channel.getRecipients(user);
        for (StringsUser recipient : recipients) {
            recipient.sendMessage(formattedMessage);
        }

        Bukkit.getConsoleSender().sendMessage(formattedMessage);
    }

    private void handleMentions(StringsUser sender, String plainMessage) {
        if (!sender.hasPermission("strings.mention")) return;

        for (StringsUser target : plugin.getUserManager().getUsers()) {
            if (!target.isMentionsEnabled()) continue;

            String mentionTag = "@" + target.getName();
            if (plainMessage.toLowerCase().contains(mentionTag.toLowerCase())) {
                plugin.getStringsAPI().mention(target, sender);
            }
        }

        if (sender.hasPermission("strings.mention.all") && plainMessage.contains("@everyone")) {
             for (StringsUser target : plugin.getUserManager().getUsers()) {
                 if (target.isMentionsEnabled()) {
                     plugin.getStringsAPI().mention(target, sender);
                 }
             }
        }
    }
}
