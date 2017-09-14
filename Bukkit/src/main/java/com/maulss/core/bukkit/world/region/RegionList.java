/*
 * Part of core.
 * 
 * Created on 19 June 2017 at 12:47 PM.
 */

package com.maulss.core.bukkit.world.region;

import com.google.gson.Gson;
import com.maulss.core.collect.EnhancedList;

import java.util.Collection;

public class RegionList extends EnhancedList<Region> {

    public RegionList() {}

    public RegionList(final int initialCapacity) {
        super(initialCapacity);
    }

    public RegionList(final Region... elements) {
        super(elements);
    }

    public RegionList(final Collection<Region> c) {
        super(c);
    }

    @Override
    public Gson getGson() {
        return Regions.REGION_GSON;
    }

    public static RegionList fromJson(final String json) {
        RegionList list = Regions.REGION_GSON.fromJson(json, RegionList.class);
        list.forEach(Region::calculate);
        return list;
    }
}