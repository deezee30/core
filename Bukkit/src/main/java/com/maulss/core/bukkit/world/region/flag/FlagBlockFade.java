/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 5:02 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.block.BlockFadeEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagBlockFade implements IFlag<BlockFadeEvent> {

    @Override
    public Class<BlockFadeEvent> getEvent() {
        return BlockFadeEvent.class;
    }

    @Override
    public Location getLocationOfAction(BlockFadeEvent event) {
        return event.getBlock().getLocation();
    }

    @Override
    public Optional<Predicate<BlockFadeEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(BlockFadeEvent event) {
        return Optional.empty();
    }
}