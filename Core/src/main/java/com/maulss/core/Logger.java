/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core;

import com.maulss.core.text.StringUtil;
import org.apache.commons.lang3.Validate;

import java.io.Console;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Easy to use tool for quickly working with {@code String}s in
 * order to output them, modify them for use in messages and etc.
 *
 * @author	Maulss
 * @see     StringUtil
 * @see     Console
 * @see     Formatter
 * @see     PrintStream
 */
public class Logger {

    /**
     * Used for separating large amounts of texts to simplify reading
     */
    public static final String BORDER = "---------------------------------------";

    /**
     * Used for suppressing output where a logger is required
     */
    public static final Logger SUPPRESSED_LOGGER = new Logger(new PrintStream(new OutputStream() {
        @Override
        public void write(int dummy) {}
    }));

    private boolean     debug           = false;
    private String      prefix          = "-> ";
    private String      debugPrefix     = "[DEBUG] -> ";
    private char        noPrefixChar    = 126; // char "~"
    private PrintStream output          = System.out;


    /**
     * Sets up logger with default configurations.
     */
    public Logger() {}


    /**
     * Sets up logger with prefix before every message.
     *
     * @param prefix The prefix to use before each outputting message
     */
    public Logger(final String prefix) {
        this.prefix = Validate.notNull(prefix);
    }


    /**
     * Sets up logger with prefix before every message and the output
     * where the stream should be sent.
     *
     * @param prefix The prefix to use before each outputting message
     * @param output Where the stream should be outputted
     */
    public Logger(final String prefix,
                  final PrintStream output) {
        this.prefix = Validate.notNull(prefix);
        this.output = Validate.notNull(output);
    }


    /**
     * Sets up logger with the output where the stream should be sent.
     *
     * @param output Where the stream should be outputted
     */
    public Logger(final PrintStream output) {
        this.output = Validate.notNull(output);
    }


    /**
     * Logs a message to the console if it's present using
     * {@link Console#format(String, Object...)}.  If a console is not
     * present nothing will be logged, although the formatted return string
     * will still be returned.
     *
     * <p>There will be a global prefix in front of the output text that
     * can be set using {@link #setPrefix(String)}.  By default, the prefix
     * is set to {@code EnhancedLib -{@literal >} }.  If for a reason the
     * programmer does <b>not</b> want to include the prefix before the
     * outputted string, they can add a defined {@link char} at the very
     * start, which can be set using {@link #setNoPrefixChar(char)}.
     *  The default {@link #noPrefixChar} {@code char} is by ID of {@code 126}
     * ({@code ~}).</p>
     *
     * <p>{@link Formatter} codes are available to be used in the
     * {@param string} with components being used in {@param components}.</p>
     *
     * @param   string
     *          String to be formatted with {@link Formatter}'s
     *          default formatting layout.
     * @param   components
     *          Arguments referenced by the format specifiers in the format
     *          string.  If there are more arguments than format specifiers,
     *          the extra arguments are ignored.  The number of arguments is
     *          variable and may be zero.
     * @return  The already formatted string that has been outputted to
     *          the console.  If {@link IllegalFormatException} was
     *          thrown, the string returned remains unformatted.
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.
     * @see     Console#format(String, Object...)
     * @see     Console#printf(String, Object...)
     * @see     Formatter
     * @see     #prefix(String, String)
     * @see     #setPrefix(String)
     * @see     #setNoPrefixChar(char)
     */
    public Optional<String> log(final String string,
                                final Object... components) {
        return write(output, prefix(string, prefix), components);
    }


