/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

final class PlayerMove implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) return;

        CorePlayer player = CorePlayer.PLAYER_MANAGER.get(event);
        if (player == null || player.isMovable()) return;

        player.getPlayer().teleport(event.getFrom());
    }
}