/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.player.profile.CoreProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Optional;

final class PlayerLogin implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        CoreProfile.PLAYER_MANAGER.add(
                event.getPlayer(),
                Optional.ofNullable(event.getAddress().getHostAddress())
        );
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }
}