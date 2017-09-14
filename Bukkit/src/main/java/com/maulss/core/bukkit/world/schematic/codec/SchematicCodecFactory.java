/*
 * Part of core.
 * 
 * Created on 17 July 2017 at 12:28 AM.
 */

package com.maulss.core.bukkit.world.schematic.codec;

import com.maulss.core.bukkit.world.schematic.SchematicType;
import org.apache.commons.lang3.Validate;

public final class SchematicCodecFactory {

    private static SchematicCodecFactory instance;

    private static final SchematicCodec
            NBT_CODEC   = new NBTSchematicCodec(),
            CORE_CODEC  = new CoreSchematicCodec();

    private SchematicCodecFactory() {}

    public SchematicCodec getCodec(final SchematicType type) {
        Validate.notNull(type);
        switch (type) {
            case MCEDIT:
            case WORLDEDIT:
            case MINECRAFT:
                return NBT_CODEC;
            case RIDDLESVILLAGE:
                return CORE_CODEC;
        }

        throw new IllegalArgumentException("Schematic codec for " + type + " doesn't exist!");
    }

    public static SchematicCodecFactory getFactory() {
        return instance == null
                ? instance = new SchematicCodecFactory()
                : instance;
    }
}