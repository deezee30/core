/*
 * Part of core.
 */

package com.maulss.core.bukkit.player.manager;

import com.google.common.collect.ImmutableList;
import com.maulss.core.bukkit.player.profile.Profile;
import com.maulss.core.collect.EnhancedList;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import java.util.Iterator;
import java.util.UUID;

/**
 * A manager that can hold subclass profiles of {@link
 * Profile} while keeping only a single instance of
 * each profile.
 *
 * @param <P> Any profile extending {@link Profile}
 */
public abstract class PlayerManager<P extends Profile> implements Iterable<P> {

    /**
     * Gets the {@link P} instance from his name.
     *
     * @param	name
     * 			The name of the profile.
     * @return	The {@link P} instance if found, or
     * 			{@code null} if not.
     */
    public P get(final String name) {
        Validate.notNull(name);

        for (P player : getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Gets the {@link P} instance from his unique ID.
     *
     * @param   id
     * 			The ID of the profile.
     * @return	The {@link P} instance if found, or
     * 			{@code null} if not.
     */
    public P get(final UUID id) {
        Validate.notNull(id);

        for (P player : getOnlinePlayers()) {
            if (player.getUuid().equals(id)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Gets the {@link P} instance from a {@link PlayerEvent}.
     *
     * Suitable for online players only.
     *
     * @param	event
     * 			The event the online player is associated with.
     * @return	The {@link P} instance if found, or
     * 			{@code null} if not.
     */
    public P get(final PlayerEvent event) {
        return get(event.getPlayer().getName());
    }

    /**
     * @return All online players that are registered in this player manager in an unmodifiable list.
     */
    public ImmutableList<P> getOnlinePlayers() {
        // Return an unmodifiable list to make sure the actual list isn't modified externally
        return delegate().getImmutableElements();
    }

    /**
     * @return A random {@link P} instance.
     */
    public P getRandomElement() {
        return delegate().getRandomElement();
    }

    protected abstract EnhancedList<P> delegate();

    public abstract P add(final Player player);

    public P remove(final P player) {
        Validate.notNull(player);
        delegate().remove(player);
        return player;
    }

    @Override
    public Iterator<P> iterator() {
        return getOnlinePlayers().iterator();
    }

    @Override
    public final String toString() {
        return delegate().toString();
    }
}