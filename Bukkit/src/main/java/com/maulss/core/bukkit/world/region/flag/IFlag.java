/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 12:50 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.event.CoreConnectedPlayerEvent;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * The interface for custom flags to be registered and applied
 * to {@link com.maulss.core.bukkit.world.region.Region}s.
 *
 * <p>Each flag is linked to a corresponding action event that
 * has to be cancellable.</p>
 *
 * @param <E> The class of the event this flag is associated with
 */
public interface IFlag<E extends Event & Cancellable> {

    CorePlayerManager MAN = CorePlayerManager.getInstance();

    /**
     * Gets the class of the associated event
     *
     * @return the event's class associated with the cancellable action
     */
    Class<E> getEvent();

    /**
     * Gets location of where the cancellable action
     * has occurred based on the event provided.
     *
     * <p>For example, if a block that shouldn't be
     * placed is placed, the {@param event} will be
     * {@link org.bukkit.event.block.BlockPlaceEvent}
     * and the location of action will be {@code
     * event.getBlock().getLocation()}</p>
     *
     * @param   event
     *          the event associated with the cancellable action
     * @return  the location of action
     */
    Location getLocationOfAction(E event);

    /**
     * An {@code Optional} condition that decides whether
     * the action event should be cancelled or not.
     *
     * <p>If a condition is not provided, the event action
     * will be cancelled without any further checks.</p>
     *
     * @return  the optional condition
     */
    Optional<Predicate<E>> onCondition();

    /**
     * In case the action gets refused in the region,
     * an optional message can be sent to the associated
     * player is he is provided in {@link #getAssociatedPlayer(Event)}.
     *
     * <p>The returned message can either be an actual
     * message or a path to the locale message.</p>
     *
     * <p>This only happens if the player has been detected
     * in the event action. If the event is a sub class of
     * either {@link PlayerEvent}, {@link EntityEvent} or
     * {@link CoreConnectedPlayerEvent}, a player should automatically
     * be detected. In any other case, {@link Optional#empty()}
     * should be returned</p>
     *
     * @param   event
     *          the event associated with the cancellable action
     * @return  the result message that will be sent to the player
     * @see     #getAssociatedPlayer(Event)
     */
    Optional<String> getResultMessage(E event);

    /**
     * In case any further action is required to be done.
     *
     * <p>This default method can be inherited to be called
     * whenever an action is cancelled, similar to {@link
     * #getResultMessage(Event)} but instead of sending a
     * message, any process can be done.</p>
     *
     * @param event the event associated with the cancellable action
     */
    default void onFurtherAction(E event) {}

    /**
     * If present, detects a player that is associated with
     * the flag's action event.
     *
     * <p>A player is only found if the event is a sub class
     * of either {@link PlayerEvent}, {@link EntityEvent} or
     * {@link CoreConnectedPlayerEvent}.</p>
     *
     * @param event the event associated with the cancellable action
     * @return a player associated with the action if found, or empty
     */
    default Optional<CorePlayer> getAssociatedPlayer(E event) {
        Optional<CorePlayer> player = Optional.empty();

        if (event instanceof PlayerEvent) {
            PlayerEvent pEvent = (PlayerEvent) event;
            player = Optional.ofNullable(MAN.get(pEvent));
        } else if (event instanceof CoreConnectedPlayerEvent) {
            CoreConnectedPlayerEvent pEvent = (CoreConnectedPlayerEvent) event;
            player = Optional.of(pEvent.getPlayer());
        } else if (event instanceof EntityEvent) {
            EntityEvent eEvent = (EntityEvent) event;
            player = Optional.ofNullable(MAN.get(eEvent));
        }

        return player;
    }
}