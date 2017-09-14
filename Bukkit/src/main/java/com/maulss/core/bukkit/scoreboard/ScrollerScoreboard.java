/*
 * Part of core.
 * 
 * Created on 13 June 2017 at 2:06 PM.
 */

package com.maulss.core.bukkit.scoreboard;

import com.maulss.core.bukkit.player.statistic.ScoreboardHolder;
import com.maulss.core.collect.EnhancedMap;
import com.maulss.core.text.Scroller;
import com.maulss.core.text.SimpleScroller;

import java.util.Map;

public class ScrollerScoreboard implements IScoreboard {

    private final ScoreboardHolder holder;
    private final int maxWidth;
    private final int spaceBetween;

    private Scroller title;
    private EnhancedMap<Scroller, Integer> rows;

    public ScrollerScoreboard(ScoreboardHolder holder, int maxWidth, int spaceBetween) {
        this.holder = holder;
        this.maxWidth = maxWidth;
        this.spaceBetween = spaceBetween;
        update();
    }

    @Override
    public String getTitle() {
        StringBuilder title = new StringBuilder(this.title.next());
        boolean a = true;
        while (title.length() < maxWidth - 5) {
            if (a = !a) title.insert(0, " ");
            else title.append(" ");
        }

        return title.toString();
    }

    @Override
    public Map<String, Integer> getRows() {
        EnhancedMap<String, Integer> rows = new EnhancedMap<>(this.rows.size());

        for (Map.Entry<Scroller, Integer> entry : this.rows.entrySet()) {
            rows.put(entry.getKey().next(), entry.getValue());
        }

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

        String newTitle = scoreboard.getTitle();
        Map<String, Integer> newRows = scoreboard.getRows();

        title = new SimpleScroller(
                newTitle == null
                        ? holder.getBukkitPlayer().getDisplayName()
                        : newTitle,
                maxWidth,
                spaceBetween
        );

        if (rows == null)
            rows = new EnhancedMap<>(newRows.size());

        rows.clear();
        for (Map.Entry<String, Integer> entry : newRows.entrySet()) {
            rows.put(new SimpleScroller(entry.getKey(), maxWidth, spaceBetween), entry.getValue());
        }
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }
}