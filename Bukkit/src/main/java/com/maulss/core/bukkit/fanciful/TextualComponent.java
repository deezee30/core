package com.maulss.core.bukkit.fanciful;

import com.google.common.collect.ImmutableMap;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a textual component of a message part.
 * This can be used to not only represent string literals in a JSON message,
 * but also to represent localized strings and other text values.
 */
public abstract class TextualComponent implements Cloneable {

    static {
        ConfigurationSerialization.registerClass(ArbitraryTextTypeComponent.class);
        ConfigurationSerialization.registerClass(ComplexTextTypeComponent.class);
    }

    @Override
    public String toString() {
        return getReadableString();
    }

    /**
         * @return The JSON key used to represent text components of this type.
     */
    public abstract String getKey();

        /**
         * @return A readable String
     */
    public abstract String getReadableString();

    /**
     * Clones a textual component instance.
     * The returned object should not reference this textual component instance, but should maintain the same key and value.
     */
    @Override
    public abstract TextualComponent clone() throws CloneNotSupportedException;

    /**
     * Writes the text data represented by this textual component to the specified JSON writer object.
     * A new object within the writer is not started.
     * @param writer The object to which to write the JSON data.
     * @throws IOException If an error occurs while writing to the stream.
     */
    public abstract void writeJson(final JsonWriter writer) throws IOException;

    public static TextualComponent deserialize(Map<String, Object> map){
        if (map.containsKey("key") && map.size() == 2 && map.containsKey("value")) {
            // Arbitrary text component
            return ArbitraryTextTypeComponent.deserialize(map);
        } else if(map.size() >= 2 && map.containsKey("key") && !map.containsKey("value") /* It contains keys that START WITH value */){
            // Complex JSON object
            return ComplexTextTypeComponent.deserialize(map);
        }

        return null;
    }

    static boolean isTextKey(String key) {
        return key.equals("translate") || key.equals("text") || key.equals("score") || key.equals("selector");
    }

    /**
     * Internal class used to represent all types of text components.
     * Exception validating done is on keys and values.
     */
    private static final class ArbitraryTextTypeComponent extends TextualComponent implements ConfigurationSerializable {

        private String key;
        private String value;

        public ArbitraryTextTypeComponent(final String key,
                                          final String value) {
            this.key = Validate.notNull(key);
            this.value = Validate.notNull(value);
        }

        @Override
        public TextualComponent clone() throws CloneNotSupportedException {
            // Since this is a private and final class, we can just reinstantiate this class instead of casting super.clone
            return new ArbitraryTextTypeComponent(key, value);
        }

        @Override
        public void writeJson(final JsonWriter writer) throws IOException {
            writer.name(key).value(value);
        }

        public Map<String, Object> serialize() {
            return new ImmutableMap.Builder<String, Object>()
                    .put("key", key)
                    .put("value", value)
                    .build();
        }

        public static ArbitraryTextTypeComponent deserialize(final Map<String, Object> map) {
            return new ArbitraryTextTypeComponent(map.get("key").toString(), map.get("value").toString());
        }

        @Override
        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String getReadableString() {
            return value;
        }
    }

    /**
     * Internal class used to represent a text component with a nested JSON value.
     * Exception validating done is on keys and values.
     */
    private static final class ComplexTextTypeComponent extends TextualComponent implements ConfigurationSerializable {

        private String key;
        private Map<String, String> value;

        public ComplexTextTypeComponent(final String key,
                                        final Map<String, String> value) {
            this.key = Validate.notNull(key);
            this.value = Validate.notNull(value);
        }

        @Override
        public TextualComponent clone() throws CloneNotSupportedException {
            // Since this is a private and final class, we can just reinstantiate this class instead of casting super.clone
            return new ComplexTextTypeComponent(key, value);
        }

        @Override
        public void writeJson(final JsonWriter writer) throws IOException {
            writer.name(key);
            writer.beginObject();

            for (Map.Entry<String, String> jsonPair : value.entrySet())
                writer.name(jsonPair.getKey()).value(jsonPair.getValue());

            writer.endObject();
        }

        public Map<String, Object> serialize() {
            return new HashMap<String, Object>(2) {{
                put("key", getKey());

                for (Entry<String, String> valEntry : value.entrySet())
                    put("value." + valEntry.getKey(), valEntry.getValue());
            }};
        }

