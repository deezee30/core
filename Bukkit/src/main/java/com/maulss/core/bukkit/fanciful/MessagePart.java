package com.maulss.core.bukkit.fanciful;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.stream.JsonWriter;
import com.maulss.core.bukkit.CoreLogger;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal class: Represents a component of a JSON-serializable {@link FancyMessage}.
 */
final class MessagePart implements JsonRepresentedObject, ConfigurationSerializable, Cloneable {

    static final BiMap<ChatColor, String> stylesToNames;

    ChatColor color = ChatColor.WHITE;
    ArrayList<ChatColor> styles = new ArrayList<>();
    String clickActionName, clickActionData, hoverActionName;
    JsonRepresentedObject hoverActionData;
    TextualComponent text;

    MessagePart(final TextualComponent text) {
        this.text = text;
    }

    MessagePart() {
        text = null;
    }

    boolean hasText() {
        return text != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MessagePart clone() throws CloneNotSupportedException {
        MessagePart obj = (MessagePart) super.clone();
        obj.styles = (ArrayList<ChatColor>) styles.clone();
        if (hoverActionData instanceof JsonString) {
            obj.hoverActionData = new JsonString(((JsonString) hoverActionData).getValue());
        } else if (hoverActionData instanceof FancyMessage) {
            obj.hoverActionData = ((FancyMessage) hoverActionData).clone();
        }
        return obj;

    }

    public void writeJson(final JsonWriter json) {
        Validate.notNull(json);
        try {
            json.beginObject();
            text.writeJson(json);
            json.name("color").value(color.name().toLowerCase());
            for (final ChatColor style : styles) {
                json.name(stylesToNames.get(style)).value(true);
            }
            if (clickActionName != null && clickActionData != null) {
                json.name("clickEvent")
                .beginObject()
                .name("action").value(clickActionName)
                .name("value").value(clickActionData)
                .endObject();
            }
            if (hoverActionName != null && hoverActionData != null) {
                json.name("hoverEvent")
                .beginObject()
                .name("action").value(hoverActionName)
                .name("value");
                hoverActionData.writeJson(json);
                json.endObject();
            }
            json.endObject();
        } catch(IOException e){
            CoreLogger.log("A problem occured during writing of JSON string: " + e.getMessage());
        }
    }

    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("text", text);
        map.put("styles", styles);
        map.put("color", color.getChar());
        map.put("hoverActionName", hoverActionName);
        map.put("hoverActionData", hoverActionData);
        map.put("clickActionName", clickActionName);
        map.put("clickActionData", clickActionData);
        return map;
    }

    public static MessagePart deserialize(final Map<String, Object> serialized) {
        MessagePart part = new MessagePart((TextualComponent)serialized.get("text"));
        part.styles = (ArrayList<ChatColor>) serialized.get("styles");
        part.color = ChatColor.getByChar(serialized.get("color").toString());
        part.hoverActionName = serialized.get("hoverActionName").toString();
        part.hoverActionData = (JsonRepresentedObject) serialized.get("hoverActionData");
        part.clickActionName = serialized.get("clickActionName").toString();
        part.clickActionData = serialized.get("clickActionData").toString();
        return part;
    }

    static {
        ConfigurationSerialization.registerClass(MessagePart.class);

        ImmutableBiMap.Builder<ChatColor, String> builder = ImmutableBiMap.builder();
        for (final ChatColor style : ChatColor.values()){
            if (!style.isFormat()) continue;

            String styleName;
            switch (style) {
            case MAGIC:
                styleName = "obfuscated";
                break;
            case UNDERLINE:
                styleName = "underlined";
                break;
            default:
                styleName = style.name().toLowerCase();
                break;
            }

            builder.put(style, styleName);
        }

        stylesToNames = builder.build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("color", color)
                .append("styles", styles)
                .append("clickActionName", clickActionName)
                .append("clickActionData", clickActionData)
                .append("hoverActionName", hoverActionName)
                .append("hoverActionData", hoverActionData)
                .append("text", text)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MessagePart that = (MessagePart) o;

        return new EqualsBuilder()
                .append(color, that.color)
                .append(styles, that.styles)
                .append(clickActionName, that.clickActionName)
                .append(clickActionData, that.clickActionData)
                .append(hoverActionName, that.hoverActionName)
                .append(hoverActionData, that.hoverActionData)
                .append(text, that.text)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(color)
                .append(styles)
                .append(clickActionName)
                .append(clickActionData)
                .append(hoverActionName)
                .append(hoverActionData)
                .append(text)
                .toHashCode();
    }
}
