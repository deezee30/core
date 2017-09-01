/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.sql;

import com.maulss.core.collect.EnhancedList;
import com.maulss.core.database.*;
import com.maulss.core.database.callback.DatabaseCallback;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

@SuppressWarnings("unchecked")
public class SQLTable {

    // SQL Syntax
    private static final String
            CREATE                          = "CREATE TABLE IF NOT EXISTS `%s` (%s);",

            SELECT_COUNT                    = "SELECT count(*) FROM `%s`;",
            SELECT_ALL                      = "SELECT * FROM `%s`;",
            SELECT_ALL_WHERE                = "SELECT * FROM `%s` WHERE `%s` = ? LIMIT 1;",
            SELECT_WHERE                    = "SELECT %s FROM `%s` WHERE `%s` = ? LIMIT 1;",

            UPDATE_SET_WHERE                = "UPDATE `%s` SET %s WHERE `%s` = ?;",

            DELETE_WHERE                    = "DELETE FROM `%s` WHERE `%s` = ?;",

            INSERT_VALUES                   = "INSERT INTO `%s` VALUES (%s);",
            INSERT_INTO_VALUES              = "INSERT INTO `%s` (%s) VALUES (%s);";

    // Error messages
    private static final String
            NULL_CALLBACK                   = "Callback can't be null; if no callback is needed, use an empty callback instead",
            NULL_IDENTITY                   = "Could not delete the identity because it is null",
            NULL_KEYS                       = "Could not create a new table because the SQL keys are null",
            NULL_COLUMNS                    = "Could not insert values into columns because the columns are null",
            NULL_VALUES                     = "Could not insert values into columns because the values are null",
            EMPTY_VALUES                    = "Could not insert values into columns because the values array is empty",
            EMPTY_COLUMNS                   = "There should be more than 0 columns to look up",
            COLUMN_VALUE_LENGTH_NO_MATCH    = "The amount of columns in the database does not match the amount of values that were put in",

            TABLE_CHECK_NO_CONNECTION       = "Tried checking if table exists without connection";


    private final SQLDatabase       database;
    private final DatabaseCallback  callbackHandler;
    private final String            table;

    SQLTable(final SQLDatabase database,
             final DatabaseCallback callbackHandler,
             final String table) {
        this.database           = notNull(database, "database");
        this.callbackHandler    = notNull(callbackHandler, "callbackHandler");
        this.table              = notNull(table, "table");
    }


    /**
     * Creates a new table with the specified array of {@link SQLKey} types.
     *
     * @param columns
     *         The list of types of columns to be inserted into the database.
     * @throws NullPointerException
     *         If {@code columns == null}.
     * @see SQLKey
     */
    public void create(final SQLKey[] columns) {
        create(callbackHandler, columns);
    }


    /**
     * Creates a new table with the specified array of {@link SQLKey} types.
     *
     * @param callback
     *         The callback with the optional error and result
     * @param columns
     *         The list of types of columns to be inserted into the database.
     * @throws NullPointerException
     *         If {@code columns == null}.
     * @see SQLKey
     */
    public void create(final DatabaseCallback<ResultSet> callback,
                       final SQLKey[] columns) {
        notNull(callback, NULL_CALLBACK);
        notNull(columns, NULL_KEYS);

        StringBuilder colBuilder = new StringBuilder();

        for (int x = 0; x < columns.length; ++x) {
            colBuilder.append(columns[x].compileWildcards());

            if (columns.length - 1 != x) colBuilder.append(", ");
        }

        database.update(callback, String.format(CREATE, this, colBuilder));
    }


    /**
     * Creates a new table with the specified array of {@link SQLKey} types.
     *
     * @param types
     *         The list of types of columns to be inserted into the database.
     * @param index
     *         The column that will be marked as an index.
     * @throws NullPointerException
     *         If {@code types == null}.
     * @see SQLKey
     * @see DatabaseKey
     */
    public void create(final SQLKey[] types,
                       final DatabaseKey index) {
        create(callbackHandler, types, index);
    }


