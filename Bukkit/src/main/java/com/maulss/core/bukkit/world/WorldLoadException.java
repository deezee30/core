/*
 * Part of core.
 * 
 * Created on 16 July 2017 at 1:22 AM.
 */

package com.maulss.core.bukkit.world;

import com.maulss.core.CoreException;

public class WorldLoadException extends CoreException {

    public WorldLoadException(final String message,
                              final Object... components) {
        super(message, components);
    }

    public WorldLoadException(final String message,
                              final Throwable cause) {
        super(message, cause);
    }

    public WorldLoadException(final Throwable cause) {
        super(cause);
    }

    public WorldLoadException(final String message,
                              final Throwable cause,
                              final boolean enableSuppression,
                              final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}