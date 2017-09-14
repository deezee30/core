/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 4:04 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.block.BlockBurnEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagBlockBurn implements IFlag<BlockBurnEvent> {

    @Override
    public Class<BlockBurnEvent> getEvent() {
        return BlockBurnEvent.class;
    }

    @Override
    public Location getLocationOfAction(BlockBurnEvent event) {
        return event.getBlock().getLocation();
    }

    @Override
    public Optional<Predicate<BlockBurnEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(BlockBurnEvent event) {
        return Optional.empty();
    }
}