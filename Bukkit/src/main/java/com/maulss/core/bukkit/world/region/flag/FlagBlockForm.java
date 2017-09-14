/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 5:02 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.block.BlockFormEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagBlockForm implements IFlag<BlockFormEvent> {

    @Override
    public Class<BlockFormEvent> getEvent() {
        return BlockFormEvent.class;
    }

    @Override
    public Location getLocationOfAction(BlockFormEvent event) {
        return event.getBlock().getLocation();
    }

    @Override
    public Optional<Predicate<BlockFormEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(BlockFormEvent event) {
        return Optional.empty();
    }
}