    /**
     * Creates a new table with the specified array of {@link SQLKey} types.
     *
     * @param callback
     *         The callback with the optional error and result
     * @param types
     *         The list of types of columns to be inserted into the database.
     * @param index
     *         The column that will be marked as an index.
     * @throws NullPointerException
     *         If {@code types == null}.
     * @see SQLKey
     * @see DatabaseKey
     */
    public void create(final DatabaseCallback<ResultSet> callback,
                       final SQLKey[] types,
                       final DatabaseKey index) {
        if (index == null) {
            create(callback, types);
            return;
        }

        notNull(types, NULL_KEYS);

        StringBuilder colBuilder = new StringBuilder();

        for (int x = 0; x < types.length; ++x) {
            colBuilder.append(types[x].compileWildcards()).append(", ");

            // Append INDEX at the very end
            if (types.length - 1 == x) {
                colBuilder.append("INDEX (");
                colBuilder.append(index.getKey());
                colBuilder.append(")");
            }
        }

        database.update(callback, String.format(CREATE, this, colBuilder));
    }


    /**
     * Checks if the name of the table held in this {@code Table} instance
     * actually exists in the database.
     *
     * This is processed by filtering out tables in the database which name and
     * type matches the ones of this class and returning the one that was left
     * using {@link DatabaseMetaData#getTables(String, String, String,
     * String[])}.
     *
     * @param callback
     *         The callback with the optional error and result
     * @throws NullPointerException
     *         If {@code callback == null}.
     * @see DatabaseMetaData
     * @see DatabaseMetaData#getTables(String, String, String, String[])
     */
    public void exists(final DatabaseCallback<Boolean> callback) {
        notNull(callback, NULL_CALLBACK);
        database.getExecutor().execute(() -> {
            Throwable error = null;
            Boolean exists = null;
            try {
                if (!database.isConnected()) {
                    throw new DatabaseException(TABLE_CHECK_NO_CONNECTION);
                }

                DatabaseMetaData meta = database.getConnection().getMetaData();
                // Obtain all tables that match the criteria -- Should only be 1
                ResultSet res = meta.getTables(null, null, getName(), new String[] {"TABLE"});
                exists = res.next();
                res.close();
            } catch (Exception e) {
                error = e;
            } finally {
                callback.onResult(exists, error);
            }
        });
    }


    /**
     * Inserts a new entry into the table containing the {@param columns} with
     * their {@param types}.  The amount of columns must equal to the amount of
     * values.  If the amount of values equal to the amount of columns that are
     * in this table already, take a look at {@link #insert(DatabaseCallback,
     * Object...)} for in this case the provided columns are not necessary.
     *
     * @param columns
     *         The columns that correspond to the columns in the database.
     * @param values
     *         The values to insert.  Each value must correspond to each
     *         column.
     * @throws NullPointerException
     *         If {@code callback == null || columns == null || values ==
     *         null}.
     * @throws IllegalArgumentException
     *         If {@code values.length == 0 || columns.length == 0} or the
     *         {@code length} of {@param columns} does not equal to the {@code
     *         length} of {@param values}.
     * @see SQLKey
     * @see #insert(DatabaseCallback, Object...)
     */
    public void insertInto(final SQLKey[] columns,
                           final Object... values) {
        insertInto(callbackHandler, columns, values);
    }


