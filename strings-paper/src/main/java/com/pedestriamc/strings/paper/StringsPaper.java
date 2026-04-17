package com.pedestriamc.strings.paper;

import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.local.LocalityManager;
import com.pedestriamc.strings.api.command.Source;
import com.pedestriamc.strings.api.event.strings.EventManager;
import com.pedestriamc.strings.api.files.FileManager;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.settings.Settings;
import com.pedestriamc.strings.api.text.EmojiManager;
import com.pedestriamc.strings.api.user.UserManager;
import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.common.CommonStrings;
import com.pedestriamc.strings.common.chat.EmojiProvider;
import com.pedestriamc.strings.common.channel.impl.HelpOPChannel;
import com.pedestriamc.strings.common.channel.impl.StringChannel;
import com.pedestriamc.strings.common.channel.impl.local.proximity.SphericalProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.world.StandardWorldChannel;
import com.pedestriamc.strings.paper.chat.PaperMessageProcessor;
import com.pedestriamc.strings.common.event.StringsEventManager;
import com.pedestriamc.strings.common.manager.DirectMessageManager;
import com.pedestriamc.strings.common.manager.StringsMentioner;
import com.pedestriamc.strings.paper.api.PaperStringsAPI;
import com.pedestriamc.strings.paper.platform.*;
import com.pedestriamc.strings.paper.channel.PaperLocalityManager;
import com.pedestriamc.strings.paper.hooks.LuckPermsHook;
import com.pedestriamc.strings.paper.hooks.VaultHook;
import com.pedestriamc.strings.paper.tasks.AutoBroadcastTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.UUID;

public class StringsPaper extends JavaPlugin implements CommonStrings {

    private PaperFileManager fileManager;
    private PaperPlatformAdapter adapter;
    private PaperEventFactory eventFactory;
    private PaperUserManager userManager;
    private PaperChannelLoader channelLoader;
    private StringsEventManager eventManager;
    private DirectMessageManager dmManager;
    private PaperMessageProcessor messageProcessor;
    private StringsMentioner mentioner;
    private EmojiManager emojiManager;
    private Messenger messenger;
    private Settings settings;
    private AutoBroadcastTask autoBroadcastTask;
    private VaultHook vaultHook;
    private LuckPermsHook luckPermsHook;

    private final UUID pluginUuid = UUID.randomUUID();

    @Override
    public void onEnable() {
        this.fileManager = new PaperFileManager(this);
        this.adapter = new PaperPlatformAdapter(this);
        this.eventFactory = new PaperEventFactory();
        this.eventManager = new StringsEventManager(this);
        this.channelLoader = new PaperChannelLoader(this, fileManager);
        this.userManager = new PaperUserManager(this, fileManager);
        this.mentioner = new StringsMentioner(this);
        this.emojiManager = new EmojiProvider(this);
        this.messenger = new PaperMessenger(this, fileManager);
        this.settings = new PaperSettings(this, fileManager);
        this.messageProcessor = new PaperMessageProcessor(this);
        this.dmManager = new DirectMessageManager(this);
        this.vaultHook = new VaultHook(this);
        this.luckPermsHook = new LuckPermsHook(this);

        initializeBuilderRegistry();
        registerAPI();
        registerListeners();
        registerCommands();
        startTasks();
    }

    private void startTasks() {
        this.autoBroadcastTask = new AutoBroadcastTask(this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new com.pedestriamc.strings.paper.listeners.PaperChatListener(this), this);
        getServer().getPluginManager().registerEvents(new com.pedestriamc.strings.paper.listeners.PaperUserListener(this), this);
    }

    private void registerCommands() {
        getCommand("broadcast").setExecutor(new com.pedestriamc.strings.paper.commands.BroadcastCommand(this));
        getCommand("clearchat").setExecutor(new com.pedestriamc.strings.paper.commands.ClearChatCommand(this));
        getCommand("channel").setExecutor(new com.pedestriamc.strings.paper.commands.ChannelCommand(this));
        getCommand("msg").setExecutor(new com.pedestriamc.strings.paper.commands.MsgCommand(this));
        getCommand("r").setExecutor(new com.pedestriamc.strings.paper.commands.ReplyCommand(this));
        getCommand("helpop").setExecutor(new com.pedestriamc.strings.paper.commands.HelpOPCommand(this));
        getCommand("socialspy").setExecutor(new com.pedestriamc.strings.paper.commands.SocialSpyCommand(this));
        getCommand("chatcolor").setExecutor(new com.pedestriamc.strings.paper.commands.ChatColorCommand(this));
        getCommand("ignore").setExecutor(new com.pedestriamc.strings.paper.commands.IgnoreCommand(this));
        getCommand("emoji").setExecutor(new com.pedestriamc.strings.paper.commands.EmojiCommand(this));
        getCommand("strings").setExecutor(new com.pedestriamc.strings.paper.commands.StringsCommand(this));
    }

