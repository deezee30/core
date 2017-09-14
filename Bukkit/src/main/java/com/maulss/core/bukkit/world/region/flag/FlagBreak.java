/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 1:32 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagBreak implements IFlag<BlockBreakEvent> {

    @Override
    public Class<BlockBreakEvent> getEvent() {
        return BlockBreakEvent.class;
    }

    @Override
    public Location getLocationOfAction(BlockBreakEvent event) {
        return event.getBlock().getLocation();
    }

    @Override
    public Optional<Predicate<BlockBreakEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(BlockBreakEvent event) {
        return Optional.empty();
    }
}