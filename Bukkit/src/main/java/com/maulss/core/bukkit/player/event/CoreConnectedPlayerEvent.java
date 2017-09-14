/*
 * Part of core.
 * Made on 09/09/2017
 */

package com.maulss.core.bukkit.player.event;

import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import org.bukkit.event.Cancellable;

public class CoreConnectedPlayerEvent extends CorePlayerEvent implements Cancellable {

    public static final CorePlayerManager MANAGER = CorePlayerManager.getInstance();

    private boolean cancel = false;

    protected CoreConnectedPlayerEvent(CorePlayer profile) {
        super(profile);
    }

    public CorePlayer getPlayer() {
        return (CorePlayer) getProfile();
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}