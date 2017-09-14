/*
 * Part of core.
 * 
 * Created on 13 June 2017 at 1:47 PM.
 */

package com.maulss.core.bukkit.scoreboard.handler;

public interface IScoreboardHandler extends Cloneable {

    IScoreboardHandler refresh();

    void destroy();
}