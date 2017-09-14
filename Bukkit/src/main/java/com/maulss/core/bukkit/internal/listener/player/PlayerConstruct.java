/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.sign.SignHandler;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

final class PlayerConstruct implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        CorePlayer player = CorePlayer.createIfAbsent(event.getPlayer());
        if (!player.isConstructable()) {
            event.setCancelled(true);
        } else {
            // add support for color coding signs if premium or mod or higher
            Block block = event.getBlock();
            if (SignHandler.isSign(block) && player.isPremium()) {
                Sign sign = (Sign) block;
                String[] lines = sign.getLines();
                for (int x = 0; x < lines.length; x++) {
                    sign.setLine(x, ChatColor.translateAlternateColorCodes('&', lines[x]));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!CorePlayer.createIfAbsent(event.getPlayer()).isConstructable()) {
            event.setCancelled(true);
        }
    }
}