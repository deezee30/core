/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

final class PlayerQuit implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        CorePlayerManager.getInstance().get(event).destroy();
    }
}