        public static ComplexTextTypeComponent deserialize(final Map<String, Object> map) {
            String key = null;
            Map<String, String> value = new HashMap<>();
            for (Map.Entry<String, Object> valEntry : map.entrySet()) {
                if (valEntry.getKey().equals("key")) {
                    key = (String) valEntry.getValue();
                } else if (valEntry.getKey().startsWith("value.")) {
                    value.put((valEntry.getKey()).substring(6) /* Strips out the value prefix */, valEntry.getValue().toString());
                }
            }

            return new ComplexTextTypeComponent(key, value);
        }

        @Override
        public String getKey() {
            return key;
        }

        public Map<String, String> getValue() {
            return value;
        }

        @Override
        public String getReadableString() {
            return key;
        }
    }

    /**
     * Create a textual component representing a string literal.
     * This is the default type of textual component when a single string literal is given to a method.
     * @param textValue The text which will be represented.
     * @return The text component representing the specified literal text.
     */
    public static TextualComponent rawText(final String textValue) {
        return new ArbitraryTextTypeComponent("text", textValue);
    }

    /**
     * Create a textual component representing a localized string.
     * The client will see this text component as their localized version of the specified string <em>key</em>, which can be overridden by a resource pack.
     * <p>
     * If the specified translation key is not present on the client resource pack, the translation key will be displayed as a string literal to the client.
     * </p>
     * @param translateKey The string key which maps to localized text.
     * @return The text component representing the specified localized text.
     */
    public static TextualComponent localizedText(final String translateKey) {
        return new ArbitraryTextTypeComponent("translate", translateKey);
    }

    private static void throwUnsupportedSnapshot() {
        throw new UnsupportedOperationException("This feature is only supported in snapshot releases.");
    }

    /**
     * Create a textual component representing a scoreboard value.
     * The client will see their own score for the specified objective as the text represented by this component.
     * <p>
     * <b>This method is currently guaranteed to throw an {@code UnsupportedOperationException} as it is only supported on snapshot clients.</b>
     * </p>
     * @param scoreboardObjective The name of the objective for which to display the score.
     * @return The text component representing the specified scoreboard score (for the viewing player), or {@code null} if an error occurs during JSON serialization.
     */
    public static TextualComponent objectiveScore(final String scoreboardObjective) {
        return objectiveScore("*", scoreboardObjective);
    }

    /**
     * Create a textual component representing a scoreboard value.
     * The client will see the score of the specified player for the specified objective as the text represented by this component.
     * <p>
     * <b>This method is currently guaranteed to throw an {@code UnsupportedOperationException} as it is only supported on snapshot clients.</b>
     * </p>
     * @param playerName The name of the player whos score will be shown. If this string represents the single-character sequence "*", the viewing player's score will be displayed.
     * Standard minecraft selectors (@a, @p, etc) are <em>not</em> supported.
     * @param scoreboardObjective The name of the objective for which to display the score.
     * @return The text component representing the specified scoreboard score for the specified player, or {@code null} if an error occurs during JSON serialization.
     */
    public static TextualComponent objectiveScore(final String playerName,
                                                  final String scoreboardObjective) {
        throwUnsupportedSnapshot(); // Remove this line when the feature is released to non-snapshot versions, in addition to updating ALL THE OVERLOADS documentation accordingly

        return new ComplexTextTypeComponent("score", ImmutableMap.<String, String>builder()
                .put("name", playerName)
                .put("objective", scoreboardObjective)
                .build());
    }

    /**
     * Create a textual component representing a player name, retrievable by using a standard minecraft selector.
     * The client will see the players or entities captured by the specified selector as the text represented by this component.
     * <p>
     * <b>This method is currently guaranteed to throw an {@code UnsupportedOperationException} as it is only supported on snapshot clients.</b>
     * </p>
     * @param selector The minecraft player or entity selector which will capture the entities whose string representations will be displayed in the place of this text component.
     * @return The text component representing the name of the entities captured by the selector.
     */
    public static TextualComponent selector(final String selector) {
        throwUnsupportedSnapshot(); // Remove this line when the feature is released to non-snapshot versions, in addition to updating ALL THE OVERLOADS documentation accordingly

        return new ArbitraryTextTypeComponent("selector", selector);
    }
}