    /**
     * Logs a message to the console if it's present and the provided check
     * is true using {@link Console#format(String , Object...)}.  If a
     * console is not present nothing will be logged.
     *
     * <p>There will be a global prefix in front of the output text that
     * can be set using {@link #setPrefix(String)}.  By default, the prefix
     * is set to {@code EnhancedLib -{@literal >} }.  If for a reason the
     * programmer does <b>not</b> want to include the prefix before the
     * outputted string, they can add a defined {@link char} at the very
     * start, which can be set using {@link #setNoPrefixChar(char)}.
     *  The default {@link #noPrefixChar} {@code char} is by ID of {@code 126}
     * ({@code ~}).</p>
     *
     * <p>{@link Formatter} codes are available to be used in the
     * {@param string} with components being used in {@param components}.</p>
     *
     * @param   check
     *          The boolean that needs to be checked in order for the message
     *          to be outputted.  If {@code check == false}, nothing happen.
     * @param   string
     *          String to be formatted with {@link Formatter}'s
     *          default formatting layout.
     * @param   components
     *          Arguments referenced by the format specifiers in the format
     *          string.  If there are more arguments than format specifiers,
     *          the extra arguments are ignored.  The number of arguments is
     *          variable and may be zero.
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.
     * @return  The value of {@param check}.
     * @see     Console#printf(String , Object...)
     * @see     Console#format(String , Object...)
     * @see     Formatter
     * @see     #prefix(String , String)
     * @see     #setPrefix(String)
     * @see     #setNoPrefixChar(char)
     */
    public boolean logIf(final boolean check,
                         final String string,
                         final Object... components) {
        if (check) log(string, components);
        return check;
    }


    /**
     * <p>Debugs a message to the console if it's present and {@link
     * #enableDebugging(boolean)} is set to {@code true}.  This method works
     * like an ordinary {@link #log(String , Object...)} method would except
     * it only outputs the message to the console if debugging is enabled,
     * which should only be the case during testing as it can otherwise spam
     * the console with many useless material.  If a console is not present
     * nothing will be logged, although the formatted return string will
     * still be returned.  The prefix in front of the message output will be
     * a combination of a predetermined {@link #setDebugPrefix(String)}
     * {@code +} {@link #setPrefix(String)}.</p>
     *
     * <p>There will be a global prefix in front of the output text that can
     * be set using {@link #setPrefix(String)}.  By default, the prefix is
     * set to {@code EnhancedLib -{@literal >} }.  If for a reason the programmer
     * does <b>not</b> want to include the prefix before the outputted string,
     * they can add a defined {@link char} at the very start, which can be
     * set using {@link #setNoPrefixChar(char)}.  The default {@link #noPrefixChar}
     * {@code char} is by ID of {@code 126} ({@code ~}).</p>
     *
     * <p>{@link Formatter} codes are available to be used in the
     * {@param string} with components being used in {@param components}.</p>
     *
     * @param   string
     *          String to be formatted with {@link Formatter}'s
     *          default formatting layout.
     * @param   components
     *          Arguments referenced by the format specifiers in the format
     *          string.  If there are more arguments than format specifiers,
     *          the extra arguments are ignored.  The number of arguments is
     *          variable and may be zero.
     * @return  The already formatted string that has been outputted to
     *          the console.  If {@link IllegalFormatException} was
     *          thrown, the string returned remains unformatted.
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.
     * @see     Console#format(String , Object...)
     * @see     Console#printf(String , Object...)
     * @see     Formatter
     * @see     #prefix(String , String)
     * @see     #setPrefix(String)
     * @see     #setNoPrefixChar(char)
     * @see     #setDebugPrefix(String)
     * @see     #enableDebugging(boolean)
     */
    public Optional<String> debug(final String string,
                                  final Object... components) {
        String msg = prefix(string, debugPrefix);
        return debug ? write(System.out, msg, components) : Optional.of(msg);
    }


