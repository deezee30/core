/*
 * Part of core.
 * 
 * Created on 19 June 2017 at 12:07 PM.
 */

package com.maulss.core.bukkit.world.region;

import com.maulss.core.bukkit.world.region.flag.Flag;
import com.maulss.core.bukkit.world.region.flag.FlagMap;
import com.maulss.core.bukkit.world.region.type.RegionType;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.collect.EnhancedMap;
import com.maulss.core.math.Vector3D;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public final class RegionCriteria {

    private Optional<List<String>> worlds = Optional.empty();
    private Optional<List<Predicate<Region>>> predicates = Optional.empty();
    private Optional<List<RegionType>> types = Optional.empty();
    private Optional<List<Vector3D>> coords = Optional.empty();
    private Optional<Map<Flag, Optional<Boolean>>> flags = Optional.empty();

    public RegionCriteria() {}

    public RegionCriteria in(final World... worlds) {
        EnhancedList<String> worldList = new EnhancedList<>(worlds.length);
        for (World world : worlds)
            if (world != null) worldList.add(world.getName());
        this.worlds = Optional.of(worldList);
        return this;
    }

    public RegionCriteria byPredicates(final Predicate<Region>... predicates) {
        this.predicates = Optional.of(new EnhancedList<>(predicates));
        return this;
    }

    public RegionCriteria byTypes(final RegionType... types) {
        this.types = Optional.of(new EnhancedList<>(types));
        return this;
    }

    public RegionCriteria at(final Vector3D... coords) {
        this.coords = Optional.of(new EnhancedList<>(coords));
        return this;
    }

    public RegionCriteria withFlags(final FlagMap flags) {
        EnhancedMap<Flag, Optional<Boolean>> flagMap = new EnhancedMap<>();
        for (Map.Entry<Flag, Boolean> entry : flags.entrySet())
            flagMap.put(entry.getKey(), Optional.ofNullable(entry.getValue()));
        this.flags = Optional.of(flagMap);
        return this;
    }

    public RegionCriteria withFlags(final Flag... flags) {
        EnhancedMap<Flag, Optional<Boolean>> flagMap = new EnhancedMap<>();
        for (Flag flag : flags) {
            flagMap.put(flag, Optional.empty());
        }
        this.flags = Optional.of(flagMap);
        return this;
    }

    public RegionList searchIn(RegionList list) {
        int len = list.size();
        if (len == 0) return list;

        RegionList regions = new RegionList(list);

        worlds.ifPresent(strings -> list.forEach(region ->
                strings.forEach(world -> regions.removeIf(
                        !region.getWorld().equals(world), region))));

        predicates.ifPresent(predicates -> list.forEach(region ->
                predicates.forEach(predicate -> regions.removeIf(
                        !predicate.test(region), region))));

        types.ifPresent(regionTypes -> list.forEach(region -> regions.removeIf(
                !regionTypes.contains(region.getType()), region)));

        coords.ifPresent(vector3DS -> list.forEach(region ->
                vector3DS.forEach(coord -> regions.removeIf(
                        !region.contains(coord),region))));

        flags.ifPresent(flagOptionalMap -> list.forEach(region ->
                flagOptionalMap.forEach((flag, allow) -> regions.removeIf(
                        !region.hasFlag(flag) || (allow.isPresent()
                                && region.isAllowed(flag) == allow.get()), region))));

        return regions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RegionCriteria regions1 = (RegionCriteria) o;

        return new EqualsBuilder()
                .append(worlds, regions1.worlds)
                .append(predicates, regions1.predicates)
                .append(types, regions1.types)
                .append(coords, regions1.coords)
                .append(flags, regions1.flags)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(worlds)
                .append(predicates)
                .append(types)
                .append(coords)
                .append(flags)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("worlds", worlds)
                .append("predicates", predicates)
                .append("types", types)
                .append("coords", coords)
                .append("flags", flags)
                .toString();
    }
}