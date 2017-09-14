/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 3:48 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagEntityInteract implements IFlag<PlayerInteractAtEntityEvent> {

    @Override
    public Class<PlayerInteractAtEntityEvent> getEvent() {
        return PlayerInteractAtEntityEvent.class;
    }

    @Override
    public Location getLocationOfAction(PlayerInteractAtEntityEvent event) {
        return event.getRightClicked().getLocation();
    }

    @Override
    public Optional<Predicate<PlayerInteractAtEntityEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(PlayerInteractAtEntityEvent event) {
        return Optional.empty();
    }
}