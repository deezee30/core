/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 2:41 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagCommand implements IFlag<PlayerCommandPreprocessEvent> {

    @Override
    public Class<PlayerCommandPreprocessEvent> getEvent() {
        return PlayerCommandPreprocessEvent.class;
    }

    @Override
    public Location getLocationOfAction(PlayerCommandPreprocessEvent event) {
        return event.getPlayer().getLocation();
    }

    @Override
    public Optional<Predicate<PlayerCommandPreprocessEvent>> onCondition() {
        return Optional.of(event -> !event.getMessage().startsWith("/"));
    }

    @Override
    public Optional<String> getResultMessage(PlayerCommandPreprocessEvent event) {
        return Optional.of("region.flag.no-command");
    }
}