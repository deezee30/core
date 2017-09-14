/*
 * Part of core.*
 * Created on 04 June 2017 at 7:11 PM.
 */

package com.maulss.core.bukkit.internal.command;

import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.bukkit.Rank;
import com.maulss.core.bukkit.internal.CoreRank;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import com.maulss.core.text.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public final class RankCommand implements CommandExecutor {

    private static final CorePlayerManager MANAGER = CorePlayerManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        // This command is only executable via console
        CorePlayer player = MANAGER.get(sender.getName());
        if (player != null) {
            player.sendMessage("command.only-console");
            return true;
        }

        switch (args.length) {
            default:
                CoreLogger.log("command.usage", new String[] {"$usage"}, "/rank <player> <rank>");
            case 0:
                break;
            case 2:
                String rankName = args[1].toUpperCase(Locale.ENGLISH);

                try {
                    Rank rank = CoreRank.byName(rankName);

                    String targetName = args[0];

                    CorePlayer.get(targetName, target -> {
                        if (target == null) {
                            CoreLogger.log("Player %s wasn't found", targetName);
                        } else {
                            target.setRank(rank, true);
                            CoreLogger.log("%s's rank has been set to %s", targetName, rankName);
                        }
                    });

                    return true;
                } catch (IllegalArgumentException e) {
                    CoreLogger.log("%s is not a valid rank", rankName);
                    break;
                }
        }

        CoreLogger.log("Available ranks: " + StringUtil.getStringFromStringList(
                CoreRank.values()
                        .stream()
                        .map(Rank::getName)
                        .collect(Collectors.toList())));

        return true;
    }
}