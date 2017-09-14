/*
 * Part of core.
 * 
 * Created on 15 July 2017 at 2:53 PM.
 */

package com.maulss.core.net.communication.command;

import com.google.gson.JsonElement;

import java.util.Optional;

public class ReturnCommand extends Command {

    public ReturnCommand(final String name,
                         final CommandType type) {
        super(name, type);
    }

    public ReturnCommand(final String name,
                         final CommandType type,
                         final Optional<JsonElement> commandData) {
        super(name, type, commandData);
    }
}