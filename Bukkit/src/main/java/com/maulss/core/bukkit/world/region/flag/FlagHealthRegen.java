/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 4:00 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagHealthRegen implements IFlag<EntityRegainHealthEvent> {

    @Override
    public Class<EntityRegainHealthEvent> getEvent() {
        return EntityRegainHealthEvent.class;
    }

    @Override
    public Location getLocationOfAction(EntityRegainHealthEvent event) {
        return event.getEntity().getLocation();
    }

    @Override
    public Optional<Predicate<EntityRegainHealthEvent>> onCondition() {
        return Optional.of(
                e -> e.getRegainReason().equals(RegainReason.REGEN) ||
                     e.getRegainReason().equals(RegainReason.SATIATED)
        );
    }

    @Override
    public Optional<String> getResultMessage(EntityRegainHealthEvent event) {
        return Optional.empty();
    }
}