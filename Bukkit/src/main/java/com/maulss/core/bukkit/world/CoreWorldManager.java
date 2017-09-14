/*
 * Part of core.
 * 
 * Created on 16 July 2017 at 2:26 AM.
 */

package com.maulss.core.bukkit.world;

import com.maulss.core.bukkit.internal.config.MainConfig;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import java.util.Optional;

public final class CoreWorldManager implements Listener {

    private static final CoreWorldManager instance = new CoreWorldManager();

    public CoreWorld getLoadableWorld(final String name) {
        // check if world is loaded first of all
        Optional<CoreWorld> world = getLoadedWorld(name);
        return world.orElseGet(() -> new CoreWorld(name));
    }

    public Optional<CoreWorld> getLoadedWorld(final String name) {
        // TODO
        return Optional.of(new CoreWorld(name));
    }

    public boolean isLoaded(final CoreWorld world) {
        // TODO
        return false;
    }

    @EventHandler
    public void onInit(WorldInitEvent event) {
        if (MainConfig.isEfficientWorldManagement()) {
            World world = event.getWorld();
            world.setKeepSpawnInMemory(false);
            world.setAutoSave(false);
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doWeatherCycle", "false");
        }
    }

    public static CoreWorldManager getInstance() {
        return instance;
    }
}