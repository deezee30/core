/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.listener.player;

import org.bukkit.event.Listener;

public final class PlayerListeners {

    // Disable initialization
    private PlayerListeners() {}

    public static synchronized Listener[] get() {
        return new Listener[] {
                new PlayerChat(),
                new PlayerDamage(),
                new PlayerDeath(),
                new PlayerDropItem(),
                new PlayerFoodChange(),
                new PlayerInteract(),
                new PlayerInventoryClick(),
                new PlayerLogin(),
                new PlayerLoginFullAllow(),
                new PlayerLoginFullCheck(),
                new PlayerMove(),
                new PlayerQuit(),
                new PlayerRespawn()
        };
    }
}