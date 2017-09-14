/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 3:23 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagItemInteract implements IFlag<PlayerInteractEvent> {

    @Override
    public Class<PlayerInteractEvent> getEvent() {
        return PlayerInteractEvent.class;
    }

    @Override
    public Location getLocationOfAction(PlayerInteractEvent event) {
        return event.getPlayer().getLocation();
    }

    @Override
    public Optional<Predicate<PlayerInteractEvent>> onCondition() {
        return Optional.of(PlayerInteractEvent::hasItem);
    }

    @Override
    public Optional<String> getResultMessage(PlayerInteractEvent event) {
        return Optional.empty();
    }
}