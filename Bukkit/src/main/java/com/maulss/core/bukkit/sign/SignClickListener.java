/*
 * Part of core.
 * 
 * Created on 10 July 2017 at 2:42 PM.
 */

package com.maulss.core.bukkit.sign;

import com.maulss.core.bukkit.player.CorePlayer;
import org.bukkit.block.Sign;

public interface SignClickListener {

    void onClick(final CorePlayer player,
                 final Sign sign);
}