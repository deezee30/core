/*
 * Part of core.
 * Made on 13/09/2017
 */

package com.maulss.core.bukkit.player;

import com.maulss.core.bukkit.CoreLogger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CorePlayerSender implements CommandSender {

    private final CommandSender sender;
    private final Optional<CorePlayer> player;

    public CorePlayerSender(final CommandSender sender) {
        this.sender = sender == null ? Bukkit.getConsoleSender() : sender;
        this.player = CorePlayer.getOnline(this.sender.getName());
    }

    public boolean isPlayer() {
        return player.isPresent();
    }

    public CorePlayer getPlayer() {
        return player.orElse(null);
    }

    public CommandSender getSender() {
        return sender;
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(message, new Object[] {});
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    public void sendMessages(final String[] paths,
                             final Object... components) {
        for (String path : paths) {
            sendMessage(path, components);
        }
    }

    public void sendMessage(final String path,
                            final Object... components) {
        if (player.isPresent()) {
            player.get().sendMessage(path, components);
        } else {
            CoreLogger.log(path, components);
        }
    }

    public void sendMessage(final String path,
                            final String[] keys,
                            final Object... vals) {
        if (player.isPresent()) {
            player.get().sendMessage(path, keys, vals);
        } else {
            CoreLogger.log(path, keys, vals);
        }
    }

    public void sendMessage(final String path,
                            final Map<String, Object> replacements) {
        if (player.isPresent()) {
            player.get().sendMessage(path, replacements);
        } else {
            CoreLogger.log(path, replacements);
        }
    }

    @Override
    public Server getServer() {
        return sender.getServer();
    }

    @Override
    public String getName() {
        return player.isPresent() ? player.get().getName() : sender.getName();
    }

    @Override
    public Spigot spigot() {
        return sender.spigot();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return sender.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return sender.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return sender.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return sender.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return sender.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return sender.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return sender.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return sender.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        sender.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        sender.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return sender.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return (player.isPresent() && player.get().isAdmin()) || sender.isOp();
    }

    @Override
    public void setOp(boolean value) {
        sender.setOp(true);
    }
}