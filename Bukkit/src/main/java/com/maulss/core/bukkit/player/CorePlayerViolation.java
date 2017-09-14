/*
 * Part of core.
 * 
 * Created on 12 June 2017 at 5:34 PM.
 */

package com.maulss.core.bukkit.player;

import com.maulss.core.bukkit.Violation;

public abstract class CorePlayerViolation extends Violation<CorePlayer> {

    protected CorePlayerViolation(final CorePlayer player,
                                  final int toleration) {
        super(player, toleration);
    }

    public final CorePlayer getPlayer() {
        return getTarget();
    }
}