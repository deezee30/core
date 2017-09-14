/*
 * Part of core.
 * 
 * Created on 11 June 2017 at 8:22 PM.
 */

package com.maulss.core.bukkit.chat.filter;

import com.maulss.core.bukkit.player.CorePlayer;

import java.util.Optional;

/**
 * The interface Chat block filter.
 */
public interface ChatBlockFilter {

    /**
     * Whether or not the message should be filtered out.
     *
     * @param player  the player that sent the message
     * @param message the message
     * @return {@code true} if message should be filtered
     */
    boolean block(final CorePlayer player,
                  final String message);

    /**
     * The reason why the message is blocked
     *
     * <p>Supports locales</p>
     *
     * @return an optional reason why the message is blocked
     */
    Optional<String> getReason();

    /**
     * @return Whether or not this filter causes a violation
     * on the player's part
     */
    boolean violate();
}