/*
 * Part of core.
 * 
 * Created on 17 June 2017 at 1:15 AM.
 */

package com.maulss.core.bukkit.world.region.type;

import com.maulss.core.bukkit.world.region.Region;

public enum RegionType {

    CUBOID(CuboidRegion.class),
    SPHERICAL(SphericalRegion.class),
    CYLINDRICAL(CylindricalRegion.class),
    PYRAMID(PyramidalRegion.class),
    POLYGONAL(null),
    CUSTOM(null);

    private final Class<? extends Region> defaultClass;

    RegionType(Class<? extends Region> defaultClass) {
        this.defaultClass = defaultClass;
    }

    public Class<? extends Region> getDefaultClass() {
        return defaultClass;
    }

    @Override
    public String toString() {
        return name();
    }
}