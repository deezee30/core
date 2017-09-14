/*
 * Part of core.*
 * Created on 03 June 2017 at 10:45 PM.
 */

package com.maulss.core.bukkit.internal.command;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.bukkit.internal.config.MainConfig;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.CorePlayerSender;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class ClearChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        final CorePlayerSender player = new CorePlayerSender(sender);
        if (!player.isPlayer()) {
            clearChat(sender);
            return true;
        }

        if (!player.getPlayer().isAdmin()) {
            player.sendMessage("player.error.no-permission");
            return true;
        }

        if (args.length != 0) {
            player.sendMessage("command.usage", new String[] {
                    "$usage"
            }, "/clearchat");
            return true;
        }

        clearChat(sender);
        return true;
    }

    private void clearChat(CommandSender sender) {
        for (int i = 0; i < MainConfig.getClearChatLines(); i++) {
            CoreLogger.broadcast("~");
        }

        CoreLogger.broadcast("chat.clear", new String[] {"$player"}, sender.getName());
    }
}