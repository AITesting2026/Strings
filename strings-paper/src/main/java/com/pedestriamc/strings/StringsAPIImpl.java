package com.pedestriamc.strings;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StringsAPIImpl implements StringsAPI {
    private final Strings plugin;

    public StringsAPIImpl(Strings plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ChannelLoader getChannelLoader() {
        return plugin.getChannelLoader();
    }

    @Override
    public short getVersion() {
        return 170;
    }

    @Override
    public @Nullable StringsUser getUser(@NotNull UUID uuid) {
        return plugin.getUserManager().getUser(uuid);
    }

    @Override
    public void saveUser(@NotNull StringsUser user) {
        plugin.getUserManager().saveUser(user);
    }

    @Override
    public boolean isPaper() {
        return true;
    }

    @Override
    public void mention(@NotNull StringsUser subject, @NotNull StringsUser sender) {
        String mentionFormat = plugin.getSettings().get(Option.Text.MENTION_TEXT_ACTION_BAR);
        String message = mentionFormat.replace("%sender%", sender.getName());
        sendMention(subject, message);

        String soundName = plugin.getSettings().get(Option.Text.MENTION_SOUND);
        double volume = plugin.getSettings().get(Option.Double.MENTION_VOLUME);
        double pitch = plugin.getSettings().get(Option.Double.MENTION_PITCH);

        Player player = Bukkit.getPlayer(subject.getUniqueId());
        if (player != null) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(soundName), (float)volume, (float)pitch);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @Override
    public void sendMention(@NotNull StringsUser user, @NotNull String message) {
        Player player = Bukkit.getPlayer(user.getUniqueId());
        if (player != null) {
            player.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        }
    }

    @Override
    public @NotNull Messenger getMessenger() {
        return plugin.getMessenger();
    }

    @Override
    public @NotNull Settings getSettings() {
        return plugin.getSettings();
    }

    @Override
    public @NotNull EmojiManager emojiManager() {
        return plugin.getEmojiManager();
    }
}
