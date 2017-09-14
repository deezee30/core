/*
 * Part of core.
 * Made on 02/09/2017
 */

package com.maulss.core.bukkit.player.statistic;

import com.maulss.core.bukkit.Rank;
import com.maulss.core.bukkit.player.profile.Profile;

/**
 * Represents any profile that can hold and modify a {@link Rank}.
 * @see Rank
 */
public interface RankHolder extends Profile {

    /**
     * @return the holder's current cached rank
     */
    Rank getRank();

    /**
     * Sets the holder's {@link Rank} to the specified parameter
     * in cache and also updates the database.
     *
     * @param   rank the rank to update to
     * @see     Rank
     */
    void setRank(final Rank rank);

    /**
     * Returns if the rank holder is allowed to perform
     * a task that requires to be of the provided
     * <b>or higher.</b>
     *
     * @param   rank the rank to check
     * @return  if the rank provided is greater than or
     *          equal to the holder's rank
     * @see     Rank
     */
    default boolean isAllowedFor(final Rank rank) {
        return getRank().compareTo(rank) >= 0;
    }
}