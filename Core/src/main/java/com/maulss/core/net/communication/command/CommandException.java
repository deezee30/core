/*
 * rv_core
 * 
 * Created on 15 July 2017 at 7:38 PM.
 */

package com.maulss.core.net.communication.command;

import com.maulss.core.net.communication.ServerMessengerException;

public class CommandException extends ServerMessengerException {

    public CommandException(final String message,
                            final Object... components) {
        super(message, components);
    }

    public CommandException(final String message,
                            final Throwable cause) {
        super(message, cause);
    }

    public CommandException(final Throwable cause) {
        super(cause);
    }

    public CommandException(final String message,
                            final Throwable cause,
                            final boolean enableSuppression,
                            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}