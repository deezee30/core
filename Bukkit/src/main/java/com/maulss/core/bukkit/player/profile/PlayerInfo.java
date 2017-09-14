/*
 * Part of core.
 * Made on 04/09/2017
 */

package com.maulss.core.bukkit.player.profile;

import com.maulss.core.database.DatabaseKey;

import java.util.Optional;

public class PlayerInfo implements DatabaseKey {

    public static final DatabaseKey
            NAME = new PlayerInfo("name");

    private final String key;
    private final Object def;

    public PlayerInfo(final String key) {
        this(key, null);
    }

    public PlayerInfo(final String key,
                      final Object def) {
        this.key = key;
        this.def = def;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Optional<Object> getDefault() {
        return Optional.ofNullable(def);
    }
}