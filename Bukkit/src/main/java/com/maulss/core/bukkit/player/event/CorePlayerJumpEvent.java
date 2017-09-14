/*
 * Part of core.
 * 
 * Created on 04 June 2017 at 2:42 PM.
 */

package com.maulss.core.bukkit.player.event;

import com.maulss.core.bukkit.player.CorePlayer;
import org.bukkit.entity.Player;

public class CorePlayerJumpEvent extends CoreConnectedPlayerEvent {

    public CorePlayerJumpEvent(final Player player) {
        super(CorePlayer.createIfAbsent(player));
    }
}