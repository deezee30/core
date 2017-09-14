/*
 * Part of core.
 */

package com.maulss.core.bukkit.player.manager;

import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.CorePlayerList;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;

import java.util.Optional;

public final class CorePlayerManager extends PlayerManager<CorePlayer> {

    private static CorePlayerManager instance;
    private static final CorePlayerList PLAYERS = new CorePlayerList();

    private CorePlayerManager() {}

    public CorePlayer get(final EntityEvent event) {
        return get(event.getEntity().getUniqueId());
    }

    @Override
    protected CorePlayerList delegate() {
        return PLAYERS;
    }

    /**
     * {@inheritDoc}
     * @see #add(Player, Optional)
     */
    @Override
    public CorePlayer add(final Player player) {
        return add(
                Validate.notNull(player),
                Optional.ofNullable(player.getAddress().getHostName())
        );
    }

    /**
     * Registers the player along with his hostname.
     *
     * <p>This should be used when the {@param player}'s
     * {@link Player#getAddress()} is still undefined.  This
     * usually happens before the {@code CraftPlayer} instance
     * isn't fully loaded, ie, when the player is about to join
     * the server ({@link org.bukkit.event.player.PlayerLoginEvent})</p>
     *
     * <p>If the player is already registered, the current
     * registered instance will be returned.</p>
     *
     * <p>A new instance of {@link CorePlayer} will be instantiated provided
     * the player is not already online - which is a quite heavy task and
     * should be dealt with accordingly.</p>
     *
     * @param	player
     * 			The player ready to log in.
     * @param	hostName
     * 			A valid given host name of the player.
     * @return	A unique {@code CorePlayer} instance.
     * @throws	com.maulss.core.database.DatabaseException
     * 			If a database error occurs.
     */
    public CorePlayer add(final Player player,
                          final Optional<String> hostName) {
        CorePlayer cp = get(player.getName());

        if (cp != null) {
            return cp;
        }

        //noinspection deprecation
        PLAYERS.add(cp = CorePlayer._init(player, hostName));
        return cp;
    }

    public static CorePlayerManager getInstance() {
        return instance == null ? instance = new CorePlayerManager() : instance;
    }
}