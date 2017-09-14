/*
 * Part of core.
 * 
 * Created on 23 July 2017 at 9:37 PM.
 */

package com.maulss.core.bukkit.hologram.line;

public class NullHologramLine extends HologramLine {

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void setText(final String text) {
        // do nothing
    }
}