package com.maulss.core.bukkit.internal.command;

import com.google.common.collect.ImmutableList;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class StatsCommand implements CommandExecutor {

    private static final String             ERROR   = "player.error.not-found";
    private static final String             USAGE   = "/stats | /stats <player>";
    private static final CorePlayerManager  MANAGER = CorePlayer.PLAYER_MANAGER;

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        final boolean isPlayer = sender instanceof Player;
        final CorePlayer playerSender = MANAGER.get(sender.getName());

        switch (args.length) {
            case 0:
                if (!CoreLogger.logIf(!isPlayer, "command.only-players")) {
                    ImmutableList<String> l = playerSender.getStatisticValues();
                    playerSender.sendMessages(l.toArray(new String[l.size()]));
                }
                break;

            case 1:
                String victimName = args[0];

                CorePlayer.get(victimName, victim -> {
                    if (victim == null) {
                        if (!CoreLogger.logIf(!isPlayer, ERROR, new String[] {"$player"}, victimName)) {
                            playerSender.sendMessage(ERROR, new String[] {"$player"}, victimName);
                        }
                    } else {
                        List<String> l = victim.getStatisticValues();
                        if (isPlayer) {
                            playerSender.sendMessages(l.toArray(new String[l.size()]));
                        } else {
                            l.forEach(s -> CoreLogger.log(s));
                        }
                    }
                });

                break;

            default:
                if (!CoreLogger.logIf(!isPlayer, "command.usage", new String[] {"$usage"}, USAGE)) {
                    playerSender.sendMessage("command.usage", new String[] {"$usage"}, USAGE);
                }
        }
        return true;
    }
}