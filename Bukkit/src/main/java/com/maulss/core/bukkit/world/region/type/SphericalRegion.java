/*
 * MaulssLib
 * 
 * Created on 07 February 2015 at 9:47 PM.
 */

package com.maulss.core.bukkit.world.region.type;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.world.region.Region;
import com.maulss.core.bukkit.world.region.RegionBoundsException;
import com.maulss.core.bukkit.world.region.Regions;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.collect.EnhancedMap;
import com.maulss.core.math.Vector3D;
import com.maulss.core.math.Vector3DList;
import com.maulss.core.util.MathUtil;
import org.apache.commons.lang3.Validate;

import java.util.Map;

@Beta
public class SphericalRegion extends Region {

    private static final long serialVersionUID = -5576209688378810728L;

    private final Vector3D center;
    private final int radius;

    // do not serialize these
    private transient Vector3DList points;
    private transient int volume;
    private transient Vector3D min, max;

    public SphericalRegion(String world,
                           Vector3D center,
                           int radius) {
        super(world);
        this.center = Validate.notNull(center, "The center point can not be null").floor();
        this.radius = Math.abs(radius);

        calculate();
    }

    @Override
    public void calculate() {
        points = new Vector3DList();

        int xCoord = (int) center.getX();
        int yCoord = (int) center.getY();
        int zCoord = (int) center.getZ();

        // find points in region
        for (int x = -radius; x <= radius; x++)
            for (int z = -radius; z <= radius; z++)
                for (int y = -radius; y <= radius; y++)
                    points.add(new Vector3D(xCoord + x, yCoord + y, zCoord + z));

        // calculate dimensions
        volume  = MathUtil.round(4 * Math.PI * Math.pow(radius, 3) / 3);
        min     = center.clone().subtract(radius);
        max     = center.clone().add(radius);

        Core.get().debug("SPHERE: Measured volume: %s; Calculated volume: %s", points.size(), volume);
    }

    public Vector3D getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public Vector3D getMin() {
        return min;
    }

    @Override
    public Vector3D getMax() {
        return max;
    }

    @Override
    public boolean contains(Vector3D vector) {
        return center.distanceSquared(vector) <= Math.pow(radius, 2);
    }

    @Override
    public EnhancedList<Vector3D> getWalls() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnhancedList<Vector3D> getEdges() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableList<Vector3D> getPoints() {
        return points.getImmutableElements();
    }

    @Override
    public RegionType getType() {
        return RegionType.SPHERICAL;
    }

    @Override
    public Region joinWith(Region other) throws RegionBoundsException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> serialize() {
        EnhancedMap<String, Object> map = new EnhancedMap<>();
        map.put(Regions.TYPE_META, getType());
        map.put("world", getWorld());
        map.put("center", center);
        map.put("radius", radius);
        map.putIf(hasPriority(), "priority", getPriority().get());
        return map.getImmutableEntries();
    }
}