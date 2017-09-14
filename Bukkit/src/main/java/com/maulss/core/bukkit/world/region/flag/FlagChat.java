/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 2:21 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagChat implements IFlag<AsyncPlayerChatEvent> {

    @Override
    public Class<AsyncPlayerChatEvent> getEvent() {
        return AsyncPlayerChatEvent.class;
    }

    @Override
    public Location getLocationOfAction(AsyncPlayerChatEvent event) {
        return event.getPlayer().getLocation();
    }

    @Override
    public Optional<Predicate<AsyncPlayerChatEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(AsyncPlayerChatEvent event) {
        return Optional.of("region.flag.no-chat");
    }
}