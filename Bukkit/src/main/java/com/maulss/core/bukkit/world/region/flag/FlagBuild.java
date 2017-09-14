/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 1:28 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagBuild implements IFlag<BlockPlaceEvent> {

    @Override
    public Class<BlockPlaceEvent> getEvent() {
        return BlockPlaceEvent.class;
    }

    @Override
    public Location getLocationOfAction(BlockPlaceEvent event) {
        return event.getBlock().getLocation();
    }

    @Override
    public Optional<Predicate<BlockPlaceEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(BlockPlaceEvent event) {
        return Optional.empty();
    }
}