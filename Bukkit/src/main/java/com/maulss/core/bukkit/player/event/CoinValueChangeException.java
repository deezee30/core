/*
 * Part of core.
 * 
 * Created on 03 June 2017 at 7:23 PM.
 */

package com.maulss.core.bukkit.player.event;

import com.maulss.core.CoreException;

public class CoinValueChangeException extends CoreException {

    public CoinValueChangeException(String message,
                                    Object... components) {
        super(message, components);
    }

    public CoinValueChangeException(String message,
                                    Throwable cause) {
        super(message, cause);
    }

    public CoinValueChangeException(Throwable cause) {
        super(cause);
    }

    public CoinValueChangeException(String message,
                                    Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}