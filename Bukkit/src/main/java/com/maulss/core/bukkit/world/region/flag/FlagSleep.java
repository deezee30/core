/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 5:01 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagSleep implements IFlag<PlayerBedEnterEvent> {

    @Override
    public Class<PlayerBedEnterEvent> getEvent() {
        return PlayerBedEnterEvent.class;
    }

    @Override
    public Location getLocationOfAction(PlayerBedEnterEvent event) {
        return event.getBed().getLocation();
    }

    @Override
    public Optional<Predicate<PlayerBedEnterEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(PlayerBedEnterEvent event) {
        return Optional.empty();
    }
}