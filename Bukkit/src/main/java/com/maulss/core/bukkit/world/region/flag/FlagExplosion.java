/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 4:00 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagExplosion implements IFlag<ExplosionPrimeEvent> {

    @Override
    public Class<ExplosionPrimeEvent> getEvent() {
        return ExplosionPrimeEvent.class;
    }

    @Override
    public Location getLocationOfAction(ExplosionPrimeEvent event) {
        return event.getEntity().getLocation();
    }

    @Override
    public Optional<Predicate<ExplosionPrimeEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(ExplosionPrimeEvent event) {
        return Optional.empty();
    }
}