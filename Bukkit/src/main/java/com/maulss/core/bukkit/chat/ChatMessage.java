/*
 * Part of core.
 * 
 * Created on 14 June 2017 at 11:00 PM.
 */

package com.maulss.core.bukkit.chat;

import com.google.gson.JsonObject;
import com.maulss.core.bukkit.player.profile.Profile;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * The type Chat message.
 */
public class ChatMessage implements Serializable {

    private final Profile sender;
    private final String message;
    private final long date;
    private final boolean cancelled;

    /**
     * Instantiates a new Chat message.
     *
     * @param sender    the sender of the message
     * @param message   the message
     * @param cancelled whether or not the message
     *                  was cancelled as a result of
     *                  {@link com.maulss.core.bukkit.chat.filter.ChatBlockFilter}s
     */
    public ChatMessage(final Profile sender,
                       final String message,
                       final boolean cancelled) {
        this.sender = sender;
        this.message = message;
        this.date = System.currentTimeMillis();
        this.cancelled = cancelled;
    }

    /**
     * Gets the sender.
     *
     * @return the sender
     */
    public Profile getSender() {
        return sender;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public long getDate() {
        return date;
    }

    /**
     * Is cancelled boolean.
     *
     * @return the boolean
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * To json object json object.
     *
     * @return the json object
     */
    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("sender", sender.getUuid().toString());
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("date", date);
        jsonObject.addProperty("cancelled", cancelled);

        return jsonObject;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sender", sender)
                .append("message", message)
                .append("date", date)
                .append("cancelled", cancelled)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ChatMessage that = (ChatMessage) o;

        return new EqualsBuilder()
                .append(date, that.date)
                .append(cancelled, that.cancelled)
                .append(sender, that.sender)
                .append(message, that.message)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(sender)
                .append(message)
                .append(date)
                .append(cancelled)
                .toHashCode();
    }
}