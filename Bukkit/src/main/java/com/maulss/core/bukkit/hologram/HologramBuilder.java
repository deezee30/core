/*
 * Part of core.
 * 
 * Created on 21 July 2017 at 2:44 PM.
 */

package com.maulss.core.bukkit.hologram;

import com.google.common.collect.ImmutableList;
import com.maulss.core.bukkit.hologram.line.HologramLine;
import com.maulss.core.bukkit.hologram.line.NullHologramLine;
import com.maulss.core.bukkit.hologram.line.TextualLine;
import com.maulss.core.bukkit.world.Position;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.math.Vector3D;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.noNullElements;
import static org.apache.commons.lang3.Validate.notNull;

public final class HologramBuilder implements Serializable {

    private static final long serialVersionUID = 5221090406061326185L;

    private String world;
    private Vector3D location;
    private EnhancedList<HologramLine> lines;
    private Optional<Entity> mount = Optional.empty();
    private Optional<EnhancedList<HologramViewer>> visibleFor = Optional.empty();
    private Optional<Double> radius = Optional.empty();

    public HologramBuilder in(final String world) {
        notNull(world);
        in(Bukkit.getWorld(world));
        return this;
    }

    public HologramBuilder in(final World world) {
        this.world = notNull(world, "world doesn't exist").getName();
        return this;
    }

    public HologramBuilder at(final Vector3D location) {
        this.location = notNull(location);
        return this;
    }

    public HologramBuilder at(final Location location) {
        notNull(location);
        in(location.getWorld());
        return at(new Vector3D(
                location.getX(),
                location.getY(),
                location.getZ()
        ));
    }

    public HologramBuilder at(final Position position) {
        notNull(position);
        return at(position.toLocation());
    }

    public HologramBuilder displaying(final Object... lines) {
        HologramLine[] hLines = new HologramLine[lines.length];
        for (int i = 0; i < lines.length; i++) {
            Object line = lines[i];
            if (line == null || line.toString().isEmpty()) {
                hLines[i] = new NullHologramLine();
            } else if (line instanceof HologramLine) {
                hLines[i] = (HologramLine) line;
            } else {
                hLines[i] = new TextualLine(line.toString());
            }
        }

        return displaying(hLines);
    }

    public HologramBuilder displaying(final HologramLine... lines) {
        return displaying(ImmutableList.copyOf(lines));
    }

    public HologramBuilder displaying(final Collection<HologramLine> lines) {
        this.lines = new EnhancedList<>(noNullElements(lines));
        return this;
    }

    public HologramBuilder mount(final Entity entity) {
        this.mount = Optional.of(notNull(entity));
        return this;
    }

    public HologramBuilder visibleFor(final UUID... viewers) {
        ImmutableList.Builder<HologramViewer> builder = new ImmutableList.Builder<>();
        for (UUID viewer : viewers) {
            if (viewer != null) builder.add(HologramViewer.from(viewer));
        }

        return visibleFor(builder.build());
    }

    public HologramBuilder visibleFor(final HologramViewer... viewers) {
        EnhancedList<HologramViewer> visibleFor = new EnhancedList<>(viewers.length);
        for (HologramViewer viewer : viewers) {
            visibleFor.addIf(viewer != null, viewer.copy());
        }
        this.visibleFor = Optional.of(visibleFor);
        return this;
    }

    public HologramBuilder visibleFor(final Collection<HologramViewer> viewers) {
        EnhancedList<HologramViewer> visibleFor = new EnhancedList<>(viewers.size());
        for (HologramViewer viewer : viewers) {
            visibleFor.addIf(viewer != null, viewer.copy());
        }
        this.visibleFor = Optional.of(visibleFor);
        return this;
    }

    public HologramBuilder within(final double radius) {
        this.radius = Optional.of(radius);
        return this;
    }

    public Hologram build() {
        if (Bukkit.getWorld(world) == null)
            throw new IllegalArgumentException("Provided world doesn't exist or has not been set for hologram");
        if (location == null && !mount.isPresent())
            throw new IllegalArgumentException("Location hasn't been set for hologram");
        if (location != null && mount.isPresent())
            throw new IllegalArgumentException("Both mounting and location have been provided");
        if (lines == null)
            throw new IllegalArgumentException("Lines haven't been set for hologram");
        for (HologramLine line : lines) {
            if (HologramLine.LINE_OPACITY_THRESHOLD * line.getOpacity() % 1 != 0) {
                throw new IllegalArgumentException(
                        "Incorrect opacity for line '"
                        + line.getText()
                        + "': "
                        + line.getOpacity()
                );
            }
        }

        if (mount.isPresent()) {
            throw new UnsupportedOperationException("Mounting not yet supported");
            // return new MountedHologram(new Position(world, location), radius, maxWidth, visibleFor);
        } else {
            return new StaticHologram(
                    new Position(world, location),
                    lines,
                    radius,
                    visibleFor
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HologramBuilder that = (HologramBuilder) o;

        return new EqualsBuilder()
                .append(world, that.world)
                .append(location, that.location)
                .append(lines, that.lines)
                .append(visibleFor, that.visibleFor)
                .append(radius, that.radius)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(world)
                .append(location)
                .append(lines)
                .append(visibleFor)
                .append(radius)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("world", world)
                .append("location", location)
                .append("lines", lines)
                .append("visibleFor", visibleFor)
                .append("radius", radius)
                .toString();
    }
}