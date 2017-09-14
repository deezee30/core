/*
 * Part of core.
 * 
 * Created on 24 July 2017 at 1:58 PM.
 */

package com.maulss.core.bukkit.hologram.line;

public final class OpaqueTextualLine extends TextualLine {

    public OpaqueTextualLine(final String text) {
        super(text);
    }

    @Override
    public float getOpacity() {
        return 1f;
    }
}