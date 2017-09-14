/*
 * Part of core.
 * Made on 02/09/2017
 */

package com.maulss.core.bukkit.player.statistic;

import com.maulss.core.bukkit.player.profile.Profile;
import com.maulss.core.bukkit.player.event.TokenValueChangeEvent;
import com.maulss.core.bukkit.player.event.TokenValueChangeException;
import com.maulss.core.database.Value;

/**
 * Represents any user that is of value in terms of token amount.
 */
public interface TokensHolder extends Profile {

    /**
     * @return The amount of tokens the profile currently has.
     */
    int getTokens();

    /**
     * Calls a new {@link TokenValueChangeEvent} event.
     *
     * After it is processed, if the {@code event} has not been
     * cancelled, coins are updated (via {@link Value#appendTo(int)})
     * and the database collection is updated with the new value.
     *
     * @param   value
     *          The amount of tokens being changed along with the
     *          change type, {@code GIVE}, {@code SET} or {@code TAKE}.
     * @throws TokenValueChangeException
     *          If the amount provided ({@param value}) is a
     *          negative.
     * @throws TokenValueChangeException
     *          If subtracting the provided amount from the
     *          current token amount will result in a
     *          negative.
     * @see     TokenValueChangeEvent
     * @see     Value
     */
    void setTokens(final Value<Integer> value) throws TokenValueChangeException;
}