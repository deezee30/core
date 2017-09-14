package com.maulss.core.bukkit.fanciful;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Represents an object that can be serialized to a JSON writer instance.
 */
@FunctionalInterface
interface JsonRepresentedObject {

    /**
     * Writes the JSON representation of this object to the specified writer.
     * @param writer The JSON writer which will receive the object.
     * @throws IOException If an error occurs writing to the stream.
     */
    void writeJson(final JsonWriter writer) throws IOException;
}
