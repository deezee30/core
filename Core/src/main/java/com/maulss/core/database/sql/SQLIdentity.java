/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.sql;

import com.maulss.core.database.callback.DatabaseCallback;
import com.maulss.core.database.DatabaseKey;
import com.maulss.core.database.Identity;
import com.maulss.core.database.Value;

import java.sql.ResultSet;
import java.util.List;

/**
 * A class used to indentify where a certain row in the database is located.
 * This can be used for anything varying from players to anything else as an
 * entry.
 */
public interface SQLIdentity extends Identity {

    /**
     * @return The table that this {@code SQLIdentity} is associated with.
     * @see        SQLTable
     */
    SQLTable getTable();

    default void contains(final DatabaseCallback<Boolean> callback) {
        getTable().contains(callback, this);
    }

    default void delete() {
        getTable().delete(this);
    }

    default void delete(final DatabaseCallback<Void> callback) {
        getTable().delete(callback, this);
    }

    default void insertInto(final SQLKey[] columns,
                            final Object... values) {
        getTable().insertInto(columns, values);
    }

    default void insertInto(final DatabaseCallback<ResultSet> callback,
                            final SQLKey[] columns,
                            final Object... values) {
        getTable().insertInto(callback, columns, values);
    }

    default void insert(final Object... values) {
        getTable().insert(values);
    }

    default void insert(final DatabaseCallback<ResultSet> callback,
                        final Object... values) {
        getTable().insert(callback, values);
    }

    default void update(final DatabaseKey column,
                        final Value value) {
        getTable().update(this, column, value);
    }

    default void update(final DatabaseCallback<ResultSet> callback,
                        final DatabaseKey column,
                        final Value value) {
        getTable().update(callback, this, column, value);
    }

    default void update(final DatabaseKey column,
                        final Object value) {
        getTable().update(this, column, value);
    }

    default void update(final DatabaseCallback<ResultSet> callback,
                        final DatabaseKey column,
                        final Object value) {
        getTable().update(callback, this, column, value);
    }

    default void update(final DatabaseKey[] columns,
                        final Value... values) {
        getTable().update(this, columns, values);
    }

    default void update(final DatabaseCallback<ResultSet> callback,
                        final DatabaseKey[] columns,
                        final Value... values) {
        getTable().update(callback, this, columns, values);
    }

    default void update(final DatabaseKey[] columns,
                        final Object... values) {
        getTable().update(this, columns, values);
    }

    default void update(final DatabaseCallback<ResultSet> callback,
                        final DatabaseKey[] columns,
                        final Object... values) {
        getTable().update(callback, this, columns, values);
    }

    default void get(final DatabaseCallback<Object> callback,
                     final DatabaseKey column) {
        getTable().get(callback, this, column);
    }

    default void get(final DatabaseCallback<List<Object>> callback,
                     final DatabaseKey... columns) {
        getTable().get(callback, this, columns);
    }

    default void get(final DatabaseCallback<ResultSet> callback) {
        getTable().get(callback, this);
    }
}