    /**
     * Inserts a new entry into the table containing the {@param columns} with
     * their {@param types}.  The amount of columns must equal to the amount of
     * values.  If the amount of values equal to the amount of columns that are
     * in this table already, take a look at {@link #insert(DatabaseCallback,
     * Object...)} for in this case the provided columns are not necessary.
     *
     * @param callback
     *         The callback with the optional error and result
     * @param columns
     *         The columns that correspond to the columns in the database.
     * @param values
     *         The values to insert.  Each value must correspond to each
     *         column.
     * @throws NullPointerException
     *         If {@code callback == null || columns == null || values ==
     *         null}.
     * @throws IllegalArgumentException
     *         If {@code values.length == 0 || columns.length == 0} or the
     *         {@code length} of {@param columns} does not equal to the {@code
     *         length} of {@param values}.
     * @see SQLKey
     * @see #insert(DatabaseCallback, Object...)
     */
    public void insertInto(final DatabaseCallback<ResultSet> callback,
                           final SQLKey[] columns,
                           final Object... values) {
        notNull(callback, NULL_CALLBACK);
        notNull(columns, NULL_COLUMNS);
        notNull(values, NULL_VALUES);
        notEmpty(values, EMPTY_VALUES);
        isTrue(columns.length == values.length, COLUMN_VALUE_LENGTH_NO_MATCH);

        StringBuilder colBuilder = new StringBuilder();
        StringBuilder valBuilder = new StringBuilder();

        for (int x = 0; x < columns.length; ++x) {
            colBuilder.append("`").append(columns[x].getKey()).append("`");
            valBuilder.append("?");

            if (columns.length - 1 != x) {
                colBuilder.append(", ");
                valBuilder.append(", ");
            }
        }

        database.update(callback, String.format(
                INSERT_INTO_VALUES,
                this,
                colBuilder,
                valBuilder
        ), values);
    }


    /**
     * Inserts a new entry into the table with the provided values. In this
     * case, this method should only be used when the amount of values must
     * equal to the amount of columns that exist in the table already.  If the
     * amount of {@param values} does NOT equal to the amount of columns that
     * exist in the table, take a look at {@link #insertInto(DatabaseCallback,
     * SQLKey[], Object...)}.
     *
     * @param values
     *         The values to insert.  Each value must correspond to each column
     *         in the table.
     * @throws NullPointerException
     *         If {@code callback == null || values == null}.
     * @throws IllegalArgumentException
     *         If {@code values.length == 0}.
     * @see SQLKey
     * @see #insertInto(DatabaseCallback, SQLKey[], Object...)
     */
    public void insert(final Object... values) {
        insert(callbackHandler, values);
    }


    /**
     * Inserts a new entry into the table with the provided values. In this
     * case, this method should only be used when the amount of values must
     * equal to the amount of columns that exist in the table already.  If the
     * amount of {@param values} does NOT equal to the amount of columns that
     * exist in the table, take a look at {@link #insertInto(DatabaseCallback,
     * SQLKey[], Object...)}.
     *
     * @param callback
     *         The callback with the optional error and result
     * @param values
     *         The values to insert.  Each value must correspond to each column
     *         in the table.
     * @throws NullPointerException
     *         If {@code callback == null || values == null}.
     * @throws IllegalArgumentException
     *         If {@code values.length == 0}.
     * @see SQLKey
     * @see #insertInto(DatabaseCallback, SQLKey[], Object...)
     */
    public void insert(final DatabaseCallback<ResultSet> callback,
                       final Object... values) {
        notNull(callback, NULL_CALLBACK);
        notNull(values, NULL_VALUES);
        notEmpty(values, EMPTY_VALUES);

        StringBuilder valBuilder = new StringBuilder();

        for (int x = 0; x < values.length; ++x) {
            valBuilder.append("?");

            if (values.length - 1 != x) {
                valBuilder.append(", ");
            }
        }

        database.update(callback, String.format(INSERT_VALUES, this, valBuilder), values);
    }


    /**
     * Deletes an entire row from the table.
     *
     * @param identity
     *         The known identity which is used as an index in the database.
     * @throws NullPointerException
     *         If {@code identity == null}.
     * @see Identity
     */
    public void delete(final Identity identity) {
        delete(callbackHandler, identity);
    }


