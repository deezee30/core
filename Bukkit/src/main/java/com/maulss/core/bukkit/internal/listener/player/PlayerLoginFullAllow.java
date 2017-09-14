/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

final class PlayerLoginFullAllow implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            event.allow();
        }
    }
}