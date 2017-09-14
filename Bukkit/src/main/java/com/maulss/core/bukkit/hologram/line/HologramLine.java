/*
 * Part of core.
 * 
 * Created on 23 July 2017 at 2:58 PM.
 */

package com.maulss.core.bukkit.hologram.line;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.entity.EntityCreator;
import com.maulss.core.bukkit.entity.SimpleEntityCreator;
import com.maulss.core.bukkit.packet.wrapper.WrapperPlayServerEntityDestroy;
import com.maulss.core.bukkit.packet.wrapper.WrapperPlayServerEntityMetadata;
import com.maulss.core.bukkit.packet.wrapper.WrapperPlayServerSpawnEntityLiving;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.world.Position;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.math.Vector3D;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry.get;
import static org.apache.commons.lang3.Validate.notNull;

public abstract class HologramLine {

    public static final int LINE_OPACITY_THRESHOLD = 20;

    private static final EntityCreator ARMOR_STAND = new SimpleEntityCreator("EntityArmorStand");

    private transient Entity associatedEntity;

    public abstract String getText();

    public abstract void setText(final String text);

    public float getHeight() {
        return .21f;
    }

    public float getOpacity() {
        return .05f;
    }

    public Integer getRadius() {
        // by default, no radius is set
        return null;
    }

    public final Entity getEntity() {
        return associatedEntity;
    }

    public final Entity spawn(final Position position,
                              final double yOffset,
                              Collection<CorePlayer> players) {
        // support for non-textual lines
        if (getText() == null) return null;

        notNull(position);

        if (associatedEntity != null) associatedEntity.remove();

        // Create appropriate armor stand entity
        associatedEntity = ARMOR_STAND.create(position.clone()
                        .subtract(new Vector3D(0d, yOffset + getHeight(), 0d))
        );

        if (players == null)
            players = CorePlayer.PLAYER_MANAGER.getOnlinePlayers();

        if (players.isEmpty()) return associatedEntity;

        WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
        packet.setEntityID(associatedEntity.getEntityId());
        packet.setType(EntityType.ARMOR_STAND);
        packet.setUniqueId(associatedEntity.getUniqueId());
        packet.setX(associatedEntity.getLocation().getX());
        packet.setY(associatedEntity.getLocation().getY());
        packet.setZ(associatedEntity.getLocation().getZ());

        WrappedDataWatcher meta = new WrappedDataWatcher();
        meta.setObject(new WrappedDataWatcherObject(0, get(Byte.class)), (byte) 0x20);
        meta.setObject(new WrappedDataWatcherObject(3, get(Boolean.class)), true);
        meta.setObject(new WrappedDataWatcherObject(4, get(Boolean.class)), true);
        meta.setObject(new WrappedDataWatcherObject(5, get(Boolean.class)), true);
        meta.setObject(new WrappedDataWatcherObject(11, get(Byte.class)), (byte) 0x01);
        meta.setObject(new WrappedDataWatcherObject(11, get(Byte.class)), (byte) 0x04);
        meta.setObject(new WrappedDataWatcherObject(11, get(Byte.class)), (byte) 0x08);
        meta.setObject(new WrappedDataWatcherObject(11, get(Byte.class)), (byte) 0x10);

        // reuse packet but change title each time
        for (CorePlayer player : players) {
            if (!update0(players, player))
                continue;

            // support for color coding and locales
            meta.setObject(
                    new WrappedDataWatcherObject(2, get(String.class)),
                    translate(player)
            );

            // update meta per player
            packet.setMetadata(meta);

            // send multiple times for opacity
            for (int x = 0; x < LINE_OPACITY_THRESHOLD * getOpacity(); x++) {
                player.sendPacket(packet);
            }
        }

        return associatedEntity;
    }

    private boolean update0(Collection<CorePlayer> players,
                            final CorePlayer player) {
        if (players == null)
            players = CorePlayer.PLAYER_MANAGER.getOnlinePlayers();

        if (players.isEmpty()) return false;

        // make sure armor stand is available
        if (associatedEntity == null) {
            destroy(player);
            return false;
        }

        // check if player is allowed
        if (players != null && !players.contains(player)) {
            destroy(player);
            return false;
        }

        // check if player is within radius
        if (getRadius() != null) {
            double dist = associatedEntity.getLocation().distance(player.getLocation());
            if (getRadius() < dist) {
                destroy(player);
                return false;
            }
        }

        return true;
    }

    public final void update(final String text) {
        setText(text);

        // support for non-textual lines
        if (getText() == null) return;

        List<CorePlayer> players = CorePlayer.PLAYER_MANAGER.getOnlinePlayers();
        if (players.isEmpty()) return;

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();

        packet.setEntityID(associatedEntity.getEntityId());

        EnhancedList<WrappedWatchableObject> meta = new EnhancedList<>(1);

        for (CorePlayer player : players) {
            meta.clear();
            meta.add(new WrappedWatchableObject(
                    new WrappedDataWatcherObject(2, get(String.class)),
                    translate(player)
            ));

            packet.setMetadata(meta);

            player.sendPacket(packet);
        }
    }

    private String translate(final CorePlayer player) {
        return ChatColor.translateAlternateColorCodes('&',
                Core.getSettings().get(player.getLocale(), getText()));
    }

    public final void destroy(CorePlayer... player) {
        destroy(Arrays.asList(player));
    }

    public final void destroy() {
        destroy(CorePlayer.PLAYER_MANAGER.getOnlinePlayers());

        if (associatedEntity != null) {
            associatedEntity.remove();
            associatedEntity = null;
        }
    }

    public final void destroy(Collection<CorePlayer> players) {
        if (!players.isEmpty() && associatedEntity != null) {
            // reusable packet for efficiency
            WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();

            // pack entity IDs into array
            packet.setEntityIds(new int[]{associatedEntity.getEntityId()});

            for (CorePlayer player : players) {
                player.sendPacket(packet);
            }
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("text", getText())
                .append("height", getHeight())
                .append("opacity", getOpacity())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HologramLine that = (HologramLine) o;

        return new EqualsBuilder()
                .append(getText(), that.getText())
                .append(getHeight(), that.getHeight())
                .append(getOpacity(), that.getOpacity())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getText())
                .append(getHeight())
                .append(getOpacity())
                .toHashCode();
    }
}