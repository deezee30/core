/*
 * Part of core.
 * Made on 13/09/2017
 */

package com.maulss.core.bukkit;

import com.maulss.core.CoreException;
import com.maulss.core.Logger;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.Optional;

public final class CoreLogger {

    private static CoreLogger instance;
    private final Core core;

    public CoreLogger(final Core core) throws CoreException {
        Validate.notNull(core, "core");

        if (instance != null)
            throw new CoreException("CoreLogger has already been initialized");

        instance = this;
        this.core = core;
    }

    public static Optional<String> log(final String path,
                                       final Object... components) {
        return instance.core.log(path, components);
    }

    public static Optional<String> log(final String path,
                                       final String[] keys,
                                       final Object... vals) {
        return instance.core.log(path, keys, vals);
    }

    public static Optional<String> log(final String path,
                                       final Map<String, Object> replacements) {
        return instance.core.log(path, replacements);
    }

    public static boolean logIf(final boolean check,
                                final String path,
                                final Object... components) {
        return instance.core.logIf(check, path, components);
    }

    public static boolean logIf(final boolean check,
                                final String path,
                                final String[] keys,
                                final Object... vals) {
        return instance.core.logIf(check, path, keys, vals);
    }

    public static boolean logIf(final boolean check,
                                final String path,
                                final Map<String, Object> replacements) {
        return instance.core.logIf(check, path, replacements);
    }

    public static Optional<String> debug(final String string,
                                         final Object... components) {
        return instance.core.debug(string, components);
    }

    public static boolean debugIf(final boolean check,
                                  final String string,
                                  final Object... components) {
        return instance.core.debugIf(check, string, components);
    }

    public static void broadcast(final String message,
                                 final Object... components) {
        instance.core.broadcast(message, components);
    }

    public static void broadcast(final String path,
                                 final String[] keys,
                                 final Object... vals) {
        instance.core.broadcast(path, keys, vals);
    }

    public static void broadcast(final String path,
                                 final Map<String, Object> replacements) {
        instance.core.broadcast(path, replacements);
    }

    public static Logger get() {
        if (instance == null)
            throw new RuntimeException("Core logger hasn't been set up yet");

        return instance.core.logger;
    }
}