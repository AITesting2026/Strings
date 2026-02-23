package com.pedestriamc.strings.listener;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final Strings plugin;

    public JoinQuitListener(Strings plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        StringsUser user = plugin.getUserManager().loadUser(event.getPlayer().getUniqueId(), event.getPlayer().getName());

        Channel global = plugin.getChannelLoader().getChannel("global");
        if (user.getActiveChannel() == null) {
            user.setActiveChannel(global);
        }
        if (!user.memberOf(global)) {
            user.joinChannel(global);
        }

        if (plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Bool.ENABLE_JOIN_LEAVE_MESSAGE)) {
            String joinMsg = plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Text.JOIN_MESSAGE)
                    .replace("{username}", event.getPlayer().getName());
            event.joinMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(joinMsg));
        } else {
            event.joinMessage(null);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Bool.ENABLE_JOIN_LEAVE_MESSAGE)) {
            String leaveMsg = plugin.getSettings().get(com.pedestriamc.strings.api.settings.Option.Text.LEAVE_MESSAGE)
                    .replace("{username}", event.getPlayer().getName());
            event.quitMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(leaveMsg));
        } else {
            event.quitMessage(null);
        }

        plugin.getUserManager().unloadUser(event.getPlayer().getUniqueId());
    }
}
