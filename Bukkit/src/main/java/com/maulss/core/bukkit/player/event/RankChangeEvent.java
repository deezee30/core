/*
 * Part of core.
 * Made on 09/09/2017
 */

package com.maulss.core.bukkit.player.event;

import com.maulss.core.bukkit.Rank;
import com.maulss.core.bukkit.player.statistic.RankHolder;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Cancellable;

public class RankChangeEvent extends CorePlayerEvent implements Cancellable {

    private Rank oldRank;
    private Rank newRank;
    private boolean cancel = false;

    public RankChangeEvent(final RankHolder profile,
                           final Rank newRank) {
        super(profile);
        Validate.notNull(newRank, "newRank");

        oldRank = profile.getRank();
        this.newRank = newRank;

        // Cancel if event if no rank update occurs
        if (profile.getRank().equals(newRank)) {
            cancel = true;
        }
    }

    public RankHolder getPlayer() {
        return (RankHolder) getProfile();
    }

    public Rank getOldRank() {
        return oldRank;
    }

    public Rank getNewRank() {
        return newRank;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}