/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 3:16 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagAllDamage implements IFlag<EntityDamageEvent> {

    @Override
    public Class<EntityDamageEvent> getEvent() {
        return EntityDamageEvent.class;
    }

    @Override
    public Location getLocationOfAction(EntityDamageEvent event) {
        return event.getEntity().getLocation();
    }

    @Override
    public Optional<Predicate<EntityDamageEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(EntityDamageEvent event) {
        return Optional.empty();
    }
}