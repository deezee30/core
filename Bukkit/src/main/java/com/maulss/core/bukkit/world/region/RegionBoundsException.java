/*
 * MaulssLib
 * 
 * Created on 11 February 2015 at 4:47 PM.
 */

package com.maulss.core.bukkit.world.region;

import com.maulss.core.CoreException;

public class RegionBoundsException extends CoreException {

    private static final long serialVersionUID = -2633695118196820017L;

    public RegionBoundsException(String message) {
        super(message);
    }

    public RegionBoundsException(String message,
                                 Throwable cause) {
        super(message, cause);
    }

    public RegionBoundsException(Throwable cause) {
        super(cause);
    }

    public RegionBoundsException(String message,
                                 Throwable cause,
                                 boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}