/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 5:03 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagBlockMove implements IFlag<BlockFromToEvent> {

    @Override
    public Class<BlockFromToEvent> getEvent() {
        return BlockFromToEvent.class;
    }

    @Override
    public Location getLocationOfAction(BlockFromToEvent event) {
        return event.getBlock().getLocation();
    }

    @Override
    public Optional<Predicate<BlockFromToEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(BlockFromToEvent event) {
        return Optional.empty();
    }
}