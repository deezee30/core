/*
 * rv_core
 * 
 * Created on 02 July 2017 at 11:50 PM.
 */

package com.maulss.core.game.rating.elo;

import com.maulss.core.game.rating.Competitor;
import com.maulss.core.game.rating.KFactor;
import com.maulss.core.game.rating.StaticKFactor;
import com.maulss.core.math.MathUtil;

public interface EloCompetitor extends Competitor {

    int INITIAL_RATING = 1200;

    default double getRelativeEloRating() {
        return MathUtil.round((double) getRating() / (double) INITIAL_RATING, 2);
    }

    default KFactor getKFactor() {
        return StaticKFactor.getKFactor(getRating(), isProvisional());
    }
}