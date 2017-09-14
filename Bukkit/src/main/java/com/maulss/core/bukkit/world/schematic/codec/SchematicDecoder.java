/*
 * Part of core.
 * 
 * Created on 16 July 2017 at 11:51 PM.
 */

package com.maulss.core.bukkit.world.schematic.codec;

import com.maulss.core.bukkit.world.schematic.SchematicData;

import java.io.File;

@FunctionalInterface
public interface SchematicDecoder {

    SchematicData decode(final File file) throws SchematicCodecException;
}