package com.maulss.core.bukkit.internal.command;

import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.CorePlayerSender;
import com.maulss.core.bukkit.player.event.CoinValueChangeException;
import com.maulss.core.database.Value;
import com.maulss.core.database.ValueType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Locale;

public final class CoinsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        final CorePlayerSender senderPlayer = new CorePlayerSender(sender);
        final boolean isPlayer = senderPlayer.isPlayer();

        switch (args.length) {
            case 0:
                if (!isPlayer) {
                    CoreLogger.log(
                            "command.usage",
                            new String[] {"$usage"},
                            "/coins <name> (<give|take|set> <amount>)"
                    );

                    return true;
                }

                senderPlayer.sendMessage("border");
                senderPlayer.sendMessage(
                        "coins.notify-self",
                        new String[] {"$coins"},
                        senderPlayer.getPlayer().getCoins()
                );
                senderPlayer.sendMessage("border");

                break;
            case 1:
                String targetName = args[0];

                CorePlayer.get(targetName, target -> {
                    if (target == null) {
                        senderPlayer.sendMessage(
                                "player.error.not-found",
                                new String[] {"$player"},
                                targetName
                        );
                    } else {
                        int coins = target.getCoins();

                        senderPlayer.sendMessage("border");
                        senderPlayer.sendMessage(
                                "coins.notify-other",
                                new String[] {"$player", "$coins"},
                                target.getDisplayName(),
                                coins
                        );
                        senderPlayer.sendMessage("border");
                    }
                });

                break;
            case 3:
                if (isPlayer && !senderPlayer.getPlayer().isAdmin()) {
                    senderPlayer.sendMessage("command.not-found");
                    break;
                }

                targetName = args[0];
                String giveSetTake = args[1].toLowerCase(Locale.ENGLISH);
                int amount = -1;

                if (!Arrays.asList("give", "set", "take").contains(giveSetTake.toLowerCase())) {
                    senderPlayer.sendMessage("command.not-found");
                    break;
                }

                try {
                    amount = Math.abs(Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    senderPlayer.sendMessage("command.invalid-value");
                }

                if (amount == -1) return true;

                Value<Integer> value = new Value<>(amount, ValueType.valueOf(giveSetTake.toUpperCase(Locale.ENGLISH)));

                CorePlayer.get(targetName, target -> {
                    if (target == null) {
                        senderPlayer.sendMessage(
                                "player.error.not-found",
                                new String[] {"$player"},
                                targetName
                        );
                    } else {
                        try {
                            target.setCoins(value);

                            senderPlayer.sendMessage(
                                    "coins." + value.getType().toString().toLowerCase(),
                                    new String[] {"$coins", "$player"},
                                    value.getValue(),
                                    targetName
                            );
                        } catch (CoinValueChangeException e) {
                            senderPlayer.sendMessage("Error: " + e.getMessage());
                        }
                    }
                });

                break;
            default:
                senderPlayer.sendMessage("command.not-found");
                break;
        }

        return true;
    }
}