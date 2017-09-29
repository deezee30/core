/*
 * Part of core.
 */

package com.maulss.core.bukkit.imgtext;

import org.bukkit.ChatColor;

import java.awt.*;

public final class ColorUtil {

    private static final Color[] colors = {
            new Color(0, 0, 0),		new Color(0, 0, 170),		new Color(0, 170, 0),		new Color(0, 170, 170),
            new Color(170, 0, 0),	    new Color(170, 0, 170),	new Color(255, 170, 0),	new Color(170, 170, 170),
            new Color(85, 85, 85),	    new Color(85, 85, 255),	new Color(85, 255, 85),	new Color(85, 255, 255),
            new Color(255, 85, 85),	new Color(255, 85, 255),	new Color(255, 255, 85),	new Color(255, 255, 255),
    };

    public static double getDistance(final Color c1,
                                     final Color c2) {
        double rmean = (c1.getRed() + c2.getRed()) / 2.0;
        double r = c1.getRed() - c2.getRed();
        double g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return (2 + rmean / 256.0) * r * r + (4.0) * g * g + (2 + (255 - rmean) / 256.0) * b * b;
    }

    public static boolean areIdentical(final Color c1,
                                       final Color c2) {
        return Math.abs(c1.getRed() - c2.getRed()) <= 5 &&
                Math.abs(c1.getGreen() - c2.getGreen()) <= 5 &&
                Math.abs(c1.getBlue() - c2.getBlue()) <= 5;
    }

    public static ChatColor getClosestChatColor(final Color color) {
        if (color.getAlpha() < 128)
            return null;

        int index = 0;
        double best = -1;

        for (int i = 0; i < colors.length; i++)
            if (areIdentical(colors[i], color))
                return ChatColor.values()[i];

        for (int i = 0; i < colors.length; i++) {
            double distance = getDistance(color, colors[i]);
            if (distance < best || best == -1) {
                best = distance;
                index = i;
            }
        }

        return ChatColor.values()[index];
    }

    public static String center(final String s,
                                final int length) {
        if (s.length() > length)
            return s.substring(0, length);
        else if (s.length() == length)
            return s;
        else {
            int leftPadding = (length - s.length()) / 2;
            StringBuilder leftBuilder = new StringBuilder();
            for (int i = 0; i < leftPadding; i++)
                leftBuilder.append(" ");
            return leftBuilder.toString() + s;
        }
    }

    private ColorUtil() {}
}