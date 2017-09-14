/*
 * Part of core.
 * 
 * Created on 21 July 2017 at 2:47 PM.
 */

package com.maulss.core.bukkit.hologram;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.hologram.line.HologramLine;
import com.maulss.core.bukkit.hologram.line.NullHologramLine;
import com.maulss.core.bukkit.hologram.line.TextualLine;
import com.maulss.core.bukkit.player.profile.CoreProfile;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.world.Position;
import com.maulss.core.collect.EnhancedList;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notNull;

public abstract class Hologram implements Serializable, Iterable<HologramViewer> {

    private final Position position;
    private final Optional<Double> radius;
    private Optional<EnhancedList<HologramViewer>> visibleFor = Optional.empty();

    protected Hologram(final Position position,
                       final Optional<Double> radius,
                       final Optional<EnhancedList<HologramViewer>> visibleFor) {
        this.position   = position.clone();
        this.radius     = radius;
        this.visibleFor = visibleFor;
    }

    public abstract List<HologramLine> getLines();

    public void remove(final int index) {
        remove(getLines().get(index));
    }

    public abstract void remove(final HologramLine line);

    public abstract void add(final int index,
                             final HologramLine line);

    public void add(final HologramLine line) {
        add(getLines().size(), line);
    }

    public HologramLine add(final String text) {
        HologramLine line = text == null
                ? new NullHologramLine()
                : new TextualLine(text);
        add(line);
        return line;
    }

    public abstract void replace(final HologramLine previous,
                                 final HologramLine newLine);

    public final Hologram activate() {
        EnhancedList<CorePlayer> players = visibleFor
                .<EnhancedList<CorePlayer>>map(hologramViewers -> new EnhancedList<>(hologramViewers.size()))
                .orElse(null);

        if (players != null) {
            for (HologramViewer viewer : this) {
                Optional<CorePlayer> player = viewer.getHandle();
                players.addIf(player.isPresent(), player.get());
            }
        }

        double yOffset = 0d;
        for (HologramLine line : getLines()) {
            yOffset += line.getHeight();
            line.spawn(position, yOffset, players);
        }

        return this;
    }

    public void destroy() {
        getLines().forEach(HologramLine::destroy);
    }

    public void destroy(final CorePlayer... players) {
        for (HologramLine line : getLines()) {
            line.destroy(players);
        }
    }

    private int[] getEntityIdArray() {
        List<HologramLine> lines = getLines();
        int[] idArr = new int[lines.size()];
        for (int x = 0; x < lines.size(); ++x) {
            Entity ent = lines.get(x).getEntity();
            if (ent != null) idArr[x] = ent.getEntityId();
        }
        return idArr;
    }

    private CorePlayer findPlayer(final HologramViewer viewer) {
        CorePlayer player = null;

        // find CorePlayer instance
        if (viewer instanceof CorePlayer) {
            player = (CorePlayer) viewer;
        } else {
            Optional<CorePlayer> optional = viewer.getHandle();
            // only send packets to online players
            if (optional.isPresent()) {
                player = optional.get();
            }
        }

        return player;
    }

    public void add(final HologramViewer viewer) {
        notNull(viewer);
        if (hasVisibleFor()) {
            visibleFor.get().add(viewer.copy());
        } else {
            visibleFor = Optional.of(new EnhancedList<>(viewer));
        }
    }

    public Position getPosition() {
        return position;
    }

    public final boolean hasRadius() {
        return radius.isPresent();
    }

    public final Double getRadius() {
        return radius.orElse(null);
    }

    public final boolean hasVisibleFor() {
        return visibleFor.isPresent();
    }

    public final EnhancedList<HologramViewer> getVisibleFor() {
        return visibleFor.orElse(null);
    }

    @Override
    public final Iterator<HologramViewer> iterator() {
        return hasVisibleFor()
                ? visibleFor.get().iterator()
                : CoreProfile.PLAYER_MANAGER
                        .getOnlinePlayers()
                        .stream()
                        .map(HologramViewer::copy)
                        .collect(Collectors.toList())
                        .iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Hologram hologram = (Hologram) o;

        return new EqualsBuilder()
                .append(position,   hologram.position)
                .append(getLines(), hologram.getLines())
                .append(radius,     hologram.radius)
                .append(visibleFor, hologram.visibleFor)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(position)
                .append(getLines())
                .append(radius)
                .append(visibleFor)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("position",     position)
                .append("lines",        getLines())
                .append("radius",       radius)
                .append("visibleFor",   visibleFor)
                .toString();
    }

    private void runAsync(final Runnable runnable) {
        new BukkitRunnable() {

            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(Core.get());
    }

    public static HologramBuilder builder() {
        return new HologramBuilder();
    }
}