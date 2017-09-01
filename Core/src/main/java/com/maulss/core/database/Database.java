/*
 * Part of core.
 * Made on 30/07/2017
 */

package com.maulss.core.database;

import com.maulss.core.Logger;
import com.maulss.core.database.callback.VoidCallback;

import java.util.Optional;

/**
 * Represents any remote or local database.
 */
public interface Database extends AutoCloseable {

    /**
     * @return whether or not a successful connection has been established to
     * the database.
     */
    boolean isConnected();

    /**
     * Attempts to establish a new connection to the database.
     *
     * All relevant information must already be passed onto the database
     * instance before this method is called.
     *
     * Due to a default empty callback, all errors and results will be
     * suppressed.
     *
     * @return this instance
     */
    default Database connect() {
        return connect(throwable -> {});
    }

    /**
     * Attempts to establish a new connection to the database.
     *
     * All relevant information must already be passed onto the database
     * instance before this method is called.
     *
     * If any errors occur, they will be passed to the void callback provided
     * in the parameter.
     *
     * @param callback
     *         void callback that's executed on connect
     * @return this instance
     */
    Database connect(final VoidCallback callback);

    /**
     * Forces disconnect from the database.
     *
     * Due to a default empty callback, all errors and results will be
     * suppressed.
     *
     * @return this instance
     */
    default Database disconnect() {
        return disconnect(throwable -> {});
    }

    /**
     * Forces disconnect from the database.
     *
     * If any errors occur, they will be passed to the void callback provided
     * in the parameter.
     *
     * @param callback
     *         void callback that's executed on disconnect
     * @return this instance
     */
    Database disconnect(final VoidCallback callback);

    /**
     * @return the logger associated with this database instance to track errors
     * and progression
     */
    Logger getLogger();

    default Optional<String> log(final String message,
                                 final Object... components) {
        return getLogger().log(message, components);
    }

    default Optional<String> debug(final String message,
                                   final Object... components) {
        return getLogger().debug(message, components);
    }

    @Override
    default void close() {
        if (isConnected()) disconnect();
    }
}