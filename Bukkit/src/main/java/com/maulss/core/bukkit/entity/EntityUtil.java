/*
 * Part of core.
 * 
 * Created on 23 July 2017 at 9:33 PM.
 */

package com.maulss.core.bukkit.entity;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;

public class EntityUtil {

    // NMS world class
    private static final Class<?> NMS_WORLD = MinecraftReflection.getNmsWorldClass();

    private EntityUtil() {}

    public static ConstructorAccessor newEntity(final Class<?> entityClass) {
        return Accessors.getConstructorAccessor(
                entityClass, NMS_WORLD, double.class,
                double.class, double.class
        );
    }
}