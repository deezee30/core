/*
 * Part of core.
 * 
 * Created on 25 July 2017 at 6:25 PM.
 */

package com.maulss.core.bukkit.hologram.parser;

import com.maulss.core.bukkit.hologram.line.HologramLine;
import com.maulss.core.bukkit.hologram.line.UpdatingTextualLine;
import com.maulss.core.bukkit.imgtext.AnimatedMessage;
import com.maulss.core.bukkit.imgtext.ImageText;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.util.ArrayWrapper;
import org.apache.commons.lang3.Validate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class HologramGif implements HologramComponent, Iterable<HologramLine> {

    private final EnhancedList<HologramLine> lines;

    public HologramGif(final File gif,
                       final int height,
                       final int updateSpeedTicks) {
        Validate.notNull(gif, "gif");
        this.lines = new EnhancedList<>(height);

        List<BufferedImage> imgs = AnimatedMessage.getFrames(gif);
        for (int y = 0; y < height; y++) {
            String[] updates = new String[imgs.size()];
            for (int x = 0; x < imgs.size(); x++) {
                updates[x] = new ImageText(imgs.get(x), height).getLines()[y];
            }

            this.lines.add(new UpdatingTextualLine(updates[0], updateSpeedTicks) {
                int z = 0;

                @Override
                public String onUpdate() {
                    if (z == updates.length)
                        z = 0;
                    return updates[z++];
                }
            });
        }
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