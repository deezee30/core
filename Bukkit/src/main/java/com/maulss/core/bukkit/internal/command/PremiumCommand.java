package com.maulss.core.bukkit.internal.command;

import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.bukkit.player.CorePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static com.maulss.core.bukkit.player.CorePlayer.PLAYER_MANAGER;

public final class PremiumCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String useless,
                             String[] args) {

        // This command is only executable via console
        if (!(sender instanceof ConsoleCommandSender)) {
            if (sender instanceof Player) {
                PLAYER_MANAGER.get(sender.getName()).sendMessage("command.only-console");
            }
            return true;
        }

        switch (args.length) {
            case 1: {
                String targetName = args[0];

                CorePlayer.get(targetName, target -> {
                    if (target == null) {
                        CoreLogger.log("player.error.not-found", new String[] {"$player"}, targetName);
                        return;
                    }

                    CoreLogger.log(
                            "premium.notify",
                            new String[] {"$user" , "$premium"},
                            targetName,
                            target.isPremium()
                    );
                });

                break;
            } case 2: {
                String trueFalse = args[1];
                if (trueFalse.equalsIgnoreCase("true") || trueFalse.equalsIgnoreCase("false")) {
                    String targetName = args[0];

                    CorePlayer.get(targetName, target -> {
                        if (target == null) {
                            CoreLogger.log("player.error.not-found", new String[] {"$player"}, targetName);
                            return;
                        }

                        if (Boolean.parseBoolean(trueFalse)) {
                            if (target.isPremium()) {
                                CoreLogger.log("premium.already-true", new String[] {"$user"}, targetName);
                            } else {
                                target.setPremium(true);
                                CoreLogger.log("premium.promoted", new String[] {"$user"}, targetName);
                            }
                        } else {
                            if (target.isPremium()) {
                                target.setPremium(false);
                                CoreLogger.log("premium.demoted", new String[] {"$user"}, targetName);
                            } else {
                                CoreLogger.log("premium.already-false", new String[] {"$user"}, targetName);
                            }
                        }
                    });

                    break;
                } else {
                    CoreLogger.log(
                            "command.usage",
                            new String[] {"$usage"},
                            "/premium <playername> <true|false>"
                    );

                    break;
                }
            } default:
                CoreLogger.log(
                        "command.usage",
                        new String[] {"$usage"},
                        "/premium <playername> (<true|false>)"
                );

                break;
        }

        return true;
    }
}