/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal;

import com.maulss.core.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.io.PrintStream;

import static org.apache.commons.lang3.Validate.notNull;

public final class ConsoleOutput extends PrintStream {

    private final Logger logger;
    private final ConsoleCommandSender console;

    public ConsoleOutput(final Logger logger,
                         final ConsoleCommandSender console) {
        super(logger.getOutput());
        this.logger = notNull(logger);
        this.console = notNull(console);
    }

    public ConsoleCommandSender getConsole() {
        return console;
    }

    @Override
    public void println() {
        print0(logger.getNoPrefixChar());
    }

    @Override
    public void println(final String x) {
        print0(x);
    }

    @Override
    public void println(final Object x) {
        print0(x);
    }

    private void print0(final Object object) {
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', object.toString()));
    }
}