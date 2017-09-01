/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.sql;

import com.google.common.base.Strings;
import com.maulss.core.Logger;
import com.maulss.core.database.*;
import com.maulss.core.database.callback.DatabaseCallback;
import com.maulss.core.database.callback.UnhandledCallback;
import com.maulss.core.database.callback.VoidCallback;
import com.maulss.core.service.ServiceExecutor;
import com.maulss.core.service.timer.Timer;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("unchecked")
public abstract class SQLDatabase implements Database {

    // String constants
    private static final String
            // Connection output
            CONNECTION_OPEN                 = "Connected to %s database at '%s' in %sms",
            CONNECTION_CLOSE                = "Closed database connection in %sms",

            // Execution timing
            UPDATE_TIMING                   = "Database update: %sms",
            QUERY_TIMING                    = "Database query:  %sms",

            // SQL Syntax
            CREATE_DATABASE                 = "CREATE DATABASE `%s`;",
            CREATE_DATABASE_IF_NOT_EXISTS   = "CREATE DATABASE `%s` IF NOT EXISTS;",

            // Errors
            INVALID_SYNTAX                  = "Invalid syntax",
            COLUMNS_VALUES_NO_MATCH         = "Column count doesn't match value count";


    // Timer reference used for async stopwatch timing
    private volatile Timer
            cachedTimer                     = new Timer();

    // Logger used for outputs and debugging
    private final Logger
            logger                          = new Logger();

    // Unhandled callback handler
    private final DatabaseCallback
            unhandledCallback               = new UnhandledCallback(logger);

    // The executor used for executing updates, queries and other management
    private volatile ExecutorService
            executor;

    // Connection pipeline to the database
    private volatile Connection
            connection;

    public SQLDatabase() {
        this(ServiceExecutor.newAsyncExecutor());
    }

    public SQLDatabase(final ExecutorService executor) {
        this.executor = checkNotNull(executor, "executor");
    }

    /**
     * Checks if the connection is open.  If it's closed, it attempts to {@link
     * #open()} the connection again. If an error has been caught while trying
     * to contact the database, it calls the method again until 3 attempts have
     * been made.
     *
     * This method must be called whenever a connection to the database is
     * updated. For example, querying, updating or obtaining data to/from the
     * database.
     *
     * @see #open()
     */
    @Override
    public final synchronized SQLDatabase connect(final VoidCallback callback) {
        checkNotNull(callback);

        if (isConnected()) {
            return this;
        }

        executor.execute(() -> {
            cachedTimer.start();

            String connectUrl;
            try {
                connection = open();
                connectUrl = connection.getMetaData().getURL();
            } catch (SQLException | DatabaseException e) {
                callback.onResult(e);
                return;
            } finally {
                cachedTimer.forceStop();
            }

            debug(CONNECTION_OPEN, getName(), connectUrl,
                    cachedTimer.getTime(TimeUnit.MILLISECONDS));
        });

        return this;
    }

    /**
     * Opens the connection for the database.  If the database type does not use
     * {@link Connection} as its Connection variable, it has the
     * ability to return null.  Null may also be returned if the connection to
     * the database has not been made properly.
     *
     * @return Null if the database was not found, or the database type does not
     * work with the {@link Connection} object. Otherwise it returns
     * the a new connection that has been established.
     * @throws DatabaseException
     *         If an error occurs while opening the connection
     * @see Connection
     */
    protected abstract Connection open() throws DatabaseException;

    /**
     * @return The name of currently selected database
     */
    public abstract String getName();

    /**
     * @return The executor used for executing updates, queries and other
     * management
     */
    public final synchronized ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public final synchronized Logger getLogger() {
        return logger;
    }

