/*
 * Part of core.
 * Made on 02/09/2017
 */

package com.maulss.core.bukkit.player.profile;

import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import com.maulss.core.database.Identity;

public interface Profile extends Identity {

    /**
     * Static instance of the online player manager singleton class.
     */
    CorePlayerManager PLAYER_MANAGER = CorePlayerManager.getInstance();

    /**
     * Returns the name of this profile.
     *
     * @return Profile name
     */
    String getName();

    /**
     * Checks if this profile has played on the current server before.
     *
     * If the player was found in the general database but not in the player
     * database related to the current server, then {@code false} is returned.
     *
     * @return  {@code true} if the profile has played before,
     *          {@code false} if otherwise
     */
    boolean hasPlayed();

    /**
     * Checks if this profile has been found in the general database before.
     *
     * @return  {@code true} if the profile has played before,
     *          {@code false} if otherwise
     */
    boolean hasEverPlayed();

    /**
     * Checks if this profile is currently online.
     *
     * @return  {@code true} if they are online,
     *          {@code false} if otherwise
     */
    boolean isOnline();

    /**
     * Checks if the player {@link #isOnline()} and returns the only {@link
     * CorePlayer} instance that has been found according to both users'
     * {@link #getUuid()}.
     *
     * If {@link #isOnline()} is {@code false}, then {@code null} is returned.
     *
     * @return  A {@code CorePlayer} instance if one has been found.
     * @see     CorePlayer
     * @see     CorePlayerManager
     */
    default CorePlayer toCorePlayer() {
        return PLAYER_MANAGER.get(getUuid());
    }
}