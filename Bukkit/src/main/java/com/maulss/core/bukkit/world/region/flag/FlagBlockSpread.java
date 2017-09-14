/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 5:04 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.block.BlockSpreadEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagBlockSpread implements IFlag<BlockSpreadEvent> {

    @Override
    public Class<BlockSpreadEvent> getEvent() {
        return BlockSpreadEvent.class;
    }

    @Override
    public Location getLocationOfAction(BlockSpreadEvent event) {
        return event.getSource().getLocation();
    }

    @Override
    public Optional<Predicate<BlockSpreadEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(BlockSpreadEvent event) {
        return Optional.empty();
    }
}