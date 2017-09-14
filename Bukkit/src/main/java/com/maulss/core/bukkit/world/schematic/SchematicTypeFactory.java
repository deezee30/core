/*
 * Part of core.
 * 
 * Created on 16 July 2017 at 2:53 AM.
 */

package com.maulss.core.bukkit.world.schematic;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;

public final class SchematicTypeFactory {

    private static SchematicTypeFactory instance;

    private SchematicTypeFactory() {}

    public SchematicType getSchematicType(final File file) {
        Validate.notNull(file);

        String name = file.getName();
        if (!name.contains("."))
            throw new IllegalArgumentException("File "
                    + file.getPath()
                    + " is not of schematic type");

        return getSchematicType(FilenameUtils.getExtension(name));
    }

    public SchematicType getSchematicType(final String ext) {
        Validate.notNull(ext);

        switch (ext.toLowerCase()) {
            case "schematic":
                return SchematicType.MINECRAFT;
            case "sch":
                return SchematicType.CORE;
        }

        throw new IllegalArgumentException("Extension "
                + ext
                + " is not a recognised schematic extension");
    }

    public boolean existsExtension(final String ext) {
        try {
            getSchematicType(ext);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static SchematicTypeFactory getFactory() {
        return instance == null
                ? instance = new SchematicTypeFactory()
                : instance;
    }
}