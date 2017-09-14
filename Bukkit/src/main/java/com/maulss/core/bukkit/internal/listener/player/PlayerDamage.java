/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.event.CorePlayerDamageEntityEvent;
import com.maulss.core.bukkit.player.event.CorePlayerDamagePlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

final class PlayerDamage implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            CorePlayer player = CorePlayer.PLAYER_MANAGER.get(event);
            event.setCancelled(player != null && !player.isDamageable());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        if (!event.isCancelled() && damaged instanceof Damageable) {
            Entity damager = event.getDamager();
            if (damager instanceof Player) {
                Bukkit.getPluginManager().callEvent(new CorePlayerDamageEntityEvent(event));
                if (damaged instanceof Player) {
                    Bukkit.getPluginManager().callEvent(new CorePlayerDamagePlayerEvent(event));
                }
            }
        }
    }
}