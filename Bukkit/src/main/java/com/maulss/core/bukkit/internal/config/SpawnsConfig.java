/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.config;

import com.maulss.core.bukkit.Core;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.collect.EnhancedMap;
import com.maulss.core.text.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public final class SpawnsConfig extends CoreConfigFile {

    private static final SpawnsConfig INSTANCE = new SpawnsConfig();

    private final EnhancedMap<String, Location> spawns = new EnhancedMap<>();

    private SpawnsConfig() {
        super(false);

        // Scheduling this task for the next available tick in order to wait for all
        // The worlds to be loaded, otherwise Bukkit.getWorld(String) would return null
        Bukkit.getScheduler().runTaskLater(Core.get(), () -> {
            final FileConfiguration config = getFileConfig();

            try {
                config.load(getConfig());
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            for (String name : config.getKeys(false)) {
                String worldName = config.getString(name + ".world");
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    Core.get().log("Spawn '%s' wasn't loaded -- World '%s' doesn't exist", name, worldName);
                    continue;
                }

                spawns.put(name, new Location(
                        world,
                        config.getDouble(name + ".x"),
                        config.getDouble(name + ".y"),
                        config.getDouble(name + ".z"),
                        (float) config.getDouble(name + ".yaw"),
                        (float) config.getDouble(name + ".pitch")
                ));
            }

            int count = spawns.size();
            Core.get().logIf(
                    count > 0,
                    "Loaded &e%s spawn%s&r: &e%s",
                    count,
                    StringUtil.checkPlural("", "s", count),
                    new EnhancedList<>(spawns.keySet()).toReadableList("&r, &e", true)
            );
        }, 0L);
    }

    @Override
    protected String getConfigName() {
        return "spawns.yml";
    }

    @Override
    protected String[] getPaths() {
        return new String[0];
    }

    public static void save(String name, Location location) throws IOException {
        INSTANCE.spawns.put(name, location);

        FileConfiguration config = INSTANCE.getFileConfig();

        config.set(name + ".world",	location.getWorld().getName());
        config.set(name + ".x",		location.getX());
        config.set(name + ".y",		location.getY());
        config.set(name + ".z",		location.getZ());
        config.set(name + ".yaw",	location.getYaw());
        config.set(name + ".pitch",	location.getPitch());

        config.save(INSTANCE.getConfig());
    }

    public static Location get(String name) {
        return INSTANCE.spawns.get(name);
    }
}