    private void initializeBuilderRegistry() {
        try {
            Class<?> builderRegistryClass = Class.forName("com.pedestriamc.strings.api.channel.data.BuilderRegistry");
            Class<?> iChannelBuilderIdentifierClass = Class.forName("com.pedestriamc.strings.api.channel.data.IChannelBuilder$Identifier");

            Method registerPlatformMethod = builderRegistryClass.getDeclaredMethod("registerPlatform", com.pedestriamc.strings.api.StringsPlatform.class);
            registerPlatformMethod.setAccessible(true);
            registerPlatformMethod.invoke(null, this);

            Method registerBuildableMethod = builderRegistryClass.getDeclaredMethod("registerBuildable", iChannelBuilderIdentifierClass, java.util.function.BiFunction.class);
            registerBuildableMethod.setAccessible(true);

            Method registerLocalBuildableMethod = builderRegistryClass.getDeclaredMethod("registerLocalBuildable", iChannelBuilderIdentifierClass, java.util.function.BiFunction.class);
            registerLocalBuildableMethod.setAccessible(true);

            Object normalIdentifier = Enum.valueOf((Class<Enum>) iChannelBuilderIdentifierClass, "NORMAL");
            Object helpopIdentifier = Enum.valueOf((Class<Enum>) iChannelBuilderIdentifierClass, "HELPOP");
            Object worldIdentifier = Enum.valueOf((Class<Enum>) iChannelBuilderIdentifierClass, "WORLD");
            Object proximityIdentifier = Enum.valueOf((Class<Enum>) iChannelBuilderIdentifierClass, "PROXIMITY");

            registerBuildableMethod.invoke(null, normalIdentifier, (java.util.function.BiFunction<com.pedestriamc.strings.api.StringsPlatform, com.pedestriamc.strings.api.channel.data.ChannelBuilder, com.pedestriamc.strings.api.channel.Channel>) StringChannel::new);
            registerBuildableMethod.invoke(null, helpopIdentifier, (java.util.function.BiFunction<com.pedestriamc.strings.api.StringsPlatform, com.pedestriamc.strings.api.channel.data.ChannelBuilder, com.pedestriamc.strings.api.channel.Channel>) HelpOPChannel::new);

            registerLocalBuildableMethod.invoke(null, worldIdentifier, (java.util.function.BiFunction<com.pedestriamc.strings.api.StringsPlatform, com.pedestriamc.strings.api.channel.data.LocalChannelBuilder<?>, com.pedestriamc.strings.api.channel.Channel>) StandardWorldChannel::new);
            registerLocalBuildableMethod.invoke(null, proximityIdentifier, (java.util.function.BiFunction<com.pedestriamc.strings.api.StringsPlatform, com.pedestriamc.strings.api.channel.data.LocalChannelBuilder<?>, com.pedestriamc.strings.api.channel.Channel>) SphericalProximityChannel::new);

        } catch (Exception e) {
            getLogger().severe("Failed to initialize BuilderRegistry!");
            e.printStackTrace();
        }
    }

    private void registerAPI() {
        try {
            PaperStringsAPI api = new PaperStringsAPI(this);
            Method registerMethod = StringsProvider.class.getDeclaredMethod("register", StringsAPI.class, UUID.class);
            registerMethod.setAccessible(true);
            registerMethod.invoke(null, api, pluginUuid);
        } catch (Exception e) {
            getLogger().severe("Failed to register Strings API!");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Implementation will follow in subsequent steps
    }

    @Override
    public @NotNull DirectMessageManager getDirectMessageManager() {
        return dmManager;
    }

    @Override
    public @NotNull PlatformAdapter getAdapter() {
        return adapter;
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
    public @NotNull Settings settings() {
        return settings;
    }

    @Override
    public @NotNull FileManager files() {
        return fileManager;
    }

    @Override
    public @NotNull Source serverSource() {
        return new Source() {
            @Override
            public @NotNull String getName() {
                return "Console";
            }

            @Override
            public void sendMessage(@NotNull String message) {
                Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
            }

            @Override
            public void sendMessage(@NotNull Component component) {
                Bukkit.getConsoleSender().sendMessage(component);
            }
        };
    }

    @Override
    public @NotNull EventManager eventManager() {
        return eventManager;
    }

    @Override
    public @NotNull EventFactory eventFactory() {
        return eventFactory;
    }

    @Override
    public @NotNull EmojiManager emojiManager() {
        return emojiManager;
    }

    @Override
    public @NotNull LocalityManager<?> localityManager() {
        return new PaperLocalityManager(this);
    }

    @Override
    public @NotNull Messenger messenger() {
        return messenger;
    }

    @Override
    public void async(@NotNull Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public void sync(@NotNull Runnable runnable) {
        getServer().getScheduler().runTask(this, runnable);
    }

    @Override
    public void info(@NotNull String message) {
        getLogger().info(message);
    }

    @Override
    public void warning(@NotNull String message) {
        getLogger().warning(message);
    }

    @Override
    public boolean isUsingPlaceholderAPI() {
        return getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public LuckPermsHook getLuckPermsHook() {
        return luckPermsHook;
    }
}
