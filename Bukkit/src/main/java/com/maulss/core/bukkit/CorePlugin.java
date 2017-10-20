/*
 * Part of core.
 * Made on 01/09/2017
 */

package com.maulss.core.bukkit;

import com.google.common.collect.Iterables;
import com.maulss.core.Logger;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import com.maulss.core.service.timer.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public abstract class CorePlugin extends JavaPlugin {

    private static CoreSettings settings;

    protected Timer timer = new Timer();
    protected Logger logger = new Logger(getName() + " >> ");

    @Override
    public void onLoad() {
        if (settings == null)
            settings = Core.getSettings();
    }

    @Override
    public final void onEnable() {
        timer.start();
        findAndRegisterLocales();

        try {
            enable();
        } catch (Exception exception) {
            log("~&4Uncaught exception while enabling %s! Turning off...", this);
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        timer.forceStop();
        logger.log("Loaded %s in %sms", getName(), timer.getTime(TimeUnit.MILLISECONDS));
    }

    protected abstract void enable();

    @Override
    public final void onDisable() {
        disable();
    }

    protected abstract void disable();

    public final Logger logger() {
        return logger;
    }

    public static CoreSettings getSettings() {
        return settings;
    }

    @Override
    public final String toString() {
        return getName();
    }

    public final boolean sendServerMessage(final String channel,
                                     final byte[] data) {
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player == null) return false;

        player.sendPluginMessage(this, channel, data);
        return true;
    }

    public final void initClasses(final String... classes) {
        settings.initClasses(classes);
    }

    public final void registerCommands(final Map<String, CommandExecutor> commandExecutorMap) {
        settings.registerCommands(this, commandExecutorMap);
    }

    public final void registerListeners(final Listener... listeners) {
        settings.registerListeners(this, listeners);
    }

    public final void findAndRegisterLocales() {
        settings.findAndRegisterLocales(this);
    }

    public final Optional<String> log(final String path,
                                      final Object... components) {
        return logger.log(parseColors(settings.get(path)), components);
    }

    public final Optional<String> log(final String path,
                                      final String[] keys,
                                      final Object... vals) {
        return log(Logger.constructReplacements(settings.get(path), keys, vals));
    }

    public final Optional<String> log(final String path,
                                      final Map<String, Object> replacements) {
        return log(Logger.constructReplacements(settings.get(path), replacements));
    }

    public final boolean logIf(final boolean check,
                               final String path,
                               final Object... components) {
        return logger.logIf(check, parseColors(settings.get(path)), components);
    }

    public final boolean logIf(final boolean check,
                               final String path,
                               final String[] keys,
                               final Object... vals) {
        return logIf(check, Logger.constructReplacements(settings.get(path), keys, vals));
    }

    public final boolean logIf(final boolean check,
                               final String path,
                               final Map<String, Object> replacements) {
        return logIf(check, Logger.constructReplacements(settings.get(path), replacements));
    }

    public final Optional<String> debug(final String string,
                                        final Object... components) {
        return logger.debug(parseColors(string), components);
    }

    public final boolean debugIf(final boolean check,
                                 final String string,
                                 final Object... components) {
        return logger.debugIf(check, parseColors(string), components);
    }

    public final void broadcast(final String message,
                                final Object... components) {
        for (CorePlayer player : CorePlayerManager.getInstance()) {
            player.sendMessage(message, components);
        }

        log(message, components);
    }

    public final void broadcast(final String path,
                                final String[] keys,
                                final Object... vals) {
        broadcast(Logger.constructReplacements(settings.get(path), keys, vals));
    }

    public final void broadcast(final String path,
                                final Map<String, Object> replacements) {
        broadcast(Logger.constructReplacements(settings.get(path), replacements));
    }

    private String parseColors(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}