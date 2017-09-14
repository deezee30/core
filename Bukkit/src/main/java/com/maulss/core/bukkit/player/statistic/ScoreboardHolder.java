/*
 * Part of core.
 */

package com.maulss.core.bukkit.player.statistic;

import com.maulss.core.bukkit.scoreboard.ScoreboardContainer;
import org.bukkit.entity.Player;

/**
 * Represents a live client who is able to hold a {@link
 * org.bukkit.scoreboard.Scoreboard}.  The player must be
 * online and the class instance must be flexible enough
 * to adjust to very frequent calls to {@link #getScoreboard()},
 * as the scoreboard could be updated a few times a second
 * per player.
 *
 * @see ScoreboardContainer
 */
public interface ScoreboardHolder {

    ScoreboardContainer getScoreboard();

    /**
     * @return The Bukkit player instance that holds this player.
     */
    Player getBukkitPlayer();
}