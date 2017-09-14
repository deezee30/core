/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 4:02 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagHungerLoss implements IFlag<FoodLevelChangeEvent> {

    @Override
    public Class<FoodLevelChangeEvent> getEvent() {
        return FoodLevelChangeEvent.class;
    }

    @Override
    public Location getLocationOfAction(FoodLevelChangeEvent event) {
        return event.getEntity().getLocation();
    }

    @Override
    public Optional<Predicate<FoodLevelChangeEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(FoodLevelChangeEvent event) {
        return Optional.empty();
    }
}