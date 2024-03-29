package com.maulss.core.bukkit.fanciful;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import com.maulss.core.bukkit.CoreLogger;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.util.MinecraftReflection;
import com.maulss.core.util.ArrayWrapper;
import com.maulss.core.util.Reflection;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Represents a formattable message. Such messages can use elements such as
 * colors, formatting codes, hover and click data, and other features provided
 * by the vanilla Minecraft <a href="http://minecraft.gamepedia.com/Tellraw#Raw_JSON_Text">JSON
 * message formatter</a>. This class allows plugins to emulate the functionality
 * of the vanilla Minecraft <a href="http://minecraft.gamepedia.com/Commands#tellraw">tellraw
 * command</a>. <p> This class follows the builder pattern, allowing for method
 * chaining. It is set up such that invocations of property-setting methods will
 * affect the current editing component, and a call to {@link #then()} or {@link
 * #then(String)} will append a new editing component to the end of the message,
 * optionally initializing it with text. Further property-setting method calls
 * will affect that editing component. </p>
 */
public class FancyMessage implements JsonRepresentedObject, Cloneable, Iterable<MessagePart>, ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(FancyMessage.class);
    }

    private List<MessagePart> messageParts;
    private String jsonString;
    private boolean dirty;

    private static Constructor<?> nmsPacketPlayOutChatConstructor;

    @Override
    public FancyMessage clone() throws CloneNotSupportedException {
        FancyMessage instance = (FancyMessage) super.clone();
        instance.messageParts = new ArrayList<>(messageParts.size());
        for (int i = 0; i < messageParts.size(); i++) {
            instance.messageParts.add(i, messageParts.get(i).clone());
        }
        instance.dirty = false;
        instance.jsonString = null;
        return instance;
    }

    /**
     * Creates a JSON message with text.
     *
     * @param firstPartText The existing text in the message.
     */
    public FancyMessage(final String firstPartText) {
        this(TextualComponent.rawText(firstPartText));
    }

    public FancyMessage(final TextualComponent firstPartText) {
        messageParts = new ArrayList<>();
        messageParts.add(new MessagePart(firstPartText));
        jsonString = null;
        dirty = false;

        if (nmsPacketPlayOutChatConstructor == null) {
            try {
                nmsPacketPlayOutChatConstructor = MinecraftReflection.getNMSClass("PacketPlayOutChat").getDeclaredConstructor(MinecraftReflection.getNMSClass("IChatBaseComponent"));
                nmsPacketPlayOutChatConstructor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                CoreLogger.log("Could not find Minecraft method or constructor: %s", e);
            } catch (SecurityException e) {
                CoreLogger.log("Could not access constructor: %s", e);
            }
        }
    }

    /**
     * Creates a JSON message without text.
     */
    public FancyMessage() {
        this((TextualComponent) null);
    }

    /**
     * Sets the text of the current editing component to a value.
     *
     * @param text The new text of the current editing component.
     *
     * @return This builder instance.
     * @throws IllegalStateException If the text for the current editing component has already been set.
     */
    public FancyMessage text(String text) {
        MessagePart latest = latest();
        if (latest.hasText()) {
            throw new IllegalStateException("text for this message part is already set");
        }

        latest.text = TextualComponent.rawText(text);
        dirty = true;
        return this;
    }

    public FancyMessage text(TextualComponent text) {
        MessagePart latest = latest();
        if (latest.hasText()) {
            throw new IllegalStateException("text for this message part is already set");
        }

        latest.text = text;
        dirty = true;
        return this;
    }

    /**
     * Sets the color of the current editing component to a value.
     *
     * @param color The new color of the current editing component.
     *
     * @return This builder instance.
     * @throws IllegalArgumentException If the specified enumeration value does not represent a color.
     */
    public FancyMessage color(final ChatColor color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException(color.name() + " is not a color");
        }

        latest().color = color;
        dirty = true;
        return this;
    }

    /**
     * Sets the stylization of the current editing component.
     *
     * @param styles The array of styles to apply to the editing component.
     *
     * @return This builder instance.
     * @throws IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
     */
    public FancyMessage style(ChatColor... styles) {
        for (final ChatColor style : styles) {
            if (!style.isFormat()) {
                throw new IllegalArgumentException(style.name() + " is not a style");
            }
        }

        latest().styles.addAll(Arrays.asList(styles));
        dirty = true;
        return this;
    }

    /**
     * Set the behavior of the current editing component to instruct the client to open a file on the client side filesystem when the currently edited part of the {@code FancyMessage} is clicked.
     *
     * @param path The path of the file on the client filesystem.
     *
     * @return This builder instance.
     */
    public FancyMessage file(final String path) {
        onClick("open_file", path);
        return this;
    }

    /**
     * Set the behavior of the current editing component to instruct the client to open a webpage in the client's web browser when the currently edited part of the {@code FancyMessage} is clicked.
     *
     * @param url The URL of the page to open when the link is clicked.
     *
     * @return This builder instance.
     */
    public FancyMessage link(final String url) {
        onClick("open_url", url);
        return this;
    }

    /**
     * Set the behavior of the current editing component to instruct the client to replace the chat input box content with the specified string when the currently edited part of the {@code FancyMessage} is clicked.
     * The client will not immediately send the command to the server to be executed unless the client player submits the command/chat message, usually with the enter key.
     *
     * @param command The text to display in the chat bar of the client.
     *
     * @return This builder instance.
     */
    public FancyMessage suggest(final String command) {
        onClick("suggest_command", command);
        return this;
    }

    /**
     * Set the behavior of the current editing component to instruct the client to send the specified string to the server as a chat message when the currently edited part of the {@code FancyMessage} is clicked.
     * The client <b>will</b> immediately send the command to the server to be executed when the editing component is clicked.
     *
     * @param command The text to display in the chat bar of the client.
     *
     * @return This builder instance.
     */
    public FancyMessage command(final String command) {
        onClick("run_command", command);
        return this;
    }

    /**
     * Set the behavior of the current editing component to display information about an achievement when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param name The name of the achievement to display, excluding the "achievement." prefix.
     *
     * @return This builder instance.
     */
    public FancyMessage achievementTooltip(final String name) {
        onHover("show_achievement", new JsonString("achievement." + name));
        return this;
    }

    /**
     * Set the behavior of the current editing component to display information about an achievement when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param which The achievement to display.
     *
     * @return This builder instance.
     */
    public FancyMessage achievementTooltip(final Achievement which) {
        try {
            Object achievement = Reflection.getMethod(MinecraftReflection.getOBCClass("CraftStatistic"), "getNMSAchievement", Achievement.class).invoke(null, which);
            return achievementTooltip((String) Reflection.getField(MinecraftReflection.getNMSClass("Achievement"), "name").get(achievement));
        } catch (IllegalAccessException e) {
            CoreLogger.log("Could not access method: %s", e);
            return this;
        } catch (IllegalArgumentException e) {
            CoreLogger.log("Argument could not be passed: %s", e);
            return this;
        } catch (InvocationTargetException e) {
            CoreLogger.log("A error has occurred during invoking of method: %s", e);
            return this;
        }
    }

    /**
     * Set the behavior of the current editing component to display information about a parameterless statistic when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param which The statistic to display.
     *
     * @return This builder instance.
     * @throws IllegalArgumentException If the statistic requires a parameter which was not supplied.
     */
    public FancyMessage statisticTooltip(final Statistic which) {
        Statistic.Type type = which.getType();
        if (type != Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic requires an additional " + type + " parameter!");
        }
        try {
            Object statistic = Reflection.getMethod(MinecraftReflection.getOBCClass("CraftStatistic"), "getNMSStatistic", Statistic.class).invoke(null, which);
            return achievementTooltip((String) Reflection.getField(MinecraftReflection.getNMSClass("Statistic"), "name").get(statistic));
        } catch (IllegalAccessException e) {
            CoreLogger.log("Could not access method: %s", e);
            return this;
        } catch (IllegalArgumentException e) {
            CoreLogger.log("Argument could not be passed: %s", e);
            return this;
        } catch (InvocationTargetException e) {
            CoreLogger.log("A error has occured durring invoking of method: %s", e);
            return this;
        }
    }

    /**
     * Set the behavior of the current editing component to display information about a statistic parameter with a material when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param which The statistic to display.
     * @param item  The sole material parameter to the statistic.
     *
     * @return This builder instance.
     * @throws IllegalArgumentException If the statistic requires a parameter which was not supplied, or was supplied a parameter that was not required.
     */
    public FancyMessage statisticTooltip(final Statistic which, Material item) {
        Statistic.Type type = which.getType();
        if (type == Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic needs no additional parameter!");
        }
        if ((type == Statistic.Type.BLOCK && item.isBlock()) || type == Statistic.Type.ENTITY) {
            throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
        }
        try {
            Object statistic = Reflection.getMethod(MinecraftReflection.getOBCClass("CraftStatistic"), "getMaterialStatistic", Statistic.class, Material.class).invoke(null, which, item);
            return achievementTooltip((String) Reflection.getField(MinecraftReflection.getNMSClass("Statistic"), "name").get(statistic));
        } catch (IllegalAccessException e) {
            CoreLogger.log("Could not access method: %s", e);
            return this;
        } catch (IllegalArgumentException e) {
            CoreLogger.log("Argument could not be passed: %s", e);
            return this;
        } catch (InvocationTargetException e) {
            CoreLogger.log("A error has occured durring invoking of method: %s", e);
            return this;
        }
    }

    /**
     * Set the behavior of the current editing component to display information about a statistic parameter with an entity type when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param which  The statistic to display.
     * @param entity The sole entity type parameter to the statistic.
     *
     * @return This builder instance.
     * @throws IllegalArgumentException If the statistic requires a parameter which was not supplied, or was supplied a parameter that was not required.
     */
    public FancyMessage statisticTooltip(final Statistic which, EntityType entity) {
        Statistic.Type type = which.getType();
        if (type == Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic needs no additional parameter!");
        }
        if (type != Statistic.Type.ENTITY) {
            throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
        }
        try {
            Object statistic = Reflection.getMethod(MinecraftReflection.getOBCClass("CraftStatistic"), "getEntityStatistic", Statistic.class, EntityType.class).invoke(null, which, entity);
            return achievementTooltip((String) Reflection.getField(MinecraftReflection.getNMSClass("Statistic"), "name").get(statistic));
        } catch (IllegalAccessException e) {
            CoreLogger.log("Could not access method: %s", e);
            return this;
        } catch (IllegalArgumentException e) {
            CoreLogger.log("Argument could not be passed: %s", e);
            return this;
        } catch (InvocationTargetException e) {
            CoreLogger.log("A error has occured durring invoking of method: %s", e);
            return this;
        }
    }

    /**
     * Set the behavior of the current editing component to display information about an item when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param itemJSON A string representing the JSON-serialized NBT data tag of an {@link ItemStack}.
     *
     * @return This builder instance.
     */
    public FancyMessage itemTooltip(final String itemJSON) {
        onHover("show_item", new JsonString(itemJSON)); // Seems a bit hacky, considering we have a JSON object as a parameter
        return this;
    }

    /**
     * Set the behavior of the current editing component to display information about an item when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param itemStack The stack for which to display information.
     *
     * @return This builder instance.
     */
    public FancyMessage itemTooltip(final ItemStack itemStack) {
        try {
            Object nmsItem = Reflection.getMethod(MinecraftReflection.getOBCClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, itemStack);
            return itemTooltip(Reflection.getMethod(MinecraftReflection.getNMSClass("ItemStack"), "save", MinecraftReflection.getNMSClass("NBTTagCompound")).invoke(nmsItem, MinecraftReflection.getNMSClass("NBTTagCompound").newInstance()).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    /**
     * Set the behavior of the current editing component to display raw text when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param text The text, which supports newlines, which will be displayed to the client upon hovering.
     *
     * @return This builder instance.
     */
    public FancyMessage tooltip(final String text) {
        onHover("show_text", new JsonString(text));
        return this;
    }

    /**
     * Set the behavior of the current editing component to display raw text when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param lines The lines of text which will be displayed to the client upon hovering. The iteration order of this object will be the order in which the lines of the tooltip are created.
     *
     * @return This builder instance.
     */
    public FancyMessage tooltip(final Iterable<String> lines) {
        tooltip(ArrayWrapper.toArray(lines, String.class));
        return this;
    }

    /**
     * Set the behavior of the current editing component to display raw text when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param lines The lines of text which will be displayed to the client upon hovering.
     *
     * @return This builder instance.
     */
    public FancyMessage tooltip(final String... lines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            builder.append(lines[i]);
            if (i != lines.length - 1) {
                builder.append('\n');
            }
        }
        tooltip(builder.toString());
        return this;
    }

    /**
     * Set the behavior of the current editing component to display formatted text when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param text The formatted text which will be displayed to the client upon hovering.
     *
     * @return This builder instance.
     */
    public FancyMessage formattedTooltip(FancyMessage text) {
        for (MessagePart component : text.messageParts) {
            if (component.clickActionData != null && component.clickActionName != null) {
                throw new IllegalArgumentException("The tooltip text cannot have click data.");
            } else if (component.hoverActionData != null && component.hoverActionName != null) {
                throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
            }
        }
        onHover("show_text", text);
        return this;
    }

    /**
     * Set the behavior of the current editing component to display the specified lines of formatted text when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param lines The lines of formatted text which will be displayed to the client upon hovering.
     *
     * @return This builder instance.
     */
    public FancyMessage formattedTooltip(FancyMessage... lines) {
        if (lines.length < 1) {
            onHover(null, null); // Clear tooltip
            return this;
        }

        FancyMessage result = new FancyMessage();
        result.messageParts.clear(); // Remove the one existing text component that exists by default, which destabilizes the object

        for (int i = 0; i < lines.length; i++) {
            try {
                for (MessagePart component : lines[i]) {
                    if (component.clickActionData != null && component.clickActionName != null) {
                        throw new IllegalArgumentException("The tooltip text cannot have click data.");
                    } else if (component.hoverActionData != null && component.hoverActionName != null) {
                        throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
                    }
                    if (component.hasText()) {
                        result.messageParts.add(component.clone());
                    }
                }
                if (i != lines.length - 1) {
                    result.messageParts.add(new MessagePart(TextualComponent.rawText("\n")));
                }
            } catch (CloneNotSupportedException e) {
                CoreLogger.log("Failed to clone object", e);
                return this;
            }
        }
        return formattedTooltip(result.messageParts.isEmpty() ? null : result); // Throws NPE if size is 0, intended
    }

    /**
     * Set the behavior of the current editing component to display the specified lines of formatted text when the client hovers over the text.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param lines The lines of text which will be displayed to the client upon hovering. The iteration order of this object will be the order in which the lines of the tooltip are created.
     *
     * @return This builder instance.
     */
    public FancyMessage formattedTooltip(final Iterable<FancyMessage> lines) {
        return formattedTooltip(ArrayWrapper.toArray(lines, FancyMessage.class));
    }

    /**
     * Terminate construction of the current editing component, and begin construction of a new message component.
     * After a successful call to this method, all setter methods will refer to a new message component, created as a result of the call to this method.
     *
     * @param text The text which will populate the new message component.
     *
     * @return This builder instance.
     */
    public FancyMessage then(final String text) {
        return then(TextualComponent.rawText(text));
    }

    /**
     * Terminate construction of the current editing component, and begin construction of a new message component.
     * After a successful call to this method, all setter methods will refer to a new message component, created as a result of the call to this method.
     *
     * @param text The text which will populate the new message component.
     *
     * @return This builder instance.
     */
    public FancyMessage then(final TextualComponent text) {
        if (!latest().hasText()) {
            throw new IllegalStateException("previous message part has no text");
        }
        messageParts.add(new MessagePart(text));
        dirty = true;
        return this;
    }

    /**
     * Terminate construction of the current editing component, and begin construction of a new message component.
     * After a successful call to this method, all setter methods will refer to a new message component, created as a result of the call to this method.
     *
     * @return This builder instance.
     */
    public FancyMessage then() {
        if (!latest().hasText()) {
            throw new IllegalStateException("previous message part has no text");
        }
        messageParts.add(new MessagePart());
        dirty = true;
        return this;
    }

    public void writeJson(JsonWriter writer) throws IOException {
        if (messageParts.size() == 1) {
            latest().writeJson(writer);
        } else {
            writer.beginObject().name("text").value("").name("extra").beginArray();
            for (final MessagePart part : this) {
                part.writeJson(writer);
            }
            writer.endArray().endObject();
        }
    }

    /**
     * Serialize this fancy message, converting it into syntactically-valid JSON using a {@link JsonWriter}.
     * This JSON should be compatible with vanilla formatter commands such as {@code /tellraw}.
     *
     * @return The JSON string representing this object.
     */
    public String toJSONString() {
        if (!dirty && jsonString != null) {
            return jsonString;
        }
        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter(string);
        try {
            writeJson(json);
            json.close();
        } catch (IOException e) {
            throw new RuntimeException("invalid message");
        }
        jsonString = string.toString();
        dirty = false;
        return jsonString;
    }

    /**
     * Sends this message to a player. The player will receive the fully-fledged formatted display of this message.
     *
     * @param player The player who will receive the message.
     */
    public void send(CorePlayer player, String jsonString) {
        try {
            Object handle = Reflection.getHandle(player.getPlayer());
            Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
            Reflection.getMethod(connection.getClass(), "sendPacket", MinecraftReflection.getNMSClass("Packet")).invoke(connection, createChatPacket(jsonString));
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void send(CommandSender sender, String jsonString) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(toOldMessageFormat());
        } else {
            send(CorePlayer.createIfAbsent((Player) sender), jsonString);
        }
    }

    // The ChatSerializer's instance of Gson
    private static Gson nmsChatSerializerGsonInstance;

    private Object createChatPacket(String json) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
        if (nmsChatSerializerGsonInstance == null) {
            // Find the field and its value, completely bypassing obfuscation
            for (Field declaredField : MinecraftReflection.getNMSClass("ChatSerializer").getDeclaredFields()) {
                if (Modifier.isFinal(declaredField.getModifiers()) && Modifier.isStatic(declaredField.getModifiers()) && declaredField.getType() == Gson.class) {
                    // We've found our field
                    declaredField.setAccessible(true);
                    nmsChatSerializerGsonInstance = (Gson) declaredField.get(null);
                    break;
                }
            }
        }

        // Since the method is so simple, and all the obfuscated methods have the same name, it's easier to reimplement 'IChatBaseComponent a(String)' than to reflectively call it
        // Of course, the implementation may change, but fuzzy matches might break with signature changes
        Object serializedChatComponent = nmsChatSerializerGsonInstance.fromJson(json, MinecraftReflection.getNMSClass("IChatBaseComponent"));

        return nmsPacketPlayOutChatConstructor.newInstance(serializedChatComponent);
    }

    /**
     * Sends this message to a command sender.
     * If the sender is a player, they will receive the fully-fledged formatted display of this message.
     * Otherwise, they will receive a version of this message with less formatting.
     *
     * @param sender The command sender who will receive the message.
     *
     * @see #toOldMessageFormat()
     */
    public void send(CommandSender sender) {
        send(sender, toJSONString());
    }

    /**
     * Sends this message to multiple command senders.
     *
     * @param senders The command senders who will receive the message.
     *
     * @see #send(CommandSender)
     */
    public void send(final Iterable<? extends CommandSender> senders) {
        String string = toJSONString();
        for (final CommandSender sender : senders) {
            send(sender, string);
        }
    }

    /**
     * Convert this message to a human-readable string with limited formatting.
     * This method is used to send this message to clients without JSON formatting support.
     * <p>
     * Serialization of this message by using this message will include (in this order for each message part):
     * <ol>
     * <li>The color of each message part.</li>
     * <li>The applicable stylizations for each message part.</li>
     * <li>The core text of the message part.</li>
     * </ol>
     * The primary omissions are tooltips and clickable actions. Consequently, this method should be used only as a last resort.
     * </p>
     * <p>
     * Color and formatting can be removed from the returned string by using {@link ChatColor#stripColor(String)}.</p>
     *
     * @return A human-readable string representing limited formatting in addition to the core text of this message.
     */
    public String toOldMessageFormat() {
        StringBuilder result = new StringBuilder();
        for (MessagePart part : this) {
            result.append(part.color == null ? "" : part.color);
            part.styles.forEach(result:: append);
            result.append(part.text);
        }
        return result.toString();
    }

    private MessagePart latest() {
        return messageParts.get(messageParts.size() - 1);
    }

    private void onClick(final String name, final String data) {
        final MessagePart latest = latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
        dirty = true;
    }

    private void onHover(final String name, final JsonRepresentedObject data) {
        final MessagePart latest = latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
        dirty = true;
    }

    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("messageParts", messageParts);
        return map;
    }

    /**
     * Deserializes a JSON-represented message from a mapping of key-value pairs.
     * This is called by the Bukkit serialization API.
     * It is not intended for direct public API consumption.
     *
     * @param serialized The key-value mapping which represents a fancy message.
     */
    public static FancyMessage deserialize(Map<String, Object> serialized) {
        FancyMessage msg = new FancyMessage();
        msg.messageParts = (List<MessagePart>) serialized.get("messageParts");
        msg.jsonString = serialized.containsKey("JSON") ? serialized.get("JSON").toString() : null;
        msg.dirty = !serialized.containsKey("JSON");
        return msg;
    }

    /**
     * <b>Internally called method. Not for API consumption.</b>
     */
    public Iterator<MessagePart> iterator() {
        return messageParts.iterator();
    }

    private static JsonParser _stringParser = new JsonParser();

    /**
     * Deserializes a fancy message from its JSON representation. This JSON representation is of the format of
     * that returned by {@link #toJSONString()}, and is compatible with vanilla inputs.
     *
     * @param json The JSON string which represents a fancy message.
     *
     * @return A {@code FancyMessage} representing the parameterized JSON message.
     */
    public static FancyMessage deserialize(String json) {
        JsonObject serialized = _stringParser.parse(json).getAsJsonObject();
        JsonArray extra = serialized.getAsJsonArray("extra"); // Get the extra component
        FancyMessage returnVal = new FancyMessage();
        returnVal.messageParts.clear();
        for (JsonElement mPrt : extra) {
            MessagePart component = new MessagePart();
            JsonObject messagePart = mPrt.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : messagePart.entrySet()) {
                // Deserialize text
                if (TextualComponent.isTextKey(entry.getKey())) {
                    // The map mimics the YAML serialization, which has a "key" field and one or more "value" fields
                    Map<String, Object> serializedMapForm = new HashMap<String, Object>(); // Must be object due to Bukkit serializer API compliance
                    serializedMapForm.put("key", entry.getKey());
                    if (entry.getValue().isJsonPrimitive()) {
                        // Assume string
                        serializedMapForm.put("value", entry.getValue().getAsString());
                    } else {
                        // Composite object, but we assume each element is a string
                        for (Map.Entry<String, JsonElement> compositeNestedElement : entry.getValue().getAsJsonObject().entrySet()) {
                            serializedMapForm.put("value." + compositeNestedElement.getKey(), compositeNestedElement.getValue().getAsString());
                        }
                    }
                    component.text = TextualComponent.deserialize(serializedMapForm);
                } else if (MessagePart.stylesToNames.inverse().containsKey(entry.getKey())) {
                    if (entry.getValue().getAsBoolean()) {
                        component.styles.add(MessagePart.stylesToNames.inverse().get(entry.getKey()));
                    }
                } else if (entry.getKey().equals("color")) {
                    component.color = ChatColor.valueOf(entry.getValue().getAsString().toUpperCase());
                } else if (entry.getKey().equals("clickEvent")) {
                    JsonObject object = entry.getValue().getAsJsonObject();
                    component.clickActionName = object.get("action").getAsString();
                    component.clickActionData = object.get("value").getAsString();
                } else if (entry.getKey().equals("hoverEvent")) {
                    JsonObject object = entry.getValue().getAsJsonObject();
                    component.hoverActionName = object.get("action").getAsString();
                    if (object.get("value").isJsonPrimitive()) {
                        // Assume string
                        component.hoverActionData = new JsonString(object.get("value").getAsString());
                    } else {
                        // Assume composite type
                        // The only composite type we currently store is another FancyMessage
                        // Therefore, recursion time!
                        component.hoverActionData = deserialize(object.get("value").toString() /* This should properly serialize the JSON object as a JSON string */);
                    }
                }
            }
            returnVal.messageParts.add(component);
        }
        return returnVal;
    }
}
