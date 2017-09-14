/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

final class PlayerFoodChange implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!CorePlayerManager.getInstance().get(event).canGetHungry()) {
            event.setCancelled(true);
        }
    }
}