/*
 * Part of core.
 * Made on 03/09/2017
 */

package com.maulss.core.bukkit.world;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.maulss.core.math.Vector3D;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class Position extends Vector3D implements ConfigurationSerializable {

    protected final String world;
    protected final float yaw;
    protected final float pitch;

    public Position(final String world,
                    final Vector3D position) {
        this(world, position.getX(), position.getY(), position.getZ());
    }

    public Position(final Location location) {
        this(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    public Position(final String world,
                    final double x,
                    final double y,
                    final double z) {
        this(world, x, y, z, 0f, 0f);
    }

    public Position(final String world,
                    final double x,
                    final double y,
                    final double z,
                    final float yaw,
                    final float pitch) {
        super(x, y, z);
        this.world = Validate.notEmpty(world, "world");
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getWorld() {
        return world;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    @Override
    public Position add(Vector3D vec) {
        return (Position) super.add(vec);
    }

    @Override
    public Position add(double scalar) {
        return (Position) super.add(scalar);
    }

    @Override
    public Position subtract(Vector3D vec) {
        return (Position) super.subtract(vec);
    }

    @Override
    public Position subtract(double scalar) {
        return (Position) super.subtract(scalar);
    }

    @Override
    public Position multiply(Vector3D vec) {
        return (Position) super.multiply(vec);
    }

    @Override
    public Position multiply(double scalar) {
        return (Position) super.multiply(scalar);
    }

    @Override
    public Position divide(Vector3D vec) {
        return (Position) super.divide(vec);
    }

    @Override
    public Position divide(double scalar) {
        return (Position) super.divide(scalar);
    }

    @Override
    public Position copy(Vector3D vec) {
        return (Position) super.copy(vec);
    }

    @Override
    public Position floor() {
        return (Position) super.floor();
    }

    @Override
    public Position getMidpoint(Vector3D other) {
        return (Position) super.getMidpoint(other);
    }

    @Override
    public Position crossProduct(Vector3D o) {
        return (Position) super.crossProduct(o);
    }

    @Override
    public Position normalize() {
        return (Position) super.normalize();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(6);
        map.put("world", world);
        map.putAll(super.serialize());
        if (yaw != 0f)
            map.put("yaw", yaw);
        if (pitch != 0f)
            map.put("pitch", pitch);
        return map;
    }

    public static Position deserialize(final Map<String, Object> data) {
        String world = (String) data.get("world");
        double x = (double) data.get("x");
        double y = (double) data.get("y");
        double z = (double) data.get("z");
        float yaw = 0f;
        float pitch = 0f;
        if (data.containsKey("yaw"))
            yaw = (float) data.get("yaw");
        if (data.containsKey("pitch"))
            pitch = (float) data.get("pitch");

        return new Position(world, x, y, z, yaw, pitch);
    }

    @Override
    public Position clone() {
        return new Position(world, x, y, z, yaw, pitch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Position)) return false;

        Position position = (Position) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(yaw, position.yaw)
                .append(pitch, position.pitch)
                .append(world, position.world)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(world)
                .append(yaw)
                .append(pitch)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Position{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();

        obj.addProperty("world", world);
        obj.addProperty("x", x);
        obj.addProperty("y", y);
        obj.addProperty("z", z);
        if (yaw != 0f)
            obj.addProperty("yaw", yaw);
        if (pitch != 0f)
            obj.addProperty("pitch", pitch);

        return obj;
    }
}