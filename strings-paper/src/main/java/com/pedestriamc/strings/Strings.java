package com.pedestriamc.strings;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.data.BuildableRegistrar;
import com.pedestriamc.strings.api.files.FileManager;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.command.BroadcastCommand;
import com.pedestriamc.strings.command.ChannelCommand;
import com.pedestriamc.strings.command.ChatColorCommand;
import com.pedestriamc.strings.command.ClearChatCommand;
import com.pedestriamc.strings.command.EmojiCommand;
import com.pedestriamc.strings.command.HelpOpCommand;
import com.pedestriamc.strings.command.IgnoreCommand;
import com.pedestriamc.strings.command.MentionCommand;
import com.pedestriamc.strings.command.MsgCommand;
import com.pedestriamc.strings.command.ReplyCommand;
import com.pedestriamc.strings.command.RulesCommand;
import com.pedestriamc.strings.command.SocialSpyCommand;
import com.pedestriamc.strings.command.StringsCommand;
import com.pedestriamc.strings.command.UnignoreCommand;
import com.pedestriamc.strings.channel.ChannelLoaderImpl;
import com.pedestriamc.strings.files.FileManagerImpl;
import com.pedestriamc.strings.message.MessengerImpl;
import com.pedestriamc.strings.settings.SettingsImpl;
import com.pedestriamc.strings.text.EmojiManagerImpl;
import com.pedestriamc.strings.user.UserManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Strings extends JavaPlugin {

    private static Strings instance;
    private final UUID apiUuid = UUID.randomUUID();

    private ChannelLoader channelLoader;
    private UserManagerImpl userManager;
    private Settings settings;
    private FileManager fileManager;
    private Messenger messenger;
    private EmojiManager emojiManager;
    private StringsAPI api;

    private net.milkbowl.vault.chat.Chat vaultChat;
    private net.luckperms.api.LuckPerms luckPerms;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.fileManager = new FileManagerImpl(this);
        this.settings = new SettingsImpl(this);
        this.userManager = new UserManagerImpl(this);
        this.channelLoader = new ChannelLoaderImpl(this);
        this.messenger = new MessengerImpl(this);
        this.emojiManager = new EmojiManagerImpl(this);

        this.api = new StringsAPIImpl(this);

        BuildableRegistrar.register(new PaperStringsPlatform(this));

        try {
            java.lang.reflect.Method register = StringsProvider.class.getDeclaredMethod("register", StringsAPI.class, org.bukkit.plugin.java.JavaPlugin.class, UUID.class);
            register.setAccessible(true);
            register.invoke(null, api, this, apiUuid);
        } catch (Exception e) {
            getLogger().severe("Could not register Strings API!");
            e.printStackTrace();
        }

        channelLoader.refresh();

        setupVault();
        setupLuckPerms();

        Bukkit.getPluginManager().registerEvents(new com.pedestriamc.strings.listener.ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.pedestriamc.strings.listener.JoinQuitListener(this), this);

        MsgCommand msgCommand = new MsgCommand(this);
        SocialSpyCommand socialSpyCommand = new SocialSpyCommand(this);
        msgCommand.setSocialSpyCommand(socialSpyCommand);

        getCommand("msg").setExecutor(msgCommand);
        getCommand("message").setExecutor(msgCommand);
        getCommand("reply").setExecutor(new ReplyCommand(this, msgCommand));
        getCommand("r").setExecutor(new ReplyCommand(this, msgCommand));
        getCommand("socialspy").setExecutor(socialSpyCommand);
        getCommand("ignore").setExecutor(new IgnoreCommand(this));
        getCommand("unignore").setExecutor(new UnignoreCommand(this));
        getCommand("helpop").setExecutor(new HelpOpCommand(this));

        getCommand("channel").setExecutor(new ChannelCommand(this));
        getCommand("c").setExecutor(new ChannelCommand(this));
        getCommand("clearchat").setExecutor(new ClearChatCommand(this));
        getCommand("chatclear").setExecutor(new ClearChatCommand(this));
        getCommand("broadcast").setExecutor(new BroadcastCommand(this));
        getCommand("announce").setExecutor(new BroadcastCommand(this));
        getCommand("chatcolor").setExecutor(new ChatColorCommand(this));

        getCommand("strings").setExecutor(new StringsCommand(this));
        getCommand("mention").setExecutor(new MentionCommand(this));
        getCommand("mentions").setExecutor(new MentionCommand(this));
        getCommand("rules").setExecutor(new RulesCommand(this));
        getCommand("emoji").setExecutor(new EmojiCommand(this));
    }

    @Override
    public void onDisable() {
        if (api != null) {
            try {
                java.lang.reflect.Method unregister = StringsProvider.class.getDeclaredMethod("unregister", UUID.class);
                unregister.setAccessible(true);
                unregister.invoke(null, apiUuid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Strings getInstance() {
        return instance;
    }

    public ChannelLoader getChannelLoader() {
        return channelLoader;
    }

    public UserManagerImpl getUserManager() {
        return userManager;
    }

    public Settings getSettings() {
        return settings;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public EmojiManager getEmojiManager() {
        return emojiManager;
    }

    public StringsAPI getStringsAPI() {
        return api;
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        org.bukkit.plugin.RegisteredServiceProvider<net.milkbowl.vault.chat.Chat> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (rsp != null) {
            vaultChat = rsp.getProvider();
        }
    }

    public net.milkbowl.vault.chat.Chat getVaultChat() {
        return vaultChat;
    }

    private void setupLuckPerms() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            return;
        }
        org.bukkit.plugin.RegisteredServiceProvider<net.luckperms.api.LuckPerms> rsp = getServer().getServicesManager().getRegistration(net.luckperms.api.LuckPerms.class);
        if (rsp != null) {
            luckPerms = rsp.getProvider();
        }
    }

    public net.luckperms.api.LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
