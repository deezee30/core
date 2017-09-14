/*
 * Part of core.
 * Made on 01/09/2017
 */

package com.maulss.core.bukkit;

import com.google.common.collect.ImmutableMap;
import com.maulss.core.CoreException;
import com.maulss.core.bukkit.chat.ChatMessages;
import com.maulss.core.bukkit.displaybar.actionbar.ActionBar;
import com.maulss.core.bukkit.displaybar.title.Title;
import com.maulss.core.bukkit.internal.command.*;
import com.maulss.core.bukkit.internal.config.DatabaseConfig;
import com.maulss.core.bukkit.internal.config.MainConfig;
import com.maulss.core.bukkit.internal.listener.player.PlayerListeners;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import com.maulss.core.bukkit.world.CoreWorldManager;
import com.maulss.core.bukkit.world.Position;
import com.maulss.core.bukkit.world.region.Region;
import com.maulss.core.bukkit.world.region.Regions;
import com.maulss.core.bukkit.world.region.flag.Flag;
import com.maulss.core.bukkit.world.region.flag.FlagMap;
import com.maulss.core.bukkit.world.schematic.Schematics;
import com.maulss.core.database.Credentials;
import com.maulss.core.database.mongo.Mongo;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Core extends CorePlugin {

    private static final String PLUGIN_NOT_LOADED = "Core plugin hasn't been loaded yet";
    private static final String DATABASE_NOT_LOADED = "Database hasn't been loaded yet";

    private static Core instance;
    private final CoreSettings settings = new CoreSettings();
    private Mongo dbConnection = null;
    private MongoDatabase internalDb = null;
    private MongoCollection<Document> mainPlayerColl = null;

    @Override
    @Deprecated
    protected void enable() {
        try {
            if (instance == null) {
                instance = this;
            } else {
                throw new CoreException("Core has already been loaded");
            }

            new CoreLogger(this);

            ConfigurationSerialization.registerClass(Position.class);
            ConfigurationSerialization.registerClass(FlagMap.class);
            ConfigurationSerialization.registerClass(Region.class);
            ConfigurationSerialization.registerClass(Title.class);
            ConfigurationSerialization.registerClass(ActionBar.class);

            settings.initClasses(
                    // Load configuration and language files
                    "com.riddlesvillage.core.internal.config.MessagesConfig",
                    "com.riddlesvillage.core.internal.config.MainConfig",
                    "com.riddlesvillage.core.internal.config.SpawnsConfig",
                    "com.riddlesvillage.core.internal.config.DatabaseConfig"
            );

            // Set up default language for players
            settings.addLocale(MainConfig.getDefaultLocale());

            // Internal event listeners
            settings.registerListeners(this, PlayerListeners.get());
            settings.registerListeners(this, CoreWorldManager.getInstance());

            // Register default RiddlesCore commands
            settings.registerCommands(this, new ImmutableMap.Builder<String, CommandExecutor>()
                    .put("addspawn",    new AddSpawnCommand())
                    .put("clearchat",   new ClearChatCommand())
                    .put("coins",       new CoinsCommand())
                    .put("debug",       new DebugCommand())
                    .put("iphistory",   new IpHistoryCommand())
                    .put("namehistory", new NameHistoryCommand())
                    .put("god",         new GodCommand())
                    .put("premium",     new PremiumCommand())
                    .put("rank",        new RankCommand())
                    .put("teleport",    new TeleportCommand())
                    .put("stats",       new StatsCommand())
                    .put("tokens",      new TokensCommand())
                    .put("tpspawn",     new TPSpawnCommand())
                    .put("vanish",      new VanishCommand())
                    .build()
            );

            // Allow commands when commands are disabled
            settings.addAllowedCommands(MainConfig.getAllowedCommands());

            // Register default chat block filters
            settings.getChatFilters().registerDefaults();

            // Set up default language for players
            settings.addLocale(MainConfig.getDefaultLocale());

            // Initialize database connection and setup management if credentials are set
            Credentials cr = DatabaseConfig.getCredentials();
            if (!cr.isSet()) {
                log("~&4Default credentials have been generated");
                log("~&4in file 'plugins/Core/database.yml'");
                log("~&4Please change them to actual credentials...");
                Bukkit.shutdown();
                return;
            }

            dbConnection = Mongo.setup(cr)
                    .setDescription("Default core-Mongo database plugin connection")
                    .setCodecRegistry()
                    .setupThreads();

            dbConnection.connect(throwable -> {
                if (throwable == null) {
                    log("~&4A database connection exception has been caught");
                    log("~&4Make sure the database is alive and functional");
                    log("~&4Also make sure the credentials provided are correct");
                    log("~&4Once these checks have been completed, try again");
                }

                // Finish up loading

                // TODO: Store player collections as their player codecs instead of Documents
                // TODO: mainPlayerColl = internalDb.getCollection("players", CoreOfflinePlayer.class);
                mainPlayerColl = internalDb.getCollection("players");
            });

            if (!dbConnection.isConnected()) {
                Bukkit.shutdown();
                return;
            }

            internalDb = dbConnection.getClient().getDatabase(cr.getDatabase());

            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

            Flag.init();
            // init region manager and load default regions
            Regions.getManager().init();
            // init schematics
            Schematics.init(false);
        } catch (Exception exception) {
            log("~&4A startup error has occurred - shutting down");
            exception.printStackTrace();
            Bukkit.shutdown();
        }
    }

    @Override
    @Deprecated
    protected void disable() {
        // Paste all messages in this session
        ChatMessages.getInstance().pasteChatMessages();

        // Kick all players
        for (CorePlayer player : CorePlayerManager.getInstance()) {
            Player bukkitPlayer = player.getPlayer();
            bukkitPlayer.kickPlayer(settings.get("restart"));
            Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(bukkitPlayer, null));
        }

        // Close the database dbConnection after 5 milliseconds for all tasks to finish first
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
        ex.schedule(dbConnection::close, 5, TimeUnit.MILLISECONDS);
        ex.shutdown();
    }

    public static CoreSettings getSettings() {
        return instance.settings;
    }

    public static MongoCollection<Document> getPlayerCollection() {
        getDatabase();
        return instance.mainPlayerColl;
    }

    public static Mongo getDatabaseConnection() {
        get();
        return instance.dbConnection;
    }

    public static MongoDatabase getDatabase() {
        get();
        if (instance.dbConnection == null || !instance.dbConnection.isConnected())
            throw new IllegalStateException(DATABASE_NOT_LOADED);
        return instance.internalDb;
    }

    public static Core get() {
        if (instance == null)
            throw new IllegalStateException(PLUGIN_NOT_LOADED);
        return instance;
    }
}