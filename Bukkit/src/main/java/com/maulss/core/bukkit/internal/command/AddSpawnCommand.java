package com.maulss.core.bukkit.internal.command;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.bukkit.internal.config.SpawnsConfig;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.CorePlayerSender;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public final class AddSpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        final CorePlayerSender player = new CorePlayerSender(sender);
        if (!player.isPlayer()) {
            CoreLogger.log("command.only-players");
            return true;
        }

        if (!player.getPlayer().isAdmin()) {
            player.sendMessage("player.error.no-permission");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("command.usage", new String[] {
                    "$usage"
            },		"/addspawn <name>");
            return true;
        }

        final String spawn = args[0];

        try {
            Location location = player.getPlayer().getLocation();
            SpawnsConfig.save(spawn, location);
            player.sendMessage("spawn.add", new String[] {
                    "$spawn",	"$world"
            },		spawn,		location.getWorld().getName());
        } catch (IOException e) {
            player.sendMessage(e.getMessage());
        }

        return true;
    }
}