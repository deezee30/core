/*
 * Part of core.
 * 
 * Created on 19 June 2017 at 11:50 AM.
 */

package com.maulss.core.bukkit.world.region;

import com.google.common.collect.ImmutableList;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.world.region.type.RegionType;
import com.maulss.core.text.StringUtil;
import com.maulss.core.util.FileUtil;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public final class RegionManager {

    static final RegionManager INSTANCE = new RegionManager();
    private static final int MIN_REGION_FILE_SIZE = 8;
    private static final String REGION_FILE= Core.get().getDataFolder()
            + File.separator
            + "regions.json";
    private final RegionList regions = new RegionList();
    private boolean loaded = false;

    private RegionManager() {}

    public Region register(Region region) throws RegionException {
        return register(region, false);
    }

    public Region register(Region region, boolean save) throws RegionException {
        Regions.validateRegion(region);

        regions.add(region);

        Core.get().debug(
                "Registered %s region #%s: %s",
                region.getType(),
                regions.indexOf(region),
                region
        );

        if (save) save(new File(REGION_FILE));

        return region;
    }

    public RegionList register(RegionList regions) throws RegionException {
        return register(regions, false);
    }

    public RegionList register(RegionList regions, boolean save) throws RegionException {
        for (Region reg : regions)
            register(reg);

        if (save) save(new File(REGION_FILE));

        return regions;
    }

    public boolean isRegistered(Region region) {
        return regions.contains(region);
    }

    public RegionList load(File file) throws RegionLoadException {
        Validate.notNull(file);
        RegionList loaded = new RegionList();

        if (!file.exists()) {
            throw new RegionLoadException("Did not load regions, file '%s' doesn't exist!", file);
        }

        String content;

        try {
            content = FileUtil.read(file);
        } catch (IOException e) {
            throw new RegionLoadException(e);
        }

        // size of file may depend on encoding, so check for a min possible region size
        if (content == null || content.length() <= MIN_REGION_FILE_SIZE) return loaded;

        // regions seem to be ok
        loaded.addAll(RegionList.fromJson(content));

        return loaded;
    }

    public void save(File file) throws RegionException {
        Validate.notNull(file);
        AtomicReference<Throwable> oops = new AtomicReference<>();
        String json = regions.toJson();

        // perform IO saves async
        Bukkit.getScheduler().runTaskAsynchronously(Core.get(), () -> {
            if (!Core.get().debugIf(
                    !file.exists(),
                    "Did not save %s regions, file doesn't exist!",
                    regions.size())) {

                try {
                    FileUtil.write(file, json);
                } catch (IOException e) {
                    oops.set(e);
                }

                Core.get().debug(
                        "Saved %s %s",
                        regions.size(),
                        StringUtil.checkPlural("region", "regions", regions.size())
                );
            }
        });

        if (oops.get() != null) throw new RegionException(oops.get());
    }

    public ImmutableList<Region> getRegions(RegionCriteria criteria) {
        return criteria.searchIn(regions).getImmutableElements();
    }

    public ImmutableList<Region> getRegions() {
        return INSTANCE.regions.getImmutableElements();
    }

    public int init() throws RegionLoadException {
        // first of all, check if init has already been called
        if (loaded) {
            throw new RegionLoadException("Default regions have already been loaded");
        }

        // register all configuration serializables
        for (RegionType type : RegionType.values()) {
            Class<? extends ConfigurationSerializable> clazz = type.getDefaultClass();
            if (clazz != null)	ConfigurationSerialization.registerClass(clazz);
        }

        // default region file
        File file = new File(REGION_FILE);

        // if it doesn't exist, try create it and log
        try {
            Core.get().logIf(
                    !file.exists() && file.createNewFile(),
                    "Created new file '%s'",
                    REGION_FILE
            );
        } catch (IOException e) {
            throw new RegionLoadException(e);
        }

        // load the regions from the file if possible
        RegionList regions = load(file);

        // try register all regions
        try {
            register(regions);
        } catch (RegionException e) {
            throw new RegionLoadException(e);
        }

        // load complete
        loaded = true;

        return regions.size();
    }

    public boolean isLoaded() {
        return loaded;
    }
}