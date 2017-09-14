/*
 * Part of core.
 * 
 * Created on 16 July 2017 at 11:40 PM.
 */

package com.maulss.core.bukkit.world.schematic;

public enum SchematicType {

    MCEDIT("MCEDIT", "schematic"),
    WORLDEDIT("MCEDIT", "schematic"),
    MINECRAFT("MCEDIT", "schematic"),
    CORE("CORE", "sch");

    private final String type, extension;

    SchematicType(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }

    public String getType() {
        return type;
    }

    public String getExtension() {
        return extension;
    }
}