/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 4:03 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.entity.PotionSplashEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagPotionSplash implements IFlag<PotionSplashEvent> {

    @Override
    public Class<PotionSplashEvent> getEvent() {
        return PotionSplashEvent.class;
    }

    @Override
    public Location getLocationOfAction(PotionSplashEvent event) {
        return event.getEntity().getLocation();
    }

    @Override
    public Optional<Predicate<PotionSplashEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(PotionSplashEvent event) {
        return Optional.empty();
    }
}