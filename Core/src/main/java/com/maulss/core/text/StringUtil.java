/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.text;

import com.google.common.collect.ImmutableList;
import com.google.common.net.InetAddresses;
import org.apache.commons.lang3.Validate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

    // Disable initialization
    private StringUtil() {}

    /**
     * Removes a specific section of the {@param origin} string
     * by replacing that section with an empty string.
     *
     * <p>  Neither of the {@code String}s used for parameters can
     * take formatting regex.  {@code String}s must be plain.</p>
     *
     * @param   origin
     *          The super string that needs to be modofied.
     * @param   sequence
     *          The section of {@param origin} that needs to be removed.
     * @return  The new string with {@param sequence} removed.
     * @see     String#replace(CharSequence, CharSequence)
     */
    public static String remove(final String origin,
                                final String sequence) {
        return Validate.notNull(origin, "Cannot remove sequence from null string")
                .replace(Validate.notNull(sequence, "Cannot remove null sequence from string"), "");
    }

    /**
     * This method uses a region to check case-insensitive equality.
     * This means the internal array does not need to be copied
     * like a {@code toLowerCase()} call would.
     *
     * @param   string
     *          String to check.
     * @param   prefix
     *          Prefix of string to compare.
     * @return  true if provided string starts with,
     *          ignoring case, the prefix provided.
     * @throws  NullPointerException
     *          If prefix is null.
     * @throws  IllegalArgumentException
     *          If string is null.
     * @see     String#regionMatches(boolean, int, String, int, int)
     */
    public static boolean startsWithIgnoreCase(final String string,
                                               final String prefix)
            throws IllegalArgumentException, NullPointerException {
        return Validate.notNull(string, "Cannot check a null string for a match").length()
                >= prefix.length()
                && string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static String checkPlural(final String singular,
                                     final String plural, int count) {
        return count == 1 ? singular : plural;
    }

    public static ImmutableList<String> splitMaintainBounds(final String longString,
                                                            final int maxLength) {
        final Matcher m = Pattern.compile("\\G\\s*(.+" + maxLength + "})(?=\\s|$)", Pattern.DOTALL).matcher(longString);
        final ImmutableList.Builder<String> strings = new ImmutableList.Builder<>();
        while (m.find()) strings.add(m.group(1));

        return strings.build();
    }

    public static String getStringFromStringList(final List<String> list) {
        StringBuilder string = new StringBuilder();

        if (list.size() == 1)
            string.append(list.get(0));

        for (String s : list)
            string.append(s).append(", ");

        if (string.length() > 3)
            string = new StringBuilder(string.substring(0, string.length() - 2));

        return string.toString();
    }

    public static boolean containsInetAddress(final String string) {
        String[] parts = string.split("\\s+");

        for (String part : parts) {
            if (InetAddresses.isInetAddress(part)) return true;
        }

        return false;
    }

    public static boolean containsAddress(String string) {
        String[] parts = string.split("\\s+");

        for (String part : parts) {
            if (isAddress(part)) return true;
        }

        return false;
    }

    public static boolean isAddress(String string) {
        try {
            new URL(string);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}