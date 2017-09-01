/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database;

public enum ValueType {

    /** Sets the exact value in the database. */
    SET,

    /** Adds a specific amount to the column in the database. */
    GIVE,

    /** Takes a specific amount from the column in the database. */
    TAKE
}