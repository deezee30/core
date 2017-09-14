/*
 * Part of core.
 * 
 * Created on 16 July 2017 at 1:12 AM.
 */

package com.maulss.core.bukkit.world;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.player.CorePlayer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;

public class CoreWorld implements Serializable {

    private static final long serialVersionUID = 6547477132520902250L;
    private static final CoreWorldManager manager = CoreWorldManager.getInstance();

    private final String name;
    private boolean loaded;

    CoreWorld(final String name) {
        this.name = Validate.notNull(name);
    }

    public synchronized World load() throws WorldLoadException {
        if (isLoaded()) {
            throw new WorldLoadException("World '%s' is already loaded", name);
        }

        World world = Bukkit.createWorld(new WorldCreator(name));

        loaded = true;
        // TODO: Register with CoreWorldManager

        return world;
    }

    public synchronized boolean unload() throws WorldLoadException {
        // by default, saving is enabled
        return unload(true);
    }

    public synchronized boolean unload(final boolean save) throws WorldLoadException {
        // kick players by default
        return unload(player -> player.getPlayer().kickPlayer("The world is being unloaded"), save);
    }

    public synchronized boolean unload(final Consumer<CorePlayer> function,
                                       final boolean save) throws WorldLoadException {
        if (!isLoaded())
            throw new WorldLoadException("Can't unload a not loaded world '%s'...", name);

        World world = getWorld().get();

        // decide where players need to go
        world.getPlayers()
                .stream()
                .map(CorePlayer::createIfAbsent)
                .forEach(function::accept);

        if (!world.getPlayers().isEmpty())
            throw new WorldLoadException("Tried unloading world '%s' while players are still inside");

        boolean ok = Bukkit.unloadWorld(world, save);

        // TODO: Unregister with CoreWorldManager

        Core.get().log("Unloaded world &e", name);
        loaded = false;
        return ok;
    }

    public synchronized boolean unloadAndDelete(final Consumer<CorePlayer> function) throws WorldLoadException {
        boolean ok = true;
        if (isLoaded()) {
            ok = unload(function, false);
        }

        if (ok) {
            try {
                FileUtils.forceDelete(new File(name + "/"));
            } catch (IOException e) {
                throw new WorldLoadException(e);
            }
        }

        Core.get().log("Deleted world &e%s", name);

        return ok;
    }

    public synchronized boolean isLoaded() {
        return loaded && manager.isLoaded(this);
    }

    public Optional<World> getWorld() {
        if (!isLoaded()) return Optional.empty();
        return Optional.ofNullable(Bukkit.getWorld(name));
    }
}