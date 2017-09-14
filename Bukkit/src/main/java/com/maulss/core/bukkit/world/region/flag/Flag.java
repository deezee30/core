package com.maulss.core.bukkit.world.region.flag;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.world.Position;
import com.maulss.core.bukkit.world.region.Region;
import com.maulss.core.bukkit.world.region.RegionCriteria;
import com.maulss.core.bukkit.world.region.Regions;
import com.maulss.core.collect.EnhancedList;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.apache.commons.lang3.Validate.notNull;

public class Flag<E extends Event & Cancellable> implements EventExecutor {

    private static final EventPriority EVENT_PRIORITY = EventPriority.MONITOR;
    private static final EnhancedList<Flag> ALL_FLAGS = new EnhancedList<>();

    public static final Flag
            BUILD               = new Flag<>(new FlagBuild()),
            BREAK               = new Flag<>(new FlagBreak()),
            CHAT                = new Flag<>(new FlagChat()),
            COMMAND             = new Flag<>(new FlagCommand()),
            PVP                 = new Flag<>(new FlagPVP()),
            PVE                 = new Flag<>(new FlagPVE()),
            ALL_DAMAGE          = new Flag<>(new FlagAllDamage()),
            BLOCK_INTERACT      = new Flag<>(new FlagBlockInteract()),
            ITEM_INTERACT       = new Flag<>(new FlagItemInteract()),
            ENTITY_INTERACT     = new Flag<>(new FlagEntityInteract()),
            ENDERMAN_INTERACT   = new Flag<>(new FlagEndermanInteract()),
            ITEM_SPAWN          = new Flag<>(new FlagItemSpawn()),
            EXP_SPAWN           = new Flag<>(new FlagExpSpawn()),
            ANIMAL_SPAWN        = new Flag<>(new FlagAnimalSpawn()),
            MONSTER_SPAWN       = new Flag<>(new FlagMonsterSpawn()),
            EXPLOSION           = new Flag<>(new FlagExplosion()),
            HEALTH_REGEN        = new Flag<>(new FlagHealthRegen()),
            HUNGER_LOSS         = new Flag<>(new FlagHungerLoss()),
            POTION_SPLASH       = new Flag<>(new FlagPotionSplash()),
            BLOCK_BURN          = new Flag<>(new FlagBlockBurn()),
            VEHICLE_PLACE       = new Flag<>(new FlagVehiclePlace()),
            VEHICLE_DESTROY     = new Flag<>(new FlagVehicleDestroy()),
            SLEEP               = new Flag<>(new FlagSleep()),
            BLOCK_FORM          = new Flag<>(new FlagBlockForm()),
            BLOCK_FADE          = new Flag<>(new FlagBlockFade()),
            BLOCK_MOVE          = new Flag<>(new FlagBlockMove()),
            BLOCK_SPREAD        = new Flag<>(new FlagBlockSpread());

    protected String flagName;
    protected transient IFlag<E> flag;

    protected Flag(final IFlag<E> flag) {
        this.flag = flag;
        ALL_FLAGS.add(this);
        Bukkit.getPluginManager().registerEvent(
                flag.getEvent(),
                new Listener() {},
                EVENT_PRIORITY,
                this, Core.get()
        );
    }

    public String getName() {
        return flagName;
    }

    @Override
    public String toString() {
        return flagName;
    }

