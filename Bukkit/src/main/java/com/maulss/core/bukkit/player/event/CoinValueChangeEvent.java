/*
 * Part of core.
 */

package com.maulss.core.bukkit.player.event;

import com.maulss.core.bukkit.player.statistic.CoinsHolder;
import com.maulss.core.database.Value;
import com.maulss.core.database.ValueType;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Cancellable;

/**
 * Called prior to modifying the player's coin amount.
 *
 * <p>Cancelling the event will not modify the coins.</p>
 *
 * <p>If the player's new coin amount is less than 0 or
 * the amount provided is a negative, an exception is thrown.</p>
 */
public class CoinValueChangeEvent extends CorePlayerEvent implements Cancellable {

    private final   int         oldCoins;
    private final   int         changed;
    private final   ValueType   type;
    private         boolean     cancelled = false;

    /**
     * Constructs a new {@code CoinValueModificationEvent}.
     *
     * <p>In case the amount provided or the supposedly new
     * coin amount is not applicable, an exception is thrown.</p>
     *
     * @param   player
     *          The user that can hold a coin amount.
     * @param   value
     *          The amount of coins being changed along
     *          with the change type, {@code GIVE}, {@code SET}
     *          or {@code TAKE}.
     * @throws CoinValueChangeException
     *          If the amount provided ({@param value}) is a
     *          negative.
     * @throws CoinValueChangeException
     *          If subtracting the provided amount from the
     *          current coin amount will result in a negative.
     * @see     Value
     * @see     ValueType
     * @see     CoinsHolder
     */
    public CoinValueChangeEvent(final CoinsHolder player,
                                Value value) throws CoinValueChangeException {
        super(player);

        value = Validate.notNull(value).clone();
        Validate.isTrue(value.isInteger());

        this.oldCoins = player.getCoins();
        this.type = value.getType();
        int changed = Integer.parseInt(value.toString());

        // Make sure coins is 0 or bigger
        if (changed <= 0) {
            cancelled = true;
            throw new CoinValueChangeException("Cannot add coins: The amount provided is a negative");
        }

        // If coins are being taken away, make sure the new result is not 0
        if (type.equals(ValueType.TAKE) && oldCoins - changed < 0) {
            cancelled = true;
            throw new CoinValueChangeException("Subtracting %s coins from %s will result in a negative balance", changed, player.getName());
        }

        this.changed = changed;
    }

    /**
     * @return The user that can hold the coins.
     */
    public CoinsHolder getPlayer() {
        return (CoinsHolder) getProfile();
    }

    /**
     * @return The new amount of coins the user has after the change.
     */
    public int getNewCoins() {
        return new Value<>(oldCoins, type).appendTo(changed);
    }

    public int getOldCoins() {
        return oldCoins;
    }

    public int getChanged() {
        return changed;
    }

    public ValueType getType() {
        return type;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
