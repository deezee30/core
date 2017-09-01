/*
 * rv_core
 * 
 * Created on 02 July 2017 at 11:21 PM.
 */

package com.maulss.core.game.rating;

public enum StaticKFactor implements KFactor {

    DEFAULT     ( 50.0),
    PROVISIONAL (125.0),
    DOWN1800    ( 55.0),
    DOWN2400    ( 40.0),
    UP2400      ( 25.0);

    private final double k;

    StaticKFactor(final double k) {
        this.k = k;
    }

    @Override
    public double getKFactor() {
        return k;
    }

    /**
     * This is the standard variable constant.  This constant can differ
     * based on different games.  The higher the constant the faster
     * the rating will grow.  That is why for this standard pvp method,
     * the constant is higher for weaker players and lower for stronger
     * players.
     *
     * @param   rating Rating
     * @return  Constant
     */
    public static KFactor getKFactor(int rating, boolean isNewbie) {
        if (isNewbie) {
            return PROVISIONAL;
        } else if (rating < 1800) {
            return DOWN1800;
        } else if (rating < 2400) {
            return DOWN2400;
        } else if (rating >= 2400) {
            return UP2400;
        }

        return DEFAULT;
    }
}