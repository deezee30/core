/*
 * Part of core.
 * 
 * Created on 23 July 2017 at 10:02 PM.
 */

package com.maulss.core.bukkit.hologram;

import com.maulss.core.bukkit.hologram.line.HologramLine;
import com.maulss.core.bukkit.world.Position;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.math.Vector3D;

import java.util.Optional;

public class MountedHologram extends Hologram {

    MountedHologram(Position position, Optional<Double> radius, Optional<EnhancedList<HologramViewer>> visibleFor) {
        super(position, radius, visibleFor);
        throw new UnsupportedOperationException();
    }

    @Override
    public EnhancedList<HologramLine> getLines() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int line) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(HologramLine line) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, HologramLine line) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replace(HologramLine previous, HologramLine newLine) {
        throw new UnsupportedOperationException();
    }
}