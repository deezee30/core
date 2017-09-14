/*
 * Part of core.
 * 
 * Created on 02 July 2017 at 11:50 PM.
 */

package com.maulss.core.game.rating;

import java.util.UUID;

public interface Competitor {

    UUID getUuid();

    String getName();

    int getRating();

    void setRating(int rating);

    boolean isProvisional();
}