    /**
     * Deletes an entire row from the table.
     *
     * @param callback
     *         The callback with the optional error and result.
     * @param identity
     *         The known identity which is used as an index in the database.
     * @throws NullPointerException
     *         If {@code callback == null || identity == null}.
     * @see Identity
     */
    public void delete(final DatabaseCallback<Void> callback,
                       final Identity identity) {
        notNull(callback, NULL_CALLBACK);
        notNull(identity, NULL_IDENTITY);

        database.update(String.format(
                DELETE_WHERE,
                this,
                identity.getDatabaseKey().getKey()
        ), identity.getUuid());
    }


    /**
     * Checks if an {@link Identity} exists in this table.
     *
     * @param callback
     *         The callback with the optional error and result.
     * @param identity
     *         The known identity which is used as an index in the database.
     * @throws NullPointerException
     *         If {@code callback == null || identity == null}.
     * @see #get(DatabaseCallback, Identity)
     */
    public void contains(final DatabaseCallback<Boolean> callback,
                         final Identity identity) {
        get((result, t) -> {
            Boolean contains = null;
            try {
                contains = result.next();
            } catch (SQLException e) {
                t = e;
            } finally {
                callback.onResult(contains, t);
            }
        }, identity);
    }


    /**
     * Updates a value in the database for the specified sub class of {@link
     * Identity}.  {@param value} is used to incrememnt, decrement or set the
     * values specifically. For more information, take a look at {@link Value}
     * and {@link ValueType}.
     *
     * @param identity
     *         Any instance of {@link Identity} to be found in the database.
     * @param column
     *         The column that is to be modified.
     * @param value
     *         The new value that will replace the old one.
     * @throws NullPointerException
     *         If {@code identity == null || column == null}.
     * @see Identity
     * @see DatabaseKey
     * @see Value
     */
    public final void update(final Identity identity,
                             final DatabaseKey column,
                             final Value value) {
        update(callbackHandler, identity, column, value);
    }


    /**
     * Updates a value in the database for the specified sub class of {@link
     * Identity}.  {@param value} is used to incrememnt, decrement or set the
     * values specifically. For more information, take a look at {@link Value}
     * and {@link ValueType}.
     *
     * @param callback
     *         The callback with the optional error and result.
     * @param identity
     *         Any instance of {@link Identity} to be found in the database.
     * @param column
     *         The column that is to be modified.
     * @param value
     *         The new value that will replace the old one.
     * @throws NullPointerException
     *         If {@code callback == null || identity == null || column ==
     *         null}.
     * @see Identity
     * @see DatabaseKey
     * @see Value
     */
    public final void update(final DatabaseCallback<ResultSet> callback,
                             final Identity identity,
                             final DatabaseKey column,
                             final Value value) {
        notNull(callback, NULL_CALLBACK);

        update(callback, identity, new DatabaseKey[] {column}, value);
    }


    /**
     * Updates a value in the database for the specified sub class of {@link
     * Identity}.  If the value is of either {@link ValueType#GIVE} or {@link
     * ValueType#TAKE}, then use {@link #update(DatabaseCallback, Identity,
     * DatabaseKey, Value)}.
     *
     * @param identity
     *         Any instance of {@link Identity} to be found in the database.
     * @param column
     *         The column that is to be modified.
     * @param value
     *         The new value that will replace the old one.
     * @throws NullPointerException
     *         If {@code column == null || identity == null}.
     * @see Identity
     * @see DatabaseKey
     * @see Value
     */
    public final void update(final Identity identity,
                             final DatabaseKey column,
                             final Object value) {
        update(callbackHandler, identity, column, value);
    }


    /**
     * Updates a value in the database for the specified sub class of {@link
     * Identity}.  If the value is of either {@link ValueType#GIVE} or {@link
     * ValueType#TAKE}, then use {@link #update(DatabaseCallback, Identity,
     * DatabaseKey, Value)}.
     *
     * @param callback
     *         The callback with the optional error and result.
     * @param identity
     *         Any instance of {@link Identity} to be found in the database.
     * @param column
     *         The column that is to be modified.
     * @param value
     *         The new value that will replace the old one.
     * @throws NullPointerException
     *         If {@code callback == null || identity == null || column ==
     *         null}.
     * @see Identity
     * @see DatabaseKey
     * @see Value
     */
    public final void update(final DatabaseCallback<ResultSet> callback,
                             final Identity identity,
                             final DatabaseKey column,
                             final Object value) {
        notNull(callback, NULL_CALLBACK);

        update(callback, identity, new DatabaseKey[] {column}, value);
    }


