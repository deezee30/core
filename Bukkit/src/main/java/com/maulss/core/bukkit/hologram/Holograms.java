/*
 * Part of core.
 * 
 * Created on 22 July 2017 at 2:03 PM.
 */

package com.maulss.core.bukkit.hologram;

import com.google.common.collect.ImmutableList;
import com.maulss.core.collect.EnhancedList;
import org.apache.commons.lang3.Validate;

public final class Holograms {

    private static Holograms instance;
    private final EnhancedList<Hologram> holograms = new EnhancedList<>();

    private Holograms() {}

    public Hologram add(final Hologram hologram) {
        Validate.notNull(hologram);
        holograms.add(hologram);
        return hologram;
    }

    public ImmutableList<Hologram> getHolograms() {
        return holograms.getImmutableElements();
    }

    public Holograms getInstance() {
        return instance == null
                ? instance = new Holograms()
                : instance;
    }
}