/*
 * Part of core.
 * 
 * Created on 17 July 2017 at 12:31 AM.
 */

package com.maulss.core.bukkit.world.schematic.codec;

import com.maulss.core.bukkit.world.schematic.Schematic;
import com.maulss.core.bukkit.world.schematic.SchematicData;
import org.bukkit.World;

import java.io.File;

public class CoreSchematicCodec implements SchematicCodec {

    CoreSchematicCodec() {}

    @Override
    public SchematicData decode(final File file) throws SchematicCodecException {
        return null;
    }

    @Override
    public File encode(final Schematic schematic,
                       final World world) throws SchematicCodecException {
        return null;
    }
}