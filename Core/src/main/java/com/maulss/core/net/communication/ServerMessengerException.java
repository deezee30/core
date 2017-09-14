/*
 * Part of core.
 * 
 * Created on 15 July 2017 at 2:28 PM.
 */

package com.maulss.core.net.communication;

import com.maulss.core.CoreException;

public class ServerMessengerException extends CoreException {

    public ServerMessengerException(final String message,
                                    final Object... components) {
        super(message, components);
    }

    public ServerMessengerException(final String message,
                                    final Throwable cause) {
        super(message, cause);
    }

    public ServerMessengerException(final Throwable cause) {
        super(cause);
    }

    public ServerMessengerException(final String message,
                                    final Throwable cause,
                                    final boolean enableSuppression,
                                    final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}