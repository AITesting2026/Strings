package com.pedestriamc.strings;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.data.BuildableRegistrar;
import com.pedestriamc.strings.api.command.Source;
import com.pedestriamc.strings.api.files.FileManager;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.api.user.UserManager;
import com.pedestriamc.strings.command.BroadcastCommand;
import com.pedestriamc.strings.command.ChannelCommand;
import com.pedestriamc.strings.command.ChatColorCommand;
import com.pedestriamc.strings.command.ClearChatCommand;
import com.pedestriamc.strings.command.ConsoleSource;
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
import com.pedestriamc.strings.listener.ChatListener;
import com.pedestriamc.strings.listener.JoinQuitListener;
import com.pedestriamc.strings.message.MessengerImpl;
import com.pedestriamc.strings.settings.SettingsImpl;
import com.pedestriamc.strings.text.EmojiManagerImpl;
import com.pedestriamc.strings.user.UserManagerImpl;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.UUID;

public class Strings extends JavaPlugin implements StringsPlatform {

    private static Strings instance;
    private final UUID apiUuid = UUID.randomUUID();

    private ChannelLoaderImpl channelLoader;
    private UserManagerImpl userManager;
    private SettingsImpl settings;
    private FileManagerImpl fileManager;
    private MessengerImpl messenger;
    private EmojiManagerImpl emojiManager;
    private StringsAPI api;

    private Chat vaultChat;
    private LuckPerms luckPerms;

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

        BuildableRegistrar.register(this);

        try {
            Method register = StringsProvider.class.getDeclaredMethod("register", StringsAPI.class, JavaPlugin.class, UUID.class);
            register.setAccessible(true);
            register.invoke(null, api, this, apiUuid);
        } catch (Exception e) {
            getLogger().severe("Could not register Strings API!");
            e.printStackTrace();
        }

        channelLoader.refresh();

        setupVault();
        setupLuckPerms();

        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(this), this);

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
                Method unregister = StringsProvider.class.getDeclaredMethod("unregister", UUID.class);
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

    @Override
    public @NotNull ChannelLoader getChannelLoader() {
        return channelLoader;
    }

    @Override
    public @NotNull UserManager users() {
        return userManager;
    }

    @Override
    public @NotNull Settings getSettings() {
        return settings;
    }

    @Override
    public @NotNull FileManager files() {
        return fileManager;
    }

    @Override
    public @NotNull Source serverSource() {
        return ConsoleSource.getInstance();
    }

    @Override
    public void async(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public void info(@NotNull String message) {
        getLogger().info(message);
    }

    @Override
    public void warning(@NotNull String message) {
        getLogger().warning(message);
    }

    public UserManagerImpl getUserManager() {
        return userManager;
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
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) {
            vaultChat = rsp.getProvider();
        }
    }

    public Chat getVaultChat() {
        return vaultChat;
    }

    private void setupLuckPerms() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            return;
        }
        RegisteredServiceProvider<LuckPerms> rsp = getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (rsp != null) {
            luckPerms = rsp.getProvider();
        }
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
