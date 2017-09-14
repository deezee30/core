/*
 * Part of core.
 * Made on 02/09/2017
 */

package com.maulss.core.bukkit.player.statistic;

import com.maulss.core.bukkit.player.profile.Profile;
import com.maulss.core.bukkit.player.event.PremiumStatusChangeEvent;

/**
 * Represents any user that can contain the "premium" attribute
 * whether he is online or not.
 *
 * @see Profile
 */
public interface PremiumHolder extends Profile {

    /**
     * @return  {@code true} if the holder is a premium player,
     *          {@code false} if otherwise.
     */
    boolean isPremium();

    /**
     * Calls a new {@link PremiumStatusChangeEvent} event.
     *
     * If a player's premium status is the same as the one provided, nothing
     * will happen and the event will not be called.
     *
     * After it is processed, if the {@code event} has not been
     * cancelled, the premium state is changed and the database
     * is updated with the new value.
     *
     * @param   premium
     *          The new premium value.
     * @see     PremiumStatusChangeEvent
     */
    void setPremium(final boolean premium);
}