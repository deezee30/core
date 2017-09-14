/*
 * MaulssLib
 * 
 * Created on 07 February 2015 at 8:24 PM.
 */

package com.maulss.core.bukkit.world.region.type;

import com.google.common.collect.ImmutableList;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.bukkit.world.region.Region;
import com.maulss.core.bukkit.world.region.RegionBoundsException;
import com.maulss.core.bukkit.world.region.Regions;
import com.maulss.core.bukkit.world.schematic.CuboidSchematic;
import com.maulss.core.bukkit.world.schematic.Schematic;
import com.maulss.core.bukkit.world.schematic.SchematicData;
import com.maulss.core.bukkit.world.schematic.SchematicType;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.collect.EnhancedMap;
import com.maulss.core.math.Vector3D;
import com.maulss.core.math.Vector3DList;
import com.maulss.core.util.MathUtil;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Map;

public class CuboidRegion extends Region {

    private static final long serialVersionUID = -1670751903460496963L;

    private final Vector3D min, max;

    // do not serialize these
    private transient Vector3DList points;
    private transient int volume, length, width, height;

    public CuboidRegion(final String world,
                        final Vector3D min,
                        final Vector3D max) {
        super(world);

		this.min = Validate.notNull(min, "The min point can not be null").floor();
		this.max = Validate.notNull(max, "The max point can not be null").floor();

        calculate();
    }

    @Override
    public void calculate() {
        points = new Vector3DList();

        // find points in region
        for (int x = getMinX(); x <= getMaxX(); x++)
            for (int y = getMinY(); y <= getMaxY(); y++)
                for (int z = getMinZ(); z <= getMaxZ(); z++)
                    points.add(new Vector3D(x, y, z));

        // calculate dimensions
        length  = MathUtil.floor(getMaxX() - getMinX() + 1);
        height  = MathUtil.floor(getMaxY() - getMinY() + 1);
        width   = MathUtil.floor(getMaxZ() - getMinZ() + 1);
        volume  = width * height * length;

        CoreLogger.debug("CUBOID: Measured volume: %s; Calculated volume: %s", points.size(), volume);
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
    public int getVolume() {
        return volume;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * @deprecated Use {@link #getLength()}
     */
    @Deprecated
    public int getDepth() {
        return length;
    }

    public Schematic toSchematic(final String name,
                                 final SchematicType schemType) {
        World world = Bukkit.getWorld(this.world);
        Validate.notNull(world, "World " + this.world + " hasn't been loaded");
        Validate.notNull(name);
        Validate.notNull(schemType);

        short[] blocks = new short[points.size()];
        byte[] blockData = new byte[points.size()];
        int x = 0;
        for (Vector3D point : points) {
            Block block = world.getBlockAt(
                    point.getFloorX(),
                    point.getFloorY(),
                    point.getFloorZ()
            );

            blocks[x] = (byte) block.getTypeId();
            blockData[x] = block.getData();
            x++;
        }

        SchematicData data = new SchematicData(
                blocks,
                blockData,
                new Vector3D(width, length, height)
        );

        return new CuboidSchematic(name, schemType, data);
    }

    @Override
    public boolean contains(Vector3D vector) {
        return vector.getX() >= minX
                && vector.getX() <= maxX
                && vector.getY() >= minY
                && vector.getY() <= maxY
                && vector.getZ() >= minZ
                && vector.getZ() <= maxZ;
    }

    @Override
    public EnhancedList<Vector3D> getWalls() {
        EnhancedList<Vector3D> points = new EnhancedList<>();

        for (Vector3D vector : this) {
            int x = vector.getFloorX();
            int y = vector.getFloorY();
            int z = vector.getFloorZ();

            if (x == minX || x == maxX ||
                    y == minY || y == maxY ||
                    z == minZ || z == maxZ) {
                points.add(vector);
            }
        }

        return points;
    }

    @Override
    public EnhancedList<Vector3D> getEdges() {
        EnhancedList<Vector3D> points = new EnhancedList<>();

        for (Vector3D vector : this) {
            int x = vector.getFloorX();
            int y = vector.getFloorY();
            int z = vector.getFloorZ();

            boolean edge = false;

            if ((x == minX || x == maxX) && (y == minY || y == maxY)) edge = true;
            if ((z == minZ || z == maxZ) && (y == minY || y == maxY)) edge = true;
            if ((x == minX || x == maxX) && (z == minZ || z == maxZ)) edge = true;

            points.addIf(edge, vector);
        }

        return points;
    }

    @Override
    public ImmutableList<Vector3D> getPoints() {
        return points.getImmutableElements();
    }

    @Override
    public RegionType getType() {
        return RegionType.CUBOID;
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
        map.put("min", min);
        map.put("max", max);
        map.putIf(hasPriority(), "priority", getPriority().get());
        return map.getImmutableEntries();
    }
}