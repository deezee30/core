/*
 * Part of core.*
 * Created on 10 July 2017 at 3:01 PM.
 */

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.sign.SignHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

final class PlayerInteract implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Action action = event.getAction();

        // check for sign listeners
        if (block == null || (block.getType() == Material.AIR)) return;
        if (SignHandler.isSign(block)
                && (action.equals(Action.LEFT_CLICK_BLOCK)
                || action.equals(Action.RIGHT_CLICK_BLOCK))) {
            SignHandler.handleSignClick(block, event.getPlayer(), action);
        }
    }
}