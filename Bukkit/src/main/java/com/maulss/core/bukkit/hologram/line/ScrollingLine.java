/*
 * Part of core.
 * 
 * Created on 26 July 2017 at 8:33 PM.
 */

package com.maulss.core.bukkit.hologram.line;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.CorePlugin;
import com.maulss.core.text.Scroller;
import com.maulss.core.text.SimpleScroller;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;

public class ScrollingLine extends HologramLine {

    private final Scroller scroller;
    private String text;

    public ScrollingLine(String text,
                         int maxWidth,
                         int spaceBetween,
                         int scrollSpeedTicks) {
        this.text = text;
        this.scroller = new SimpleScroller(text, maxWidth, spaceBetween);

        if (scrollSpeedTicks > 0) {
            Bukkit.getScheduler().runTaskTimer(
                    Core.get(),
                    this :: next,
                    scrollSpeedTicks,
                    scrollSpeedTicks
            );
        }
    }

    public void next() {
        update(scroller.next());
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(final String text) {
        this.text = Validate.notNull(text, "text");
    }
}