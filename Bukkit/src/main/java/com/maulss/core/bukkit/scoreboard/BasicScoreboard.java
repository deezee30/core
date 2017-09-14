/*
 * Part of core.
 * 
 * Created on 13 June 2017 at 11:54 PM.
 */

package com.maulss.core.bukkit.scoreboard;

import com.maulss.core.bukkit.player.statistic.ScoreboardHolder;
import com.maulss.core.collect.EnhancedMap;

import java.util.Map;

public class BasicScoreboard implements IScoreboard {

    private final ScoreboardHolder holder;

    private String title;
    private EnhancedMap<String, Integer> rows;

    public BasicScoreboard(final ScoreboardHolder holder) {
        this.holder = holder;
        update();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Map<String, Integer> getRows() {
        return rows;
    }

    @Override
    public ScoreboardHolder getHolder() {
        return holder;
    }

    @Override
    public void update() {
        ScoreboardContainer scoreboard = holder.getScoreboard();
        if (scoreboard == null) return;

        title = scoreboard.getTitle() == null
                ? holder.getBukkitPlayer().getDisplayName()
                : scoreboard.getTitle();

        Map<String, Integer> newRows = scoreboard.getRows();

        if (rows == null)
            rows = new EnhancedMap<>(newRows.size());

        rows.clear();
        rows.putAll(newRows);
    }
}