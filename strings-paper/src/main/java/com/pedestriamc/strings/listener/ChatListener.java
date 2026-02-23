package com.pedestriamc.strings.listener;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

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
            user.setActiveChannel(plugin.getChannelLoader().getChannel("global"));
            channel = user.resolveActiveChannel();
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
            rawMessage = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, rawMessage);
        }

        final String finalRawMessage = rawMessage;
        Component messageComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(user.getChatColorComponent().toString() + finalRawMessage);

        // Mentions
        handleMentions(user, messageComponent, channel);

        // Formatting
        String format = channel.getFormat();
        // Placeholders: {prefix}, {suffix}, {displayname}, {message}

        String prefix = user.getPrefix();
        String suffix = user.getSuffix();
        String displayName = user.getDisplayName();

        // Vault/LuckPerms integration for prefix/suffix would go here if not already set in user

        Component formattedMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(format
                .replace("{prefix}", prefix)
                .replace("{suffix}", suffix)
                .replace("{displayname}", displayName)
                .replace("{message}", "") // We'll append the message component
        ).append(messageComponent);

        // Deletion button
        if (channel.allowsMessageDeletion()) {
            UUID messageId = UUID.randomUUID();
            com.pedestriamc.strings.message.MessageHistory.addMessage(messageId, formattedMessage);

            String deletionFormat = plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Text.DELETION_BUTTON_FORMAT);
            String deletionHover = plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Text.DELETION_BUTTON_HOVER);

            Component deletionButton = LegacyComponentSerializer.legacyAmpersand().deserialize(deletionFormat)
                    .hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(deletionHover)))
                    .clickEvent(ClickEvent.runCommand("/strings delete " + messageId));

            formattedMessage = deletionButton.append(Component.space()).append(formattedMessage);
        }

        Set<StringsUser> recipients = channel.getRecipients(user);
        for (StringsUser recipient : recipients) {
            recipient.sendMessage(formattedMessage);
        }

        // Log to console
        Bukkit.getConsoleSender().sendMessage(formattedMessage);
    }

    private void handleMentions(StringsUser sender, Component message, Channel channel) {
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(message);
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
