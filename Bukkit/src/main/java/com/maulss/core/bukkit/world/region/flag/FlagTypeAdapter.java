/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 6:10 PM.
 */

package com.maulss.core.bukkit.world.region.flag;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FlagTypeAdapter implements JsonSerializer<Flag>, JsonDeserializer<Flag> {

    private static final FlagTypeAdapter INSTANCE = new FlagTypeAdapter();

    private FlagTypeAdapter() {}

    @Override
    public JsonElement serialize(Flag flag,
                                 Type type,
                                 JsonSerializationContext context) {
        return context.serialize(flag.getName(), Flag.class);
    }

    @Override
    public Flag deserialize(JsonElement jsonElement,
                            Type type,
                            JsonDeserializationContext context) throws JsonParseException {
        return Flag.from(jsonElement.getAsString()).get();
    }

    public static FlagTypeAdapter getInstance() {
        return INSTANCE;
    }
}