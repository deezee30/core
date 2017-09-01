/*
 * rv_core
 * 
 * Created on 03 June 2017 at 10:17 PM.
 */

package com.maulss.core.database;

import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.Optional;

/**
 * The interface key type.
 */
public interface DatabaseKey {

    DatabaseKey UUID = create("_id");

    /**
     * Gets key.
     *
     * @return the key
     */
    String getKey();

    /**
     * Gets default.
     *
     * @return the default
     */
    Optional<Object> getDefault();

    /**
     * Append map.
     *
     * @param map the map
     * @return the map
     */
    default Map<DatabaseKey, Object> append(final Map<DatabaseKey, Object> map) {
        Validate.notNull(map);
        return append(map, getDefault().isPresent() ? getDefault().get() : null);
    }

    /**
     * Append map.
     *
     * @param map the map
     * @param def the def
     * @return the map
     */
    default Map<DatabaseKey, Object> append(final Map<DatabaseKey, Object> map,
                                            final Object def) {
        Validate.notNull(map);

        map.put(this, def);
        return map;
    }

    /**
     * Create stat type.
     *
     * @param stat the stat
     * @return the stat type
     */
    static DatabaseKey create(final String stat) {
        return create(stat, null);
    }

    /**
     * Create stat type.
     *
     * @param stat the stat
     * @param def  the def
     * @return the stat type
     */
    static DatabaseKey create(final String stat,
                              final Object def) {
        Validate.notNull(stat);

        return new DatabaseKey() {

            @Override
            public String getKey() {
                return stat;
            }

            @Override
            public Optional<Object> getDefault() {
                return Optional.ofNullable(def);
            }

            @Override
            public String toString() {
                return stat;
            }
        };
    }
}
