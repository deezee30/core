/*
 * Part of core.
 * 
 * Created on 21 July 2017 at 2:42 PM.
 */

package com.maulss.core.bukkit.hologram;

import com.maulss.core.bukkit.hologram.line.HologramLine;
import com.maulss.core.bukkit.hologram.line.NullHologramLine;
import com.maulss.core.bukkit.world.Position;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.math.Vector3D;

import java.util.Optional;

import static org.apache.commons.lang3.Validate.notNull;

public class StaticHologram extends Hologram {

    private static final long serialVersionUID = 715515239385559351L;

    protected final EnhancedList<HologramLine> lines;

    StaticHologram(final Position position,
                   final EnhancedList<HologramLine> lines,
                   final Optional<Double> radius,
                   final Optional<EnhancedList<HologramViewer>> visibleFor) {
        super(position, radius, visibleFor);
        this.lines = lines;
    }

    @Override
    public EnhancedList<HologramLine> getLines() {
        return lines;
    }

    @Override
    public void remove(final int index) {
        remove(lines.get(index));
    }

    @Override
    public void remove(final HologramLine line) {
        replace(line, new NullHologramLine());
    }

    @Override
    public void add(final int index,
                    final HologramLine line) {
        if (index != lines.size() || !isNull(line)) {
            lines.add(line);
            destroy();
            activate();
        }
    }

    @Override
    public void replace(final HologramLine previous,
                        final HologramLine newLine) {
        notNull(previous, "previous");
        notNull(newLine, "newLine");
        int index = lines.indexOf(previous);
        if (index == -1)
            throw new IllegalArgumentException("Hologram doesn't contain provided previous line");
        lines.remove(index);

        /*
         * If the new line is just a placeholder, ie: getTest() returns null,
         * AND the index is the last element in the collection, do not add
         * the new placeholder
         */
        if (index != lines.size() || !isNull(newLine)) {
            lines.add(newLine);
        }
    }

    private boolean isNull(final HologramLine line) {
        return line instanceof NullHologramLine || line.getText() == null;
    }
}