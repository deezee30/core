/*
 * rv_core
 * 
 * Created on 15 July 2017 at 2:53 PM.
 */

package com.maulss.core.net.communication.command;

import com.maulss.core.collect.EnhancedMap;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.Optional;

public final class CommandRegistry {

    private final EnhancedMap<Command, CommandProcess> commands = new EnhancedMap<>();

    public CommandProcess add(final Command command,
                              final CommandProcess process) {
        Validate.notNull(command, "command");
        Validate.notNull(process, "process");
        return commands.put(command, process);
    }

    public Optional<Command> getCommand(final String command) {
        Validate.notNull(command, "command");
        for (Map.Entry<Command, CommandProcess> entry : commands.entrySet()) {
            Command cmd = entry.getKey();
            if (cmd.getName().equalsIgnoreCase(command)) {
                return Optional.of(cmd);
            }
        }

        return Optional.empty();
    }

    public Optional<CommandProcess> getProcess(final String command) {
        return getCommand(command).flatMap(this::getProcess);
    }

    public Optional<CommandProcess> getProcess(final Command command) {
        Validate.notNull(command, "command");
        return Optional.ofNullable(commands.get(command));
    }

    public boolean isRegistered(final String command) {
        return getCommand(command).isPresent();
    }
}