/*
 * Part of core.
 */

package com.maulss.core.bukkit.hologram.parser;

import com.maulss.core.bukkit.hologram.line.HologramLine;
import com.maulss.core.collect.EnhancedList;

import java.util.Collection;

public interface HologramComponent {

    Collection<HologramLine> parse();

    static EnhancedList<HologramLine> parse(final Collection<HologramComponent> parsers) {
        EnhancedList<HologramLine> list = new EnhancedList<>();
        for (HologramComponent parser : parsers) {
            list.addAll(parser.parse());
        }

        return list;
    }
}