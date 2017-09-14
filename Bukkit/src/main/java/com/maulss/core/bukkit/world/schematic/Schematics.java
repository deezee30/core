/*
 * Part of core.
 * 
 * Created on 17 July 2017 at 12:42 AM.
 */

package com.maulss.core.bukkit.world.schematic;
import com.maulss.core.bukkit.Core;
import com.maulss.core.collect.EnhancedList;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.util.List;

public final class Schematics {

    public static File FOLDER;

    private static Schematics instance;
    private final EnhancedList<Schematic> loadedSchematics = new EnhancedList<>();

    private Schematics() {}

    public static boolean init(final boolean autoLoad) {
        boolean ok = true;

        try {
            if (instance != null)
                throw new IllegalStateException("Schematics have already been init");

            // default schematics folder
            FOLDER = new File(String.format(
                    "%s%sschematics",
                    Core.get().getDataFolder().getPath(),
                    File.separator
            ));

            // try generate schematics folder
            if (!(ok = FOLDER.mkdir()) && autoLoad) {
                instance = new Schematics();
                File[] files = FOLDER.listFiles();
                for (File file : files) {
                    String name = file.getName();
                    if (!name.contains(".") || !SchematicTypeFactory
                            .getFactory()
                            .existsExtension(FilenameUtils.getExtension(name))) continue;
                    instance.loadedSchematics.add(new CuboidSchematic(file));
                }
            }
        } catch (Exception exception) {
            // loaded schematics will get gc'ed
            instance = null;

            if (autoLoad) {
                Core.get().log("Caught %s while autoloading schematics", exception.getClass().getName());
                exception.printStackTrace();
                Core.get().log("Will retry to init with autoload disabled...");
                // retry without auto loading
                ok = init(false);
            } else {
                exception.printStackTrace();
            }
        }

        return ok;
    }

    public static List<Schematic> getLoadedSchematics() {
        return instance.loadedSchematics.getImmutableElements();
    }

    public static String getSchematicFromDefaultLocation(String fullName) {
        Validate.notNull(fullName);
        return FOLDER + File.separator + fullName;
    }
}