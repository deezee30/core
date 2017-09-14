/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.event.CorePlayerPostLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

final class PlayerLoginFullCheck implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(CorePlayerPostLoadEvent event) {
        Bukkit.getScheduler().runTaskLater(Core.get(), () -> {
            final CorePlayer player = event.getPlayer();
            if (Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size() - 1 && !(player.isPremium() || player.isMod())) {
                player.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&',
                        Core.getSettings().get(player.getLocale(), "player.error.server-full")
                ));
                player.destroy();
            }
        }, 4L);
    }
}