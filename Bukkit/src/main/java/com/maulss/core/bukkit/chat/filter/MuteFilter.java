/*
 * Part of core.
 * 
 * Created on 11 June 2017 at 9:29 PM.
 */

package com.maulss.core.bukkit.chat.filter;

import com.maulss.core.bukkit.player.CorePlayer;

import java.util.Optional;

class MuteFilter implements ChatBlockFilter {

    @Override
    public boolean block(final CorePlayer player,
                         final String message) {
        return player.isMuted();
    }

    @Override
    public Optional<String> getReason() {
        return Optional.of("chat.mute.reminder");
    }

    @Override
    public boolean violate() {
        return false;
    }
}