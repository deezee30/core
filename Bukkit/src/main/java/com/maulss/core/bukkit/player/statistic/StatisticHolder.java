/*
 * Part of core.
 */

package com.maulss.core.bukkit.player.statistic;

import com.google.common.collect.ImmutableList;

/**
 * Used for online and offline users that can be recorded
 * in the form of statistics.
 */
@FunctionalInterface
public interface StatisticHolder {

    /**
     * The lines used for presenting the statistical values for
     * this player instance.
     *
     * Used by {@link com.maulss.core.bukkit.player.CorePlayer}
     * and its subclasses to set the values.
     *
     * @return  The custom statistic lines stored in an unmodifiable {@code List}
     * @see     com.maulss.core.bukkit.player.CorePlayer#getStatisticValues()
     */
    ImmutableList<String> getStatisticValues();
}