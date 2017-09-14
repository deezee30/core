/*
 * Part of core.
 */

package com.maulss.core.bukkit.player.event;

import com.maulss.core.CoreException;

public class TokenValueChangeException extends CoreException {

    public TokenValueChangeException(String message,
                                     Object... components) {
        super(message, components);
    }

    public TokenValueChangeException(String message,
                                     Throwable cause) {
        super(message, cause);
    }

    public TokenValueChangeException(Throwable cause) {
        super(cause);
    }

    public TokenValueChangeException(String message,
                                     Throwable cause,
                                     boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}