    /**
     * Updates a value in the database for the specified sub class of {@link
     * Identity}. If the value is of either {@link ValueType#GIVE} or {@link
     * ValueType#TAKE}, then use {@link #update(DatabaseCallback, Identity,
     * DatabaseKey, Value)}.
     *
     * <p>This method, unlike {@link #update(DatabaseCallback, Identity,
     * DatabaseKey, Value)} and {@link #update(DatabaseCallback, Identity,
     * DatabaseKey, Object)} will update multiple columns at once for a single
     * entry.  The size of {@param columns} must correspond to the size of
     * {@param values}.</p>
     *
     * @param identity
     *         Any instance of {@link Identity} to be found in the database.
     * @param columns
     *         The columns that are to be modified.
     * @param values
     *         The new values that will replace the old ones.
     * @throws NullPointerException
     *         If {@code columns == null || identity == null || values ==
     *         null}.
     * @throws IllegalArgumentException
     *         If {@code columns.length != values.length || columns.length == 0
     *         || values.length == 0}.
     * @see Identity
     * @see DatabaseKey
     * @see Value
     */
    public final void update(final Identity identity,
                             final DatabaseKey[] columns,
                             final Object... values) {
        update(callbackHandler, identity, columns, values);
    }


    /**
     * Updates a value in the database for the specified sub class of {@link
     * Identity}. If the value is of either {@link ValueType#GIVE} or {@link
     * ValueType#TAKE}, then use {@link #update(DatabaseCallback, Identity,
     * DatabaseKey, Value)}.
     *
     * <p>This method, unlike {@link #update(DatabaseCallback, Identity,
     * DatabaseKey, Value)} and {@link #update(DatabaseCallback, Identity,
     * DatabaseKey, Object)} will update multiple columns at once for a single
     * entry.  The size of {@param columns} must correspond to the size of
     * {@param values}.</p>
     *
     * @param callback
     *         The callback with the optional error and result.
     * @param identity
     *         Any instance of {@link Identity} to be found in the database.
     * @param columns
     *         The columns that are to be modified.
     * @param values
     *         The new values that will replace the old ones.
     * @throws NullPointerException
     *         If {@code callback == null || identity == null || columns == null
     *         || values == null}.
     * @throws IllegalArgumentException
     *         If {@code columns.length != values.length || columns.length == 0
     *         || values.length == 0}.
     * @see Identity
     * @see DatabaseKey
     * @see Value
     */
    public final void update(final DatabaseCallback<ResultSet> callback,
                             final Identity identity,
                             final DatabaseKey[] columns,
                             final Object... values) {
        notNull(callback, NULL_CALLBACK);
        notNull(identity, NULL_IDENTITY);
        notEmpty(columns, EMPTY_COLUMNS);
        notEmpty(values, EMPTY_VALUES);
        int valength = values.length;
        isTrue(columns.length == valength, COLUMN_VALUE_LENGTH_NO_MATCH);

        Value[] setValues = new Value[valength];
        for (int x = 0; x < valength; ++x) {
            setValues[x] = new Value<>(values[x], ValueType.SET);
        }

        update(callback, identity, columns, setValues);
    }


