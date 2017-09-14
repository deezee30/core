/*
 * Part of core.
 * 
 * Created on 13 June 2017 at 2:01 PM.
 */

package com.maulss.core.bukkit.scoreboard;

import java.util.Map;

public interface ScoreboardContainer {

    String getTitle();

    Map<String, Integer> getRows();
}