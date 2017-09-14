/*
 * Part of core.
 * 
 * Created on 15 July 2017 at 2:52 PM.
 */

package com.maulss.core.net.communication.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Optional;

import static org.apache.commons.lang3.Validate.notNull;

public class Command {

    private final String name;
    private final CommandType type;
    private final Optional<JsonElement> commandData;

    public Command(final String name,
                   final CommandType type) {
        this(name, type, Optional.empty());
    }

    public Command(final String name,
                   final CommandType type,
                   final Optional<JsonElement> commandData) {
        this.name = notNull(name, "name");
        this.type = notNull(type, "type");
        this.commandData = notNull(commandData, "commandData");
    }

    public String getName() {
        return name;
    }

    public CommandType getType() {
        return type;
    }

    public Optional<JsonElement> getCommandData() {
        return commandData;
    }

    public ReturnCommand returnCommand() {
        return new ReturnCommand(name, type, commandData);
    }

    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("type", type.name());
        commandData.ifPresent(jsonElement -> obj.add("data", jsonElement));
        return obj;
    }

    public String toJson() {
        return toJsonObject().toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("type", type)
                .append("commandData", commandData)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        return new EqualsBuilder()
                .append(name, command.name)
                .append(type, command.type)
                .append(commandData, command.commandData)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(type)
                .append(commandData)
                .toHashCode();
    }
}