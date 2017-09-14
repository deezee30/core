package com.maulss.core.bukkit.fanciful;

import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a JSON string value.
 * Writes by this object will not write name values nor begin/end objects in the JSON stream.
 * All writes merely write the represented string value.
 */
final class JsonString implements JsonRepresentedObject, ConfigurationSerializable {

    private final String value;

    public JsonString(String value){
        this.value = value;
    }

    @Override
    public void writeJson(final JsonWriter writer) throws IOException {
        Validate.notNull(writer);
        writer.value(getValue());
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> theSingleValue = new HashMap<>();
        theSingleValue.put("stringValue", value);
        return theSingleValue;
    }

    public static JsonString deserialize(final Map<String, Object> map) {
        Validate.notNull(map);
        return new JsonString(map.get("stringValue").toString());
    }

    @Override
    public String toString(){
        return value;
    }

    public String getValue() {
        return value;
    }


}
