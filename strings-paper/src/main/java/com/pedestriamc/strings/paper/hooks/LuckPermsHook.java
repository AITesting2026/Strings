package com.pedestriamc.strings.paper.hooks;

import com.pedestriamc.strings.paper.StringsPaper;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

public class LuckPermsHook {

    private final StringsPaper plugin;
    private LuckPerms luckPerms;

    public LuckPermsHook(StringsPaper plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            this.luckPerms = LuckPermsProvider.get();
        }
    }

    public String getPrefix(Player player) {
        if (luckPerms == null) return "";
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        return user != null ? user.getCachedData().getMetaData().getPrefix() : "";
    }

    public String getSuffix(Player player) {
        if (luckPerms == null) return "";
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        return user != null ? user.getCachedData().getMetaData().getSuffix() : "";
    }
}