    /**
     * <p>Debugs a message to the console if it's present, the provided check
     * is true using {@link Console#format(String , Object...)} and {@link
     * #enableDebugging(boolean)} is set to {@code true}.  If a console is
     * not present nothing will be logged.  This method works like an ordinary
     * {@link #logIf(boolean, String, Object...)} method would except it only
     * outputs the message to the console if debugging is enabled, which
     * should only be the case during testing as it can otherwise spam the
     * console with many useless material.  If a console is not present nothing
     * will be logged, although the formatted return string will still be
     * returned.  The prefix in front of the message output will be a combination
     * of a predetermined {@link #setDebugPrefix(String)} {@code +} {@link
     * #setPrefix(String)}.</p>
     *
     * <p>There will be a global prefix in front of the output text that can
     * be set using {@link #setPrefix(String)}.  By default, the prefix is
     * set to {@code EnhancedLib -{@literal >} }.  If for a reason the programmer
     * does <b>not</b> want to include the prefix before the outputted string,
     * they can add a defined {@link char} at the very start, which can be
     * set using {@link #setNoPrefixChar(char)}.  The default {@link #noPrefixChar}
     * {@code char} is by ID of {@code 126} ({@code ~}).</p>
     *
     * <p>{@link Formatter} codes are available to be used in the
     * {@param string} with components being used in {@param components}.</p>
     *
     * @param   check
     *          The boolean that needs to be checked in order for the message
     *          to be outputted.  If {@code check == false}, nothing happen.
     * @param   string
     *          String to be formatted with {@link Formatter}'s
     *          default formatting layout.
     * @param   components
     *          Arguments referenced by the format specifiers in the format
     *          string.  If there are more arguments than format specifiers,
     *          the extra arguments are ignored.  The number of arguments is
     *          variable and may be zero.
     * @return  The already formatted string that has been outputted to
     *          the console.  If {@link IllegalFormatException} was
     *          thrown, the string returned remains unformatted.
     * @throws  IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.
     * @see     Console#format(String , Object...)
     * @see     Console#printf(String , Object...)
     * @see     Formatter
     * @see     #prefix(String , String)
     * @see     #setPrefix(String)
     * @see     #setNoPrefixChar(char)
     */
    public boolean debugIf(final boolean check,
                           final String string,
                           final Object... components) {
        if (check) debug(string, components);
        return check;
    }


    /**
     * @return  Whether or not debugging is allowed, AKA value inputted for {@link
     *          #enableDebugging(boolean)}.  By default the value is {@code false}.
     * @see     #setDebugPrefix(String)
     * @see     #debug(String , Object...)
     * @see     #debugIf(boolean , String , Object...)
     */
    public boolean debugEnabled() {
        return debug;
    }


    /**
     * Turns debugging on and off.
     *
     * <p>Messages sent through {@link #debug(String,
     * Object...)} and {@link #debugIf(boolean, String, Object...)} are
     * only outputted when this is enabled, which console with many useless
     * material.</p>
     *
     * @param   debug
     *          Whether or not to enable debugging.
     * @return  The value of {@param debug}.
     * @see     #setDebugPrefix(String)
     * @see     #debug(String, Object...)
     * @see     #debugIf(boolean, String, Object...)
     */
    public boolean enableDebugging(final boolean debug) {
        log("Debugging has been %s", debug ? "enabled" : "disabled");
        return this.debug = debug;
    }


    /**
     * Sets a prefix that will be outputted before every message in the console
     * provided it is present.
     *
     * <p>By default, the prefix is set to {@code EnhancedLib -{@literal >} }.
     *  If for a reason the programmer does <b>not</b> want to include the
     * prefix before the outputted string, they can add a defined {@link char}
     * at the very start, which can be set using {@link #setNoPrefixChar(char)}.
     *  The default {@link #noPrefixChar} {@code char} is by ID of {@code 126}
     * ({@code ~}).</p>
     *
     * @param   prefix
     *          The string that will be placed before every outputted message.
     * @return  The value of {@param prefix}.
     * @see     #log(String, Object...)
     * @see     #setNoPrefixChar(char)
     */
    public String setPrefix(final String prefix) {
        return this.prefix = prefix;
    }


