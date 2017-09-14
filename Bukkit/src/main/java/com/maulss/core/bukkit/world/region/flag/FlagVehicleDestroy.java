/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 4:05 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import org.bukkit.Location;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import java.util.Optional;
import java.util.function.Predicate;

final class FlagVehicleDestroy implements IFlag<VehicleDestroyEvent> {

    @Override
    public Class<VehicleDestroyEvent> getEvent() {
        return VehicleDestroyEvent.class;
    }

    @Override
    public Location getLocationOfAction(VehicleDestroyEvent event) {
        return event.getVehicle().getLocation();
    }

    @Override
    public Optional<Predicate<VehicleDestroyEvent>> onCondition() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getResultMessage(VehicleDestroyEvent event) {
        return Optional.empty();
    }
}