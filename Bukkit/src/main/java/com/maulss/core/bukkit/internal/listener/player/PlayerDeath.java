/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.internal.AutoRespawnTask;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.event.CorePlayerDeathByPlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

final class PlayerDeath implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);

        if (e.getEntity().getKiller() == null) return;

        CorePlayerDeathByPlayerEvent event = new CorePlayerDeathByPlayerEvent(e);
        Bukkit.getPluginManager().callEvent(event);

        if (event.doClearDrops()) {
            e.getDrops().clear();
        }

        if (event.doClearExp()) {
            e.setDroppedExp(0);
        }

        CorePlayer victim = event.getPlayer();
        Player bukVictim = victim.getPlayer();

        CorePlayer killer = event.getKiller();
        Player bukKiller = killer.getPlayer();

        if (event.doSendKillerMessage()) {
            killer.sendMessage(
                    "player.kill",
                    new String[] {"$victim"},
                    victim.getDisplayName()
            );
        }

        if (event.doSendVictimMessage()) {
            victim.sendMessage(
                    "player.death",
                    new String[] {"$killer" , "$hearts"},
                    killer.getDisplayName(),
                    (int) bukKiller.getHealth() / 2
            );
        }

        if (event.doAutoRespawn()) {
            new AutoRespawnTask(bukVictim).runTaskLater(Core.get(), 0L);
        }
    }
}