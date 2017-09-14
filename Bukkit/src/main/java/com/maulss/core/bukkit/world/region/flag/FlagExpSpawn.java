/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 3:57 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagExpSpawn implements IFlag<EntitySpawnEvent> {

    @Override
    public Class<EntitySpawnEvent> getEvent() {
        return EntitySpawnEvent.class;
    }

    @Override
    public Location getLocationOfAction(EntitySpawnEvent event) {
        return event.getLocation();
    }

    @Override
    public Optional<Predicate<EntitySpawnEvent>> onCondition() {
        return Optional.of(e -> e.getEntityType().equals(EntityType.EXPERIENCE_ORB));
    }

    @Override
    public Optional<String> getResultMessage(EntitySpawnEvent event) {
        return Optional.empty();
    }
}