/*
 * Part of core.
 * 
 * Created on 15 July 2017 at 8:46 PM.
 */

package com.maulss.core.database.mongo;

import com.maulss.core.Logger;
import com.maulss.core.database.DatabaseException;
import com.maulss.core.database.callback.VoidCallback;
import com.maulss.core.service.timer.Timer;
import com.mongodb.event.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MongoConnectionListener implements ServerMonitorListener, ServerListener {

    // String constants
    private static final String
            // Connection listener output
            OPEN                = "Successfully established a connection with Mongo database server in %sms",
            CLOSE               = "Closed Mongo connection in %sms",

            // Exceptions
            CONCURRENT_OPEN     = "Tried opening an open database",
            CONCURRENT_CLOSE    = "Tried closing a closed database",

            // Warnings
            HEARTBEAT_SKIP      = "Warning: Skipped heartbeat from connection %s";

    // Timer reference used for async stopwatch timing
    private final Timer
            timer               = new Timer();

    // Delegate Mongo database
    private final Mongo
            database;

    // Logger used for outputs and debugging
    private final Logger
            logger;

    // Connection callbacks
    private VoidCallback
            openCallback,
            closeCallback;

    // State of connection listener
    private boolean
            init                = false,
            firstHeartbeat      = false;

    public MongoConnectionListener(final Mongo database,
                                   final Logger logger) {
        this.database           = checkNotNull(database);
        this.logger             = checkNotNull(logger);
    }

    public synchronized void setOpenCallback(final VoidCallback callback) {
        this.openCallback       = checkNotNull(callback);
    }

    public synchronized void setCloseCallback(final VoidCallback callback) {
        this.closeCallback      = callback;
    }

    public synchronized Timer getTimer() {
        return timer;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("database", database)
                .toString();
    }

    @Override
    public void serverHearbeatStarted(ServerHeartbeatStartedEvent event) {}

    @Override
    public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent event) {
        if (init && !firstHeartbeat) {
            // First successful handshake
            firstHeartbeat = true;

            if (openCallback != null) {
                if (database.isConnected()) {
                    openCallback.onResult(new DatabaseException(
                            new ConcurrentModificationException(CONCURRENT_OPEN)));
                    return;
                }

                openCallback.onResult();
            }

            database.setConnected(true);
            timer.forceStop();
            logger.log(OPEN, timer.getTime(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public void serverOpening(ServerOpeningEvent event) {
        init = true;
    }

    @Override
    public synchronized void serverClosed(ServerClosedEvent event) {
        if (init && firstHeartbeat) {
            if (closeCallback != null) {
                if (!database.isConnected()) {
                    closeCallback.onResult(new DatabaseException(
                            new ConcurrentModificationException(CONCURRENT_CLOSE)));
                    return;
                }

                closeCallback.onResult();
            }

            database.setConnected(false);
            timer.forceStop();
            logger.log(CLOSE, timer.getTime(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
        logger.debugIf(init, HEARTBEAT_SKIP, database);
    }

    @Override
    public void serverDescriptionChanged(ServerDescriptionChangedEvent event) {}
}