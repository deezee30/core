/*
 * Part of core.
 * 
 * Created on 13 June 2017 at 2:13 PM.
 */

package com.maulss.core.bukkit.scoreboard;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.scoreboard.handler.CoreScoreboardHandler;
import com.maulss.core.bukkit.scoreboard.handler.IScoreboardHandler;
import com.maulss.core.collect.EnhancedMap;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public final class Scoreboards {

    public static final int MAX_WIDTH = 24;

    // Disable inialization
    private Scoreboards() {}

    public static Map<String, Integer> orderedRows(final List<String> rows) {
        int len = rows.size();
        EnhancedMap<String, Integer> map = new EnhancedMap<>(len);
        for (int i = 0; i < len; i++) {
            map.put(rows.get(i), len - i - 1);
        }

        return map;
    }

    public static IScoreboardHandler newScoreboard(final IScoreboard scoreboard,
                                                   final long refreshRateTicks) {
        CoreScoreboardHandler board = new CoreScoreboardHandler(scoreboard).refresh();

        new BukkitRunnable() {

            @Override
            public void run() {
                if (scoreboard.getHolder().getBukkitPlayer().isOnline()) {
                    board.refresh();
                } else {
                    board.destroy();
                    cancel();
                }
            }
        }.runTaskTimer(Core.get(), refreshRateTicks, refreshRateTicks);

        return board;
    }
}