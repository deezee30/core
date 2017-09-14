/*
 * Part of core.
 * 
 * Created on 20 June 2017 at 10:58 PM.
 */

package com.maulss.core.bukkit.world.region.type;

import com.google.gson.*;
import com.maulss.core.bukkit.world.region.Region;

import java.lang.reflect.Type;

public final class RegionTypeAdapter implements JsonSerializer<Region>, JsonDeserializer<Region> {

    private static final RegionTypeAdapter INSTANCE = new RegionTypeAdapter();
    public static final String META_TYPE = "type";
    public static final String META_REGION = "region";

    private RegionTypeAdapter() {}

    @Override
    public JsonElement serialize(Region region,
                                 Type type,
                                 JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.add(META_TYPE, new JsonPrimitive(region.getType().name()));
        result.add(META_REGION, context.serialize(region, region.getClass()));

        return result;
    }

    @Override
    public Region deserialize(JsonElement jsonElement,
                              Type elementType,
                              JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return context.deserialize(
                jsonElement.getAsJsonObject().get(META_REGION),
                RegionType.valueOf(jsonObject
                                .get(META_TYPE)
                                .getAsString()
                ).getDefaultClass()
        );
    }

    public static RegionTypeAdapter getInstance() {
        return INSTANCE;
    }
}