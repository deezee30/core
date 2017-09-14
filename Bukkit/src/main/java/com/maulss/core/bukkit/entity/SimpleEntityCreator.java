/*
 * Part of core.
 * 
 * Created on 23 July 2017 at 7:46 PM.
 */

package com.maulss.core.bukkit.entity;

import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.maulss.core.math.Vector3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import static org.apache.commons.lang3.Validate.notNull;

public class SimpleEntityCreator implements EntityCreator {

    // Store new entity constructor
    private ConstructorAccessor newEntity;

    public SimpleEntityCreator(final String name) {
        notNull(name, "name");
        newEntity = EntityUtil.newEntity(MinecraftReflection.getMinecraftClass(name));
    }

    public SimpleEntityCreator(final Class<?> entityClass) {
        notNull(entityClass, "entityClass");
        if (!MinecraftReflection.isMinecraftEntity(entityClass))
            throw new IllegalArgumentException(entityClass.getName() + " is not a NMS entity");
        newEntity = EntityUtil.newEntity(entityClass);
    }

    @Override
    public Entity create(final Location location) {
        notNull(location, "location");

        // create new entity
        Object obj = newEntity.invoke(
                EntityCreator.unwrapWorld(location.getWorld()),
                location.getX(),
                location.getY(),
                location.getZ()
        );

        return (Entity) MinecraftReflection.getBukkitEntity(obj);
    }
}