    /**
     * Sets a prefix that will be outputted before every <b>debugging</b>
     * message in the console provided it is present.
     *
     * <p>By default, the prefix is set to {@code [DEBUG] }.  If for a
     * reason the programmer does <b>not</b> want to include the prefix
     * before the outputted string, they can add a defined {@link char} at
     * the very start, which can be set using {@link #setNoPrefixChar(char)}.
     * The default {@link #noPrefixChar} {@code char} is by ID of {@code 126}
     * ({@code ~}).</p>
     *
     * @param   debugPrefix
     *          The string that will be placed before every outputted message.
     * @return  The value of {@param prefix}.
     * @see     #debug(String , Object...)
     * @see     #setNoPrefixChar(char)
     */
    public String setDebugPrefix(final String debugPrefix) {
        return this.debugPrefix = debugPrefix;
    }


    /**
     * Sets the {@code char} that should be used before the outputting message
     * to indicate that the prefix should not be used.
     *
     * <p>If for a reason the programmer does <b>not</b> want to include the
     * prefix before the outputted string, they can add a defined {@link char}
     * at the very start, which can be set here.  The default {@link #noPrefixChar}
     * {@code char} is by ID of {@code 126} ({@code ~}).</p>
     *
     * @param   noPrefixChar
     *          {@code char} that should be used before the outputting message
     *          to indicate that the prefix should not be used.
     * @return  The value of {@param noPrefixChar}.
     * @see     #prefix(String, String)
     * @see     #setPrefix(String)
     * @see     #log(String, Object...)
     */
    public char setNoPrefixChar(final char noPrefixChar) {
        return this.noPrefixChar = noPrefixChar;
    }


    /**
     * @return  {@code char} that should be used before the outputting message
     *          to indicate that the prefix should not be used.
     * @see     #setNoPrefixChar(char)
     * @see     #prefix(String, String)
     * @see     #setNoPrefixChar(char)
     * @see     #log(String, Object...)
     */
    public char getNoPrefixChar() {
        return noPrefixChar;
    }


    public String prefix(String string,
                         final String prefix) {
        if (string == null) return null;
        notNull(prefix);

        boolean usePrefix = true;
        if (string.startsWith(String.valueOf(noPrefixChar))) {
            usePrefix = false;
            string = string.substring(1);
        }

        return (usePrefix ? prefix : "") + string;
    }


    public PrintStream getOutput() {
        return output;
    }


    public PrintStream setOutput(final PrintStream output) {
        return this.output = output;
    }


    public static String buildMessage(String message,
                                      final Object... components) {
        if (message != null) {
            // Replace null components with "null" string
            for (int x = 0; x < components.length; ++x) {
                final Object o = components[x];

                if (o == null) components[x] = "null";
            }

            try {
                message = String.format(message, components);
            } catch (IllegalFormatException ignored) {}
        }

        return message;
    }


    public static String constructReplacements(final String string,
                                               String[] keys,
                                               Object... vals) {
        final int keysLen = keys.length;
        final int valsLen = vals.length;
        final int min = Math.min(keysLen, valsLen);

        // Remove nulls from arrays
        keys = Arrays.copyOf(keys, keysLen);
        vals = Arrays.copyOf(vals, valsLen);

        Map<String, Object> replacements = new HashMap<>(min);

        for (int x = 0; x < min; ++x) {
            replacements.put(keys[x], vals[x]);
        }

        return constructReplacements(string, replacements);
    }


    public static String constructReplacements(String string,
                                               final Map<String, Object> replacements) {
        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            final String key = entry.getKey();
            final Object val = entry.getValue();

            if (key != null && val != null) {
                string = string.replace(key, val.toString());
            }
        }

        return string;
    }


    private static Optional<String> write(final PrintStream output,
                                          String message,
                                          final Object... components) {
        message = buildMessage(message, components);
        output.println(message);
        return Optional.of(message);
    }
}