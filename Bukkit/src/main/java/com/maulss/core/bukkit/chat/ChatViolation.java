/*
 * Part of core.
 * 
 * Created on 12 June 2017 at 5:27 PM.
 */

package com.maulss.core.bukkit.chat;

import com.maulss.core.bukkit.internal.config.MainConfig;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.CorePlayerViolation;

import java.util.concurrent.TimeUnit;

public class ChatViolation extends CorePlayerViolation {

    public ChatViolation(final CorePlayer player) {
        super(player, MainConfig.getChatSpamViolationsPermitted());

        // set up violation cooldown
        int cooldownSecs = MainConfig.getChatViolationCooldown();
        if (cooldownSecs > 0) enableCooldown(cooldownSecs, TimeUnit.SECONDS);
    }

    @Override
    public final void onMaxViolations(final CorePlayer player) {
        player.setMuted(true);
        player.sendMessage("chat.mute.muted");
    }

    @Override
    public void onViolation(final CorePlayer corePlayer) {
        // check if player has 1 more available violation
        if (getRemainingViolations() == 1)
            corePlayer.sendMessage("chat.mute.last-reminder");
    }
}