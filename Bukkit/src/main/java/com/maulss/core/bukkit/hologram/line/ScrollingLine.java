/*
 * Part of core.
 * 
 * Created on 26 July 2017 at 8:33 PM.
 */

package com.maulss.core.bukkit.hologram.line;

import com.maulss.core.text.Scroller;
import com.maulss.core.text.SimpleScroller;

public class ScrollingLine extends UpdatingTextualLine {

    private final Scroller scroller;

    public ScrollingLine(final String text,
                         final int maxWidth,
                         final int spaceBetween,
                         final int scrollSpeedTicks) {
        super(text, scrollSpeedTicks);
        // TODO: Use a color-code supported scroller instead of a simple scroller
        this.scroller = new SimpleScroller(text, maxWidth, spaceBetween);
    }

    @Override
    public String onUpdate() {
        return scroller.next();
    }
}