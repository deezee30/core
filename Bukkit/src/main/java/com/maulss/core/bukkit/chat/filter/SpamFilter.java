/*
 * Part of core.
 * 
 * Created on 11 June 2017 at 9:29 PM.
 */

package com.maulss.core.bukkit.chat.filter;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.internal.config.MainConfig;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.collect.EnhancedMap;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

class SpamFilter implements ChatBlockFilter {

    private final EnhancedMap<String, Integer> count = new EnhancedMap<>();

    @Override
    public boolean block(final CorePlayer player,
                         final String message) {
        if (player.isHelper()) return false;

        boolean violation = false;

        // check for spam
        String name = player.getName();
        if (count.containsKey(name)) {
            int c = count.get(player.getName());
            if (c > MainConfig.getMaxMessages()) {
                count.remove(player.getName());
                violation = true;
            } else {
                putTemp(name, c + 1);
            }
        } else {
            putTemp(name, 1);
        }

        return violation;
    }

    @Override
    public Optional<String> getReason() {
        return Optional.of("chat.mute.no-spam");
    }

    @Override
    public boolean violate() {
        return true;
    }

    private void putTemp(final String name,
                         final int messages) {
        count.put(name, messages);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (count.containsKey(name)) {
                    int c = count.get(name);
                    if (c == 1) count.remove(name);
                    else count.put(name, c - 1);
                }
            }
        }.runTaskLater(Core.get(), 20 * 20);
    }
}