/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database;

import com.maulss.core.Logger;

import java.io.Serializable;
import java.util.UUID;

/**
 * Resembles a database object or document that occupies the space
 * within a database (of any kind).
 *
 * This can be used for anything varying from players to anything else as an entry.
 */
public interface Identity extends Serializable {

    /**
     * @return the unique identifier of this identity that
     * will be used as a primary key in the database
     */
    UUID getUuid();

    /**
     * @return the default stat type for {@link #getUuid()}
     */
    default DatabaseKey getDatabaseKey() {
        return DatabaseKey.UUID;
    }

    /**
     * @return Logger used to track progress with database-related things
     */
    Logger getLogger();
}