/*
 * Part of core.
 * Made on 05/08/2017
 */

package com.maulss.core.database.sql;

public interface SQLKeyType {

    /**
     * Compiles the provided key type with its optional attributes and returns
     * it as part of a query for creating columns in tables in SQL databases.
     *
     * @return A part of a SQL query.
     */
    String get();
}