/*
 * Part of core.
 * 
 * Created on 16 July 2017 at 3:06 AM.
 */

package com.maulss.core.bukkit.world.schematic;

import org.apache.commons.lang3.Validate;

import java.io.File;

public abstract class AbstractSchematic implements Schematic {

    protected final String name;
    protected final SchematicType type;

    public AbstractSchematic(final String name,
                             final SchematicType type) {
        this.name = Validate.notNull(name);
        this.type = Validate.notNull(type);
    }

    public AbstractSchematic(final File file) {
        Validate.notNull(file);

        String fullName = file.getName();
        String[] parts = fullName.split("\\.");
        name = parts[0];
        type = SchematicTypeFactory.getFactory().getSchematicType(parts[1]);
    }

    public AbstractSchematic(final String name) {
        this(new File(Schematics.getSchematicFromDefaultLocation(name)));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SchematicType getType() {
        return type;
    }
}