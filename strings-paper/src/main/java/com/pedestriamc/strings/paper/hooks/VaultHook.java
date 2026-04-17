package com.pedestriamc.strings.paper.hooks;

import com.pedestriamc.strings.paper.StringsPaper;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private final StringsPaper plugin;
    private Chat chat;

    public VaultHook(StringsPaper plugin) {
        this.plugin = plugin;
        setupChat();
    }

    private boolean setupChat() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) {
            return false;
        }
        chat = rsp.getProvider();
        return chat != null;
    }

    public String getPrefix(Player player) {
        return chat != null ? chat.getPlayerPrefix(player) : "";
    }

    public String getSuffix(Player player) {
        return chat != null ? chat.getPlayerSuffix(player) : "";
    }
}
