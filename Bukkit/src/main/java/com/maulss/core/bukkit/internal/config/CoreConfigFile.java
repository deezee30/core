/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.config;

import com.maulss.core.bukkit.ConfigFile;
import com.maulss.core.bukkit.Core;
import org.bukkit.plugin.java.JavaPlugin;

abstract class CoreConfigFile extends ConfigFile {

    public CoreConfigFile() {}

    public CoreConfigFile(boolean instaLoad) {
        super(instaLoad);
    }

    @Override
    protected final JavaPlugin getPluginInstance() {
        return Core.get();
    }
}