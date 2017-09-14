/*
 * Part of core.
 * 
 * Created on 13 June 2017 at 1:22 PM.
 */

package com.maulss.core.bukkit.scoreboard;

import com.maulss.core.bukkit.player.statistic.ScoreboardHolder;

import java.util.Map;

public interface IScoreboard {

    String getTitle();

    Map<String, Integer> getRows();

    ScoreboardHolder getHolder();

    void update();

    default int getMaxWidth() {
        return Scoreboards.MAX_WIDTH;
    }
}