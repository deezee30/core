/*
 * Part of core.
 * 
 * Created on 13 June 2017 at 2:59 PM.
 */

package com.maulss.core.bukkit.scoreboard.handler;

import com.maulss.core.bukkit.scoreboard.IScoreboard;
import org.apache.commons.lang3.Validate;

public class CoreScoreboardHandler implements IScoreboardHandler {

    private final IScoreboard personalScoreboard;
    private ScoreboardBuilder builder;

    public CoreScoreboardHandler(IScoreboard personalScoreboard) {
        this.personalScoreboard = Validate.notNull(personalScoreboard);
    }

    @Override
    public CoreScoreboardHandler refresh() {
        // TODO: Instead of creating and destroying scoreboards each time,
        // TODO: Reuse the same scoreboard but refresh the teams and scores
        destroy();

        builder = new ScoreboardBuilder(
                personalScoreboard.getTitle(),
                personalScoreboard.getRows(),
                personalScoreboard.getMaxWidth()
        );

        personalScoreboard.getHolder().getBukkitPlayer().setScoreboard(builder.build());
        return this;
    }

    @Override
    public void destroy() {
        if (builder != null) builder.destroy();
    }

    @Override
    protected Object clone() {
        return new CoreScoreboardHandler(personalScoreboard);
    }
}