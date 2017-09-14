/*
 * Part of core.
 * 
 * Created on 11 June 2017 at 9:29 PM.
 */

package com.maulss.core.bukkit.chat.filter;

import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.text.StringUtil;

import java.util.Optional;

class AdvertisementFilter implements ChatBlockFilter {

    @Override
    public boolean block(final CorePlayer player,
                         final String message) {
        return !player.isHelper()
                && (StringUtil.containsAddress(message)
                || StringUtil.containsInetAddress(message));
    }

    @Override
    public Optional<String> getReason() {
        return Optional.of("chat.mute.no-ads");
    }

    @Override
    public boolean violate() {
        return true;
    }
}