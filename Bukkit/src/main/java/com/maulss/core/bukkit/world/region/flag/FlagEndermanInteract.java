/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 3:54 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagEndermanInteract implements IFlag<EntityChangeBlockEvent> {

    @Override
    public Class<EntityChangeBlockEvent> getEvent() {
        return EntityChangeBlockEvent.class;
    }

    @Override
    public Location getLocationOfAction(EntityChangeBlockEvent event) {
        return event.getBlock().getLocation();
    }

    @Override
    public Optional<Predicate<EntityChangeBlockEvent>> onCondition() {
        return Optional.of(e -> e.getEntityType().equals(EntityType.ENDERMAN));
    }

    @Override
    public Optional<String> getResultMessage(EntityChangeBlockEvent event) {
        return Optional.empty();
    }
}