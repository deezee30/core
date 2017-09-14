/*
 * Part of core.
 */

package com.maulss.core.bukkit.player.event;

import com.maulss.core.bukkit.player.profile.Profile;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Handles all events in relation to the API's default
 * user object, {@link Profile}.
 *
 * <p>Events triggered here can be for both, online and
 * offline users.  However, the user must at least exist
 * in the global database table where {@link
 * Profile}s data is stored.</p>
 */
public abstract class CorePlayerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Profile profile;

    protected CorePlayerEvent(final Profile profile) {
        this.profile = Validate.notNull(profile);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Profile getProfile() {
        return profile;
    }
}