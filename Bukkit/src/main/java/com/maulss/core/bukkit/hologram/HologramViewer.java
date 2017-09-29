/*
 * Part of core.
 * 
 * Created on 21 July 2017 at 11:10 PM.
 */

package com.maulss.core.bukkit.hologram;

import com.maulss.core.bukkit.player.CorePlayer;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public interface HologramViewer {

    UUID getUuid();

    default Optional<CorePlayer> getHandle() {
        return Optional.ofNullable(CorePlayer.PLAYER_MANAGER.get(getUuid()));
    }

    default HologramViewer copy() {
        return from(this);
    }

    static HologramViewer from(final HologramViewer viewer) {
        return from(viewer.getUuid());
    }

    static HologramViewer from(final String uuid) {
        return from(UUID.fromString(uuid));
    }

    static HologramViewer from(final UUID uuid) {
        return new Impl(uuid);
    }

    class Impl implements HologramViewer, Serializable {

        private final UUID uuid;

        private Impl(final UUID uuid) {
            this.uuid = notNull(uuid);
        }

        @Override
        public UUID getUuid() {
            return uuid;
        }

        @Override
        public String toString() {
            return uuid.toString();
        }

        @Override
        public boolean equals(Object o) {
            return this == o
                    || !(o == null || getClass() != o.getClass())
                    && uuid.equals(((Impl) o).uuid);
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(uuid)
                    .toHashCode();
        }
    }
}