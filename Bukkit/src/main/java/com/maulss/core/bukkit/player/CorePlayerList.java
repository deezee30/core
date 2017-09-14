/*
 * Part of core.
 */

package com.maulss.core.bukkit.player;

import com.maulss.core.collect.EnhancedList;

import java.util.Collection;

/**
 * Delegate for {@code EnhancedList<CorePlayer>}
 *
 * @see	EnhancedList
 * @see CorePlayer
 */
public class CorePlayerList extends EnhancedList<CorePlayer> {

    public CorePlayerList() {}

    public CorePlayerList(int initialCapacity) {
        super(initialCapacity);
    }

    public CorePlayerList(CorePlayer... elements) {
        super(elements);
    }

    public CorePlayerList(Collection<CorePlayer> c) {
        super(c);
    }
}