    @Override
    public final synchronized boolean isConnected() {
        if (connection == null) return false;

        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public final synchronized SQLDatabase disconnect(final VoidCallback callback) {
        if (isConnected()) {
            cachedTimer.start();

            try {
                connection.close();
                connection = null;
            } catch (SQLException ignored) {
                callback.onResult(ignored);
            } finally {
                cachedTimer.forceStop();
            }

            debug(CONNECTION_CLOSE, cachedTimer.getTime(TimeUnit.MILLISECONDS));
        }

        return this;
    }

    @Nullable
    public final synchronized Connection getConnection() {
        return connection;
    }

    public final SQLTable getTable(final String name) {
        checkNotNull(name, "table name");
        return new SQLTable(this, unhandledCallback, name);
    }

    public final SQLDatabase create() {
        return create(unhandledCallback,true);
    }

    public final SQLDatabase create(final DatabaseCallback<ResultSet> callback) {
        return create(callback, true);
    }

    public final SQLDatabase create(final DatabaseCallback<ResultSet> callback,
                                    final boolean ifNotExists) {
        String update = ifNotExists
                ? CREATE_DATABASE_IF_NOT_EXISTS
                : CREATE_DATABASE;

        update(callback, String.format(update, getName()));

        return this;
    }

    public final void query(final DatabaseCallback<ResultSet> callback,
                            final String query) {
        executor.execute(() -> {

            // Record heavy tasks with a timer as usual
            cachedTimer.start();

            Throwable error = null;
            CachedRowSet result = null;
            try {
                checkExecution(query);

                // Using CachedRowSet so that we can manipulate the ResultSet
                // later on after it was closed.
                result = new CachedRowSetImpl();

                Statement statement = connection.createStatement();
                ResultSet row = statement.executeQuery(query);
                // Using CachedRowSet so that we can manipulate the ResultSet
                // later on after it was closed.
                result = new CachedRowSetImpl();
                result.populate(row);

                statement.close();
            } catch (Throwable throwable) {
                error = throwable;
            } finally {
                callback.onResult(result, error);
                cachedTimer.forceStop();
                debug(QUERY_TIMING, cachedTimer.getTime(TimeUnit.MILLISECONDS));
            }
        });
    }

    public final void query(final DatabaseCallback<ResultSet> callback,
                            final String query,
                            final Object... values) {
        executor.execute(() -> {

            // Record heavy tasks with a timer as usual
            cachedTimer.start();

            Throwable error = null;
            CachedRowSet result = null;
            try {
                checkExecution(query);

                // Using CachedRowSet so that we can manipulate the ResultSet
                // later on after it was closed.
                result = new CachedRowSetImpl();

                PreparedStatement statement = connection.prepareStatement(query);
                set(statement, values);

                ResultSet row = statement.executeQuery(query);
                // Using CachedRowSet so that we can manipulate the ResultSet
                // later on after it was closed.
                result = new CachedRowSetImpl();
                result.populate(row);

                statement.close();
            } catch (Throwable throwable) {
                error = throwable;
            } finally {
                callback.onResult(result, error);
                cachedTimer.forceStop();
                debug(QUERY_TIMING, cachedTimer.getTime(TimeUnit.MILLISECONDS));
            }
        });
    }

    public final void update(final String update) {
        update(unhandledCallback, update);
    }

    public final void update(final DatabaseCallback<ResultSet> callback,
                             final String update) {
        executor.execute(() -> {

            // Record heavy tasks with a timer as usual
            cachedTimer.start();

            Throwable error = null;
            CachedRowSet result = null;
            try {
                checkExecution(update);

                // Using CachedRowSet so that we can manipulate the ResultSet
                // later on after it was closed.
                result = new CachedRowSetImpl();

                PreparedStatement statement = connection.prepareStatement(update,
                        Statement.RETURN_GENERATED_KEYS);
                statement.executeUpdate();

                // Make sure the execution returns the auto-generated keys
                result.populate(statement.getGeneratedKeys());

                statement.close();
            } catch (Throwable throwable) {
                error = throwable;
            } finally {
                callback.onResult(result, error);
                cachedTimer.forceStop();
                debug(UPDATE_TIMING, cachedTimer.getTime(TimeUnit.MILLISECONDS));
            }
        });
    }

    public final void update(final String update,
                             final Object... values) {
        update(unhandledCallback, update, values);
    }

    public final void update(final DatabaseCallback<ResultSet> callback,
                             final String update,
                             final Object... values) {
        executor.execute(() -> {

            // Record heavy tasks with a timer as usual
            cachedTimer.start();

            Throwable error = null;
            CachedRowSet result = null;
            try {
                checkExecution(update);

                // Using CachedRowSet so that we can manipulate the ResultSet
                // later on after it was closed.
                result = new CachedRowSetImpl();

                PreparedStatement statement = connection.prepareStatement(update,
                        Statement.RETURN_GENERATED_KEYS);
                set(statement, values);

                statement.executeUpdate();

                // Make sure the execution returns the auto-generated keys
                result.populate(statement.getGeneratedKeys());

                statement.close();
            } catch (Throwable throwable) {
                error = throwable;
            } finally {
                callback.onResult(result, error);
                cachedTimer.forceStop();
                debug(UPDATE_TIMING, cachedTimer.getTime(TimeUnit.MILLISECONDS));
            }
        });
    }

    private void set(final PreparedStatement statement,
                     final Object... values) throws DatabaseException {
        checkNotNull(statement, "statement");

        int x = 0;
        while (x < values.length) {
            try {
                statement.setObject(++x, values[x]);
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }
    }

    private boolean checkExecution(final String execution,
                                   final Object... vars) throws DatabaseException {
        // Make sure Database != null and is also connected
        synchronized (this) {
            if (executor.isShutdown())
                executor = ServiceExecutor.newAsyncExecutor();
        }

        Throwable error = null;

        if (Strings.isNullOrEmpty(execution)) {
            error = new IllegalArgumentException(INVALID_SYNTAX);
        }

        if (vars.length > 0 && vars.length != StringUtils.countMatches(execution, "?")) {
            error = new IllegalArgumentException(COLUMNS_VALUES_NO_MATCH);
        }

        if (error == null) {
            // No error has occurred -- String looks ok so far
            return true;
        }

        throw new DatabaseException(error);
    }
}