    /**
     * Updates a value in the database for the specified sub class of {@link
     * Identity}.  {@param value} is used to incrememnt, decrement or set the
     * values specifically. For more information, take a look at {@link Value}
     * and {@link ValueType}.
     *
     * This method, unlike {@link #update(Identity, DatabaseKey, Value)} and
     * {@link #update(Identity, DatabaseKey, Object)} will update multiple
     * columns at once for a single entry. The size of {@param columns} must
     * correspond to the size of {@param values}.
     *
     * @param identity
     *         Any instance of {@link Identity} to be found in the database.
     * @param columns
     *         The columns that are to be modified.
     * @param values
     *         The new values that will replace the old ones.
     * @throws NullPointerException
     *         If {@code identity == null || columns == null || values ==
     *         null}.
     * @throws IllegalArgumentException
     *         If {@code columns.length != values.length || columns.length == 0
     *         || values.length == 0}.
     * @see Identity
     * @see DatabaseKey
     * @see Value
     */
    public void update(final Identity identity,
                       final DatabaseKey[] columns,
                       final Value<?>... values) {
        update(callbackHandler, identity, columns, values);
    }


    /**
     * Updates a value in the database for the specified sub class of {@link
     * Identity}.  {@param value} is used to incrememnt, decrement or set the
     * values specifically. For more information, take a look at {@link Value}
     * and {@link ValueType}.
     *
     * This method, unlike {@link #update(DatabaseCallback, Identity,
     * DatabaseKey, Value)} and {@link #update(DatabaseCallback, Identity,
     * DatabaseKey, Object)} will update multiple columns at once for a single
     * entry. The size of {@param columns} must correspond to the size of
     * {@param values}.
     *
     * @param callback
     *         The callback with the optional error and result.
     * @param identity
     *         Any instance of {@link Identity} to be found in the database.
     * @param columns
     *         The columns that are to be modified.
     * @param values
     *         The new values that will replace the old ones.
     * @throws NullPointerException
     *         If {@code callback == null || identity == null || columns == null
     *         || values == null}.
     * @throws IllegalArgumentException
     *         If {@code columns.length != values.length || columns.length == 0
     *         || values.length == 0}.
     * @see Identity
     * @see DatabaseKey
     * @see Value
     */
    public void update(final DatabaseCallback<ResultSet> callback,
                       final Identity identity,
                       final DatabaseKey[] columns,
                       final Value<?>... values) {
        notNull(callback, NULL_CALLBACK);
        notNull(identity, NULL_IDENTITY);
        notEmpty(columns, EMPTY_COLUMNS);
        notEmpty(values, EMPTY_VALUES);
        int len = columns.length;
        isTrue(len == values.length, COLUMN_VALUE_LENGTH_NO_MATCH);

        Object[] vals = new String[len + 1];
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < len; ++x) {
            sb.append("`");
            sb.append(columns[x].getKey());
            sb.append("` = ");

            Value<?> value = values[x];
            vals[x] = value.toString();
            // I'm not even going to bother explaining what is going on here.
            sb.append(value.isInteger() && !value.getType().equals(ValueType.SET)
                    ? String.format(
                    Locale.ENGLISH,
                    "`%s` %s ?",
                    columns[x].getKey(),
                    value.getType().equals(ValueType.GIVE) ? "+" : "-")
                    : "?"
            );

            if (x != len - 1) {
                sb.append(", ");
            }
        }

        // append UUID to the last element index
        vals[len] = identity.getUuid();

