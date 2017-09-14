/*
 * Part of core.
 * 
 * Created on 16 July 2017 at 2:46 AM.
 */

package com.maulss.core.bukkit.world.schematic;

import com.maulss.core.bukkit.world.schematic.codec.SchematicCodecException;
import com.maulss.core.bukkit.world.schematic.codec.SchematicCodecFactory;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.Serializable;

public interface Schematic extends Serializable {

    String getName();

    default String getFullName() {
        return getName() + "." + getType().getExtension();
    }

    SchematicData getData();

    boolean paste(final Location baseLocation);

    default File save(final World world) throws SchematicCodecException {
        return SchematicCodecFactory.getFactory()
                .getCodec(getType())
                .encode(this, Validate.notNull(world));
    }

    default File getDefaultFile() {
        return new File(
                Schematics.FOLDER.getPath()
                + File.separator
                + getFullName()
        );
    }

    SchematicType getType();
}