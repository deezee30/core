/*
 * Part of core.
 * Made on 09/09/2017
 */

package com.maulss.core.bukkit.player;

public class PlayerNotConnectedException extends RuntimeException {

    private static final String MSG = "Invoked an unsupported process to an offline player";

    public PlayerNotConnectedException() {
        this(MSG);
    }

    public PlayerNotConnectedException(final String message) {
        super(message);
    }

    public PlayerNotConnectedException(final String message,
                                       final Throwable cause) {
        super(message, cause);
    }

    public PlayerNotConnectedException(final Throwable cause) {
        super(MSG, cause);
    }
}