/*
 * Part of core.
 * 
 * Created on 23 July 2017 at 7:41 PM.
 */

package com.maulss.core.bukkit.entity;

import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.maulss.core.bukkit.world.Position;
import com.maulss.core.collect.EnhancedMap;
import com.maulss.core.math.Vector3D;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public interface EntityCreator {

    // Cache NMS worlds to prevent extra reflection lookups and unwraps
    EnhancedMap<String, Object> NMS_WORLD_CACHE = new EnhancedMap<>();

    Entity create(final Location location);

    default Entity create(final Position location) {
        Validate.notNull(location, "location");
        return create(location.toLocation());
    }

    static Object unwrapWorld(final World world) {
        String name = world.getName();
        Object nmsWorld = NMS_WORLD_CACHE.get(name);

        if (nmsWorld == null) {
            // Bukkit world -> NMS world
            nmsWorld = BukkitUnwrapper.getInstance().unwrapItem(world);
            // add to cache if absent
            NMS_WORLD_CACHE.put(name, nmsWorld);
        }

        return nmsWorld;
    }
}