/*
 * Part of core.
 */

package com.maulss.core.bukkit.player.event;

import com.maulss.core.bukkit.player.statistic.TokensHolder;
import com.maulss.core.database.Value;
import com.maulss.core.database.ValueType;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Cancellable;

/**
 * Called prior to modifying the player's token amount.
 *
 * <p>Cancelling the event will not modify the tokens.</p>
 *
 * <p>If the player's new token amount is less than 0 or
 * the amount provided is a negative, an exception is thrown.</p>
 */
public class TokenValueChangeEvent extends CorePlayerEvent implements Cancellable {

    private final   int         oldTokens;
    private final   int         changed;
    private final   ValueType   type;
    private         boolean     cancelled = false;

    /**
     * Constructs a new {@code TokenValueModificationEvent}.
     *
     * <p>In case the amount provided or the supposedly new
     * token amount is not applicable, an exception is thrown.</p>
     *
     * @param   player
     *          The user that can hold a token amount.
     * @param   value
     *          The amount of tokens being changed along
     *          with the change type, {@code GIVE}, {@code SET}
     *          or {@code TAKE}.
     * @throws TokenValueChangeException
     *          If the amount provided ({@param value}) is a
     *          negative.
     * @throws TokenValueChangeException
     *          If subtracting the provided amount from the
     *          current token amount will result in a negative.
     * @see     TokensHolder
     * @see     Value
     * @see     ValueType
     */
    public TokenValueChangeEvent(final TokensHolder player,
                                 Value<Integer> value) throws TokenValueChangeException {
        super(player);

        value = Validate.notNull(value).clone();

        Validate.isTrue(value.isInteger());

        this.oldTokens = player.getTokens();
        this.type = value.getType();
        int changed = Integer.parseInt(value.toString());

        // Make sure tokens is 0 or bigger
        if (changed <= 0) {
            cancelled = true;
            throw new TokenValueChangeException("Cannot add tokens: The amount provided is a negative");
        }

        // If tokens are being taken away, make sure the new result is not 0
        if (type.equals(ValueType.TAKE) && oldTokens - changed < 0) {
            cancelled = true;
            throw new TokenValueChangeException("Subtracting %s tokens from %s will result in a negative balance", changed, player.getName());
        }

        this.changed = changed;
    }

    /**
     * @return The user that can hold the tokens.
     */
    public TokensHolder getPlayer() {
        return (TokensHolder) getProfile();
    }

    /**
     * @return The new amount of tokens the user has after the change.
     */
    public int getNewTokens() {
        return new Value<>(oldTokens, type).appendTo(changed);
    }

    public int getOldTokens() {
        return oldTokens;
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
