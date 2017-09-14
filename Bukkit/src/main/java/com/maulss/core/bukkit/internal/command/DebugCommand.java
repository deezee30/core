package com.maulss.core.bukkit.internal.command;

import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class DebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        boolean debugging = !CoreLogger.get().debugEnabled();

        if (sender instanceof Player) {
            CorePlayer player = CorePlayerManager.getInstance().get(sender.getName());
            if (!player.isAdmin()) {
                player.sendMessage("player.error.no-permission");
                return true;
            }

            player.sendMessage("Debugging has been temporarily %s", debugging ? "enabled" : "disabled");
        }

        CoreLogger.get().enableDebugging(debugging);
        return true;
    }
}