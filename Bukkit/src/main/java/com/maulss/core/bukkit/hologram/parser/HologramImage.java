/*
 * Part of core.
 * 
 * Created on 25 July 2017 at 6:25 PM.
 */

package com.maulss.core.bukkit.hologram.parser;

import com.maulss.core.bukkit.hologram.line.HologramLine;
import com.maulss.core.bukkit.hologram.line.TextualLine;
import com.maulss.core.bukkit.imgtext.ImageText;
import com.maulss.core.collect.EnhancedList;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class HologramImage implements HologramComponent, Iterable<HologramLine> {

    private final ImageText imgText;
    private final List<HologramLine> lines;

    public HologramImage(final ImageText imgText) {
        this.imgText = Validate.notNull(imgText, "imgText");

        String[] lines = imgText.getLines();
        this.lines = new EnhancedList<>(lines.length);
        for (String line : lines) {
            this.lines.add(new TextualLine(line));
        }
    }

    public ImageText getImageText() {
        return imgText;
    }

    @Override
    public Collection<HologramLine> parse() {
        return lines;
    }

    @Override
    public Iterator<HologramLine> iterator() {
        return lines.iterator();
    }
}