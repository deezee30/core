/*
 * Part of core.
 * Made on 05/08/2017
 */

package com.maulss.core.database.sql;

import com.maulss.core.database.DatabaseKey;
import com.mongodb.annotations.Beta;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;
import java.util.Optional;

public class SQLKey implements DatabaseKey {

    // SQL column name
    private final String
            column;

    // Type of SQL column
    private final SQLKeyType
            type;

    private boolean
            // NOT NULL
            notNull     = false,
            // AUTO_INCREMENT
            inc         = false,
            // KEY
            key         = false,
            // UNIQUE
            unique      = false,
            // UNSIGNED
            unsigned    = false;

    @Nullable private Object
            // DEFAULT X
            def         = null,
            // extra wildcards
            extra       = null;

    public SQLKey(final String column,
                  final SQLKeyType type) {
        this.column = Validate.notNull(column, "column");
        this.type   = Validate.notNull(type, "type");
    }

    /**
     * Adds a 'NOT NULL' wildcard to the data type.
     *
     * @return This instance.
     */
    public SQLKey notNull() {
        notNull = true;
        return this;
    }

    public boolean isNotNull() {
        return notNull;
    }

    /**
     * Adds a 'AUTO_INCREMENT' wildcard to the data type.
     *
     * @return This instance.
     */
    public SQLKey autoIncrement() {
        inc = true;
        return this;
    }

    public boolean isInc() {
        return inc;
    }

    /**
     * Adds a 'KEY' wildcard to the data type.
     *
     * @return This instance.
     */
    public SQLKey key() {
        key = true;
        return this;
    }

    public boolean isKey() {
        return key;
    }

    /**
     * Adds a 'UNIQUE' wildcard to the data type.
     *
     * @return This instance.
     */
    public SQLKey unique() {
        unique = true;
        return this;
    }

    public boolean isUnique() {
        return unique;
    }

    /**
     * Adds an 'UNSIGNED' wildcard to the data type. This can ONLY be used for integers.
     *
     * @return This instance;
     */
    @Beta
    public SQLKey unsigned() {
        unsigned = true;
        return this;
    }

    @Beta
    public boolean isUnsigned() {
        return unsigned;
    }

    @Override
    public String getKey() {
        return column;
    }

    public SQLKeyType getType() {
        return type;
    }

    /**
     * Add 'DEFAULT X' wildcard to the data type.
     *
     * @param 	obj The default value for the column.
     * @return 	This instance.
     */
    public SQLKey setDefault(@Nullable final Object obj) {
        def = obj;
        return this;
    }

    @Override
    public Optional<Object> getDefault() {
        return Optional.ofNullable(def);
    }

    /**
     * Adds extra wildcard(s) to the data type.
     *
     * @param 	extra The extra wildcards for the column.
     * @return 	This instance.
     */
    @Beta
    public SQLKey extra(@Nullable final Object extra) {
        this.extra = extra;
        return this;
    }

    @Beta
    public Optional<Object> getExtra() {
        return Optional.ofNullable(extra);
    }

    /**
     * Compiles the provided wildcards, the data types and other settings and
     * returns it as a SQL query to be used for inserting columns into tables.
     *
     * @return The compiled String that contains the data of the column.
     */
    public String compileWildcards() {
        StringBuilder string = new StringBuilder("`");
        string.append(column);
        string.append("` ");
        string.append(type.get());

        if (notNull) string.append(" NOT NULL");
        if (inc) string.append(" AUTO_INCREMENT");
        if (key) string.append(" KEY");
        if (unique) string.append(" UNIQUE");
        if (unsigned) string.append(" UNSIGNED");
        if (def != null) string.append(" DEFAULT ").append(def);
        if (extra != null) string.append(extra);

        return string.toString();
    }
}