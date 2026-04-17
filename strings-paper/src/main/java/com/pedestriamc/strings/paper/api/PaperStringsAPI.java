package com.pedestriamc.strings.paper.api;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.event.strings.EventManager;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.paper.StringsPaper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PaperStringsAPI implements StringsAPI {

    private final StringsPaper plugin;

    public PaperStringsAPI(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ChannelLoader getChannelLoader() {
        return plugin.getChannelLoader();
    }

    @Override
    public short getVersion() {
        return 170; // 1.7.0
    }

    @Override
    public @Nullable StringsUser getUser(@NotNull UUID uuid) {
        return plugin.users().getUser(uuid);
    }

    @Override
    public void saveUser(@NotNull StringsUser user) {
        plugin.users().saveUser(user);
    }

    @Override
    public boolean isPaper() {
        return true;
    }

    @Override
    public void mention(@NotNull StringsUser subject, @NotNull StringsUser sender) {
    }

    @Override
    public void sendMention(@NotNull StringsUser user, @NotNull String message) {
    }

    @Override
    public @NotNull Messenger getMessenger() {
        return plugin.messenger();
    }

    @Override
    public @NotNull Settings getSettings() {
        return plugin.settings();
    }

    @Override
    public @NotNull EmojiManager emojiManager() {
        return plugin.emojiManager();
    }

    @Override
    public @NotNull EventManager getEventDispatcher() {
        return plugin.eventManager();
    }
}