        // execute and wait for callback
        database.update(callback, String.format(
                UPDATE_SET_WHERE,
                this,
                sb,
                identity.getDatabaseKey().getKey()
        ), vals);
    }


    /**
     * Returns a value found in this table from the criteria specified in the
     * parameters.
     *
     * @param callback
     *         The callback with the optional error and result.
     * @param identity
     *         Any instance of {@link Identity} that holds this data.
     * @param column
     *         The column where this value is located.
     * @throws NullPointerException
     *         If {@code callback == null || identity == null || column ==
     *         null}.
     * @see Identity
     * @see DatabaseKey
     */
    public final void get(final DatabaseCallback<Object> callback,
                          final Identity identity,
                          final DatabaseKey column) {
        get(
                (result, t) -> callback.onResult(result.get(0), t),
                identity,
                new DatabaseKey[] {column}
        );
    }


    /**
     * Returns the values found in this table from the criteria specified in the
     * parameters.
     *
     * <p>Looks up multiple columns at once - {@code List<Object>} returned will
     * be the same size as {@param columns} length.</p>
     *
     * @param callback
     *         The callback with the optional error and result.
     * @param identity
     *         Any instance of {@link Identity} that holds this data.
     * @param columns
     *         The columns where these values are located.
     * @throws NullPointerException
     *         If {@code callback == null || identity == null || || columns ==
     *         null}.
     * @throws IllegalArgumentException
     *         If {@code columns.length == 0}.
     * @see DatabaseKey
     * @see Identity
     */
    public void get(final DatabaseCallback<List<Object>> callback,
                    final Identity identity,
                    final DatabaseKey... columns) {
        notNull(callback, NULL_CALLBACK);
        notNull(identity, NULL_IDENTITY);
        notNull(columns, NULL_COLUMNS);
        isTrue(columns.length > 0, EMPTY_COLUMNS);

        final int len = columns.length;

        database.query((result, t) -> {
            List<Object> values = new EnhancedList<>(len);
            try {
                if (result != null && result.next()) {
                    int x = 0;
                    while (x < len) {
                        values.set(++x - 1, result.getObject(x));
                    }
                }
            } catch (final Throwable error) {
                t = error;
            } finally {
                callback.onResult(values, t);
            }
        }, String.format(
                SELECT_WHERE,
                new EnhancedList<String>(len) {{
                    addAll(Stream.of(columns)
                            .map(column -> String.format("`%s`", column.getKey()))
                            .collect(Collectors.toList())
                    );
                    // In the form of "x, y, z" as opposed to "[x,y,z]"
                }}.toReadableList(", "),
                this,
                identity.getDatabaseKey().getKey()
        ), identity.getUuid());
    }


    /**
     * Returns a {@link ResultSet} filled with data obtained from the table that
     * holds the entire entry under the player's {@code UUID}.
     *
     * @param callback
     *         The callback with the optional error and result.
     * @param identity
     *         Any instance of {@link Identity} that holds the data of the
     *         table.
     * @throws NullPointerException
     *         If {@code identity == null || callback == null}.
     * @see Identity
     * @see SQLDatabase
     */
    public void get(final DatabaseCallback<ResultSet> callback,
                    final Identity identity) {
        notNull(callback, NULL_CALLBACK);
        notNull(identity, NULL_IDENTITY);

        database.query(callback, String.format(
                SELECT_ALL_WHERE,
                this,
                identity.getDatabaseKey().getKey()
        ), identity.getUuid());
    }


    /**
     * Returns all rows (entries) that were found in the entire table, without
     * limiting on providing search criteria.
     *
     * @param callback
     *         The callback with the optional error and result
     * @throws NullPointerException
     *         If {@code callback == null}.
     * @see SQLDatabase
     */
    public void getAll(final DatabaseCallback<ResultSet> callback) {
        notNull(callback, NULL_CALLBACK);
        database.query(callback, String.format(SELECT_ALL, this));
    }


    /**
     * Returns the amount of rows (entries) that were found in the entire
     * table.
     *
     * @param callback
     *         The callback with the optional error and result
     * @throws NullPointerException
     *         If {@code callback == null}.
     * @see SQLDatabase
     */
    public void getTotalRows(final DatabaseCallback<Integer> callback) {
        notNull(callback, NULL_CALLBACK);
        database.query((result, t) -> {
            Integer rows = null;
            Throwable error = t;
            if (error != null) {
                try {
                    if (result.next()) {
                        rows = result.getInt(1);
                    }
                } catch (SQLException e) {
                    error = e;
                }
            }
            callback.onResult(rows, error);
        }, String.format(SELECT_COUNT, this));
    }


    /**
     * @return The {@code SQL} database associated with this table
     */
    public final SQLDatabase getDatabase() {
        return database;
    }


    /**
     * @return The name of the table
     */
    public final String getName() {
        return table;
    }


    @Override
    public final String toString() {
        return table;
    }
}