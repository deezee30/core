/*
 * Part of core.
 * 
 * Created on 16 July 2017 at 11:46 PM.
 */

package com.maulss.core.bukkit.world.schematic;

import com.maulss.core.jnbt.Tag;
import com.maulss.core.math.Vector3D;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.util.BlockVector;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public final class SchematicData implements Serializable {

    private static final long serialVersionUID = 4313609736759466913L;

    private final short[] blocks;
    private final byte[] blockData;
    private final Vector3D size;
    private final Optional<Vector3D> origin, offset;
    private final Optional<Map<BlockVector, Map<String, Tag>>> tileEntities;

    public SchematicData(final short[] blocks,
                         final byte[] blockData,
                         final Vector3D size) {
        this(blocks, blockData, size, null, null, null);
    }

    public SchematicData(final short[] blocks,
                         final byte[] blockData,
                         final Vector3D size,
                         final Vector3D origin,
                         final Vector3D offset,
                         final Map<BlockVector, Map<String, Tag>> tileEntities) {
        this.blocks = blocks;
        this.blockData = blockData;
        this.size = Validate.notNull(size);

        this.origin = Optional.ofNullable(origin);
        this.offset = Optional.ofNullable(offset);
        this.tileEntities = Optional.ofNullable(tileEntities);
    }

    public short[] getBlocks() {
        return blocks;
    }

    public byte[] getBlockData() {
        return blockData;
    }

    public Vector3D getSize() {
        return size;
    }

    public Optional<Vector3D> getOrigin() {
        return origin;
    }

    public Optional<Vector3D> getOffset() {
        return offset;
    }

    public Optional<Map<BlockVector, Map<String, Tag>>> getTileEntities() {
        return tileEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SchematicData that = (SchematicData) o;

        return new EqualsBuilder()
                .append(blocks, that.blocks)
                .append(blockData, that.blockData)
                .append(size, that.size)
                .append(origin, that.origin)
                .append(offset, that.offset)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(blocks)
                .append(blockData)
                .append(size)
                .append(origin)
                .append(offset)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("blocks", blocks)
                .append("blockData", blockData)
                .append("size", size)
                .append("origin", origin)
                .append("height", offset)
                .toString();
    }
}