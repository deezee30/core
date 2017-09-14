/*
 * Part of core.*
 * Created on 04 June 2017 at 3:58 PM.
 */

package com.maulss.core.bukkit.internal.command;

import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.CorePlayerSender;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class GodCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        CorePlayerSender player = new CorePlayerSender(sender);
        boolean isPlayer = player.isPlayer();

        if (isPlayer && !player.getPlayer().isMod()) {
            player.sendMessage("player.error.no-permission");
            return true;
        }

        switch (args.length) {
            default:
                player.sendMessage("command.usage", new String[] {"usage"}, "/god (<player>)");
                return true;
            case 0:
                if (!isPlayer)
                    CoreLogger.log("command.usage", new String[] {"usage"}, "/god <player>");
                else {
                    boolean disable = !player.getPlayer().isDamageable();
                    player.getPlayer().setDamageable(disable);
                    player.sendMessage("god." + (disable ? "disable" : "enable"));
                }

                return true;
            case 1:
                String targetName = args[0];
                CorePlayer target = CorePlayerManager.getInstance().get(targetName);
                if (target == null) {
                    player.sendMessage("player.error.not-found", new String[]{"$player"}, targetName);
                    return true;
                }

                boolean damageable = !target.isDamageable();
                target.setDamageable(damageable);

                String msg = "god." + (damageable ? "disable" : "enable");

                target.sendMessage(msg);
                player.sendMessage(msg);
        }

        return true;
    }
}