/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

final class PlayerDropItem implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        String locale = CorePlayer.createIfAbsent(event.getPlayer()).getLocale();
        ItemStack stack = event.getItemDrop().getItemStack();

        Core.getSettings().getLoginItems().keySet().stream()
                .filter(item -> stack.equals(item.buildWithLocaleSupport(locale)))
                .forEach(item -> event.setCancelled(true));
    }
}