package com.pedestriamc.strings.paper.listeners;

import com.pedestriamc.strings.api.settings.Option;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.paper.StringsPaper;
import com.pedestriamc.strings.paper.platform.PaperUserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PaperUserListener implements Listener {

    private final StringsPaper plugin;

    public PaperUserListener(StringsPaper plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        StringsUser user = plugin.users().getUser(event.getPlayer().getUniqueId());

        if (plugin.settings().get(Option.Bool.ENABLE_JOIN_LEAVE_MESSAGE)) {
            String joinMsg = plugin.settings().get(Option.Text.JOIN_MESSAGE);
            joinMsg = joinMsg.replace("{username}", user.getName());
            event.joinMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(joinMsg));
        }

        if (plugin.settings().get(Option.Bool.ENABLE_MOTD)) {
            for (String line : plugin.settings().get(Option.StringList.MOTD)) {
                user.sendMessage(line.replace("{username}", user.getName()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        StringsUser user = plugin.users().getUser(event.getPlayer().getUniqueId());

        if (plugin.settings().get(Option.Bool.ENABLE_JOIN_LEAVE_MESSAGE)) {
            String leaveMsg = plugin.settings().get(Option.Text.LEAVE_MESSAGE);
            leaveMsg = leaveMsg.replace("{username}", user.getName());
            event.quitMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(leaveMsg));
        }

        ((PaperUserManager) plugin.users()).unloadUser(user.getUniqueId());
    }
}
