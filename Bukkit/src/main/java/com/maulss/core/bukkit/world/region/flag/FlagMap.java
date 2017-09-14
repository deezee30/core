/*
 * Part of core.
 * 
 * Created on 21 June 2017 at 12:43 AM.
 */

package com.maulss.core.bukkit.world.region.flag;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.maulss.core.bukkit.world.region.Regions;
import com.maulss.core.collect.EnhancedMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;
import java.util.Optional;

public class FlagMap extends EnhancedMap<Flag, Boolean> implements ConfigurationSerializable {

    public FlagMap() {}

    public FlagMap(Flag flag, boolean bool) {
        this(1);
        put(flag, bool);
    }

    public FlagMap(final int initialCapacity,
                   final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public FlagMap(final int initialCapacity) {
        super(initialCapacity);
    }

    public FlagMap(final Map<Flag, Boolean> m) {
        super(m);
    }

    public FlagMap(final int initialCapacity,
                   final float loadFactor,
                   final boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public boolean isAllowed(final Flag flag) {
        Boolean val = get(flag);
        return val == null ? true : val;
    }

    @Override
    public Gson getGson() {
        return Regions.REGION_GSON;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        for (Map.Entry<Flag, Boolean> entry : entrySet())
            map.put(entry.getKey().flagName, entry.getValue());
        return map;
    }

    public static FlagMap deserialize(final Map<String, Object> data) {
        FlagMap map = new FlagMap();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Optional<Flag> flag = Flag.from(entry.getKey());
            map.putIf(flag.isPresent(), flag.get(), (boolean) entry.getValue());
        }
        return map;
    }

    public static FlagMap fromJson(final String json) {
        return Regions.REGION_GSON.fromJson(json, FlagMap.class);
    }
}