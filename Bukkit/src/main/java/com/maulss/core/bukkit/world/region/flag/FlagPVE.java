/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 3:15 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import com.maulss.core.bukkit.player.event.CorePlayerDamageEntityEvent;
import org.bukkit.Location;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagPVE implements IFlag<CorePlayerDamageEntityEvent> {

    @Override
    public Class<CorePlayerDamageEntityEvent> getEvent() {
        return CorePlayerDamageEntityEvent.class;
    }

    @Override
    public Location getLocationOfAction(CorePlayerDamageEntityEvent event) {
        return event.getDamaged().getLocation();
    }

    @Override
    public Optional<Predicate<CorePlayerDamageEntityEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(CorePlayerDamageEntityEvent event) {
        return Optional.empty();
    }
}