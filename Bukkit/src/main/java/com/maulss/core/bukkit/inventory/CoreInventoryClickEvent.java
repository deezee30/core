/*
 * Part of core.
 */

package com.maulss.core.bukkit.inventory;

import com.maulss.core.bukkit.player.CorePlayer;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface CoreInventoryClickEvent {

    boolean handleInventory(final CorePlayer player,
                            final ItemStack clickedItem,
                            final int slot);
}