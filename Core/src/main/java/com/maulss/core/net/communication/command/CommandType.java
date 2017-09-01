/*
 * rv_core
 * 
 * Created on 15 July 2017 at 2:50 PM.
 */

package com.maulss.core.net.communication.command;

import org.apache.commons.lang3.text.WordUtils;

public enum CommandType {

    HANDSHAKE("Handshake"),
    UPDATE("Update"),
    QUERY("Query");

    private final String commandType;

    CommandType(final String commandType) {
        this.commandType = commandType;
    }

    public String getCommandType() {
        return commandType;
    }

    @Override
    public String toString() {
        return commandType;
    }

    public static CommandType of(String commandType) {
        return valueOf(WordUtils.capitalize(commandType.toLowerCase()));
    }
}