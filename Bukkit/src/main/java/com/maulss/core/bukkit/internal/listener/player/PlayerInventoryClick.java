/*
 * Part of core.*/

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.CoreSettings;
import com.maulss.core.bukkit.inventory.CoreInventoryClickEvent;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

final class PlayerInventoryClick implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack is = event.getCurrentItem();

        if (is == null || is.getType().equals(Material.AIR)) {
            return;
        }

        Player bPlayer = (Player) event.getWhoClicked();
        CorePlayer player = CorePlayerManager.getInstance().get(bPlayer.getName());
        String locale = player.getLocale();

        CoreSettings settings = Core.getSettings();

        for (Map.Entry<String, CoreInventoryClickEvent> entry : settings.getRegisteredInventories().entrySet()) {
            if (event.getInventory().getName().equals(settings.get(locale, entry.getKey()))) {
                event.setCancelled(true);
                if (entry.getValue().handleInventory(player, is, event.getSlot())) {
                    bPlayer.closeInventory();
                }

                return;
            }
        }
    }
}