    @Override
    public void execute(final Listener listener,
                        final Event providedEvent) throws EventException {
        if (flag == null) return;

        if (!(providedEvent instanceof Cancellable))
            throw new EventException("Event " + providedEvent.getEventName() + " is not cancellable");

        if (!flag.getEvent().isInstance(providedEvent))
            return; // wrong event

        E event = (E) providedEvent;
        Optional<Predicate<E>> predicate = flag.onCondition();

        if (predicate.isPresent() && !predicate.get().test(event))
            return; // does not match predicate

        Location loc = flag.getLocationOfAction(event);

        if (loc == null)
            throw new EventException("Flag action event "
                    + flagName + " must contain location of action: ");

        Position location = new Position(loc);

        // obtain registered regions with current flag registered
        List<Region> regions = Regions.getRegions(new RegionCriteria()
                .in(loc.getWorld())
                .at(location)
                .withFlags(new FlagMap(this, true)));

        // there should be maximum 1 region with this criteria
        if (regions.isEmpty()) return;

        // cancel the event if criteria matches
        event.setCancelled(true);

        // if there is an associated player and the result message
        // isn't null, send that player a message
        flag.getResultMessage(event).ifPresent(
                s -> flag.getAssociatedPlayer(event).ifPresent(
                        corePlayer -> corePlayer.sendMessage(s)));

        // further action
        flag.onFurtherAction(event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Flag<?> flag1 = (Flag<?>) o;

        return new EqualsBuilder()
                .append(flagName, flag1.flagName)
                .append(flag, flag1.flag)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(flagName)
                .append(flag)
                .toHashCode();
    }

    public static Optional<Flag> from(final String flag) {
        for (Flag f : ALL_FLAGS) {
            if (f.flagName.toUpperCase().equals(flag.toUpperCase())) {
                return Optional.of(f);
            }
        }

        return Optional.empty();
    }

    public static Optional<Flag> from(final int id) {
        return Optional.ofNullable(ALL_FLAGS.get(id));
    }

    public static <E extends Event & Cancellable> Flag create(final String flagName,
                                                              final Class<E> event,
                                                              final Function<E, Location> locationOfAction) {
        return create(flagName, event, locationOfAction, Optional.empty());
    }

    public static <E extends Event & Cancellable> Flag create(final String flagName,
                                                              final Class<E> event,
                                                              final Function<E, Location> locationOfAction,
                                                              final Optional<Predicate<E>> onCondition) {
        return create(flagName, event, locationOfAction, onCondition, Optional.empty());
    }

    public static <E extends Event & Cancellable> Flag create(final String flagName,
                                                              final Class<E> event,
                                                              final Function<E, Location> locationOfAction,
                                                              final Optional<Predicate<E>> onCondition,
                                                              final Optional<Function<E, String>> resultMessage) {
        return create(flagName, event, locationOfAction, onCondition, resultMessage, Optional.empty());
    }

    public static <E extends Event & Cancellable> Flag create(final String flagName,
                                                              final Class<E> event,
                                                              final Function<E, Location> locationOfAction,
                                                              final Optional<Predicate<E>> onCondition,
                                                              final Optional<Function<E, String>> resultMessage,
                                                              final Optional<Function<E, Runnable>> onFurtherAction) {
        notNull(flagName);
        notNull(event);
        notNull(locationOfAction);
        notNull(onCondition);
        notNull(resultMessage);
        notNull(onFurtherAction);

        return create(flagName, new IFlag<E>() {

            @Override
            public Class<E> getEvent() {
                return event;
            }

            @Override
            public Location getLocationOfAction(E event) {
                return locationOfAction.apply(event);
            }

            @Override
            public Optional<Predicate<E>> onCondition() {
                return onCondition;
            }

            @Override
            public Optional<String> getResultMessage(E event) {
                return resultMessage.map(eStringFunction -> eStringFunction.apply(event));
            }

            @Override
            public void onFurtherAction(E event) {
                if (onFurtherAction.isPresent())
                    onFurtherAction.get().apply(event);
            }
        });
    }

    public static <E extends Event & Cancellable> Flag create(final String flagName,
                                                              final IFlag<E> iFlag) {
        notNull(flagName);
        notNull(iFlag);

        Flag<E> flag = new Flag<>(iFlag);
        flag.flagName = flagName.toUpperCase();
        return flag;
    }

    public static void init() {
        try {
            for (Field field : Flag.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    Flag flag = (Flag) field.get(null);
                    flag.flagName = field.getName();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}