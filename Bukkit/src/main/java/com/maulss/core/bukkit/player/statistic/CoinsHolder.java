/*
 * Part of core.
 * Made on 02/09/2017
 */

package com.maulss.core.bukkit.player.statistic;

import com.maulss.core.bukkit.player.profile.Profile;
import com.maulss.core.bukkit.player.event.CoinValueChangeEvent;
import com.maulss.core.bukkit.player.event.CoinValueChangeException;
import com.maulss.core.database.Value;

/**
 * Represents any user that is of value in terms of coin amount.
 */
public interface CoinsHolder extends Profile {

    /**
     * @return The amount of coins the profile currently has.
     */
    int getCoins();

    /**
     * Calls a new {@link CoinValueChangeEvent} event.
     *
     * After it is processed, if the {@code event} has not been
     * cancelled, coins are updated (via {@link Value#appendTo(int)})
     * and the database collection is updated with the new value.
     *
     * By default the {@link #getCoinMultiplier()} is not applied.
     * If it needs to be, call {@code setCoins(value, true)}.
     *
     * @param  value
     *          The amount of coins being changed along with the
     *          change type, {@code GIVE}, {@code SET} or {@code TAKE}.
     * @throws CoinValueChangeException
     *          If the amount provided ({@param value}) is a negative.
     * @throws CoinValueChangeException
     *          If subtracting the provided amount from the
     *          current coin amount will result in a
     *          negative.
     * @see     CoinValueChangeEvent
     * @see     Value
     * @see     #setCoins(Value, boolean)
     * @see     #getCoinMultiplier()
     */
    default void setCoins(final Value<Integer> value) throws CoinValueChangeException {
        setCoins(value, false);
    }

    /**
     * Calls a new {@link CoinValueChangeEvent} event.
     *
     * After it is processed, if the {@code event} has not been
     * cancelled, coins are updated (via {@link Value#appendTo(int)})
     * and the database collection is updated with the new value.
     *
     * @param   value
     *          The amount of coins being changed along with the
     *          change type, {@code GIVE}, {@code SET} or {@code TAKE}.
     * @param   applyMultiplier
     *          If enabled and the value type is {@code GIVE},
     *          {@link #getCoinMultiplier()} is applied.
     * @throws CoinValueChangeException
     *          If the amount provided ({@param value}) is a
     *          negative.
     * @throws CoinValueChangeException
     *          If subtracting the provided amount from the
     *          current coin amount will result in a
     *          negative.
     * @see     CoinValueChangeEvent
     * @see     Value
     * @see     #getCoinMultiplier()
     */
    void setCoins(Value<Integer> value,
                  final boolean applyMultiplier) throws CoinValueChangeException;

    /**
     * A coin multiplier value for special events or personal perks.
     *
     * <b>Default is 1.0</b>
     *
     * Applied when adding coins to the current balance. Only values
     * higher than 0.0 are possible. In case the resulting coin
     * count is not an integer, it is rounded to the lowest whole
     * number.
     *
     * @return Coin multiplier for specific {@code CoinsHolder}.
     */
    float getCoinMultiplier();

    /**
     * A coin multiplier value for special events or personal perks.
     *
     * <b>Default is 1.0</b>
     *
     * Applied when adding coins to the current balance. Only values
     * higher than 0.0 are possible. In case the resulting coin
     * count is not an integer, it is rounded to the lowest whole
     * number.
     *
     * @param factor The factor by which the base coin is multiplied.
     */
    void setCoinMultiplier(final float factor);
}