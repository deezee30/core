/*
 * Part of core.
 * Made on 30/07/2017
 */

package com.maulss.core.text;

import java.util.ArrayList;
import java.util.List;

public final class SimpleScroller implements Scroller {

    private static final long serialVersionUID = -3296965334328559328L;

    private String scroll = null;
    private int position;
    private List<String> list;

    /**
     * @param message      The String to scroll
     * @param width        The width of the window to scroll across
     * @param spaceBetween The amount of spaces between each repetition
     */
    public SimpleScroller(String message,
                          int width,
                          int spaceBetween) {
        if (message.length() <= width) {
            scroll = message;
            return;
        }

        list = new ArrayList<>();

        if (message.length() < width) {
            StringBuilder sb = new StringBuilder(message);
            while (sb.length() < width) sb.append(" ");
            message = sb.toString();
        }

        width -= 2;

        if (width < 1) width = 1;
        if (spaceBetween < 0) spaceBetween = 0;

        for (int i = 0; i < message.length() - width; i++)
            list.add(message.substring(i, i + width));

        StringBuilder space = new StringBuilder();
        for (int i = 0; i < spaceBetween; ++i) {
            list.add(message.substring(message.length() - width + (i > width ? width : i), message.length()) + space);
            if (space.length() < width) space.append(" ");
        }

        for (int i = 0; i < width - spaceBetween; ++i)
            list.add(message.substring(message.length() - width + spaceBetween + i, message.length()) + space + message.substring(0, i));

        for (int i = 0; i < spaceBetween; i++) {
            if (i > space.length()) break;
            list.add(space.substring(0, space.length() - i) + message.substring(0, width - (spaceBetween > width ? width : spaceBetween) + i));
        }
    }

    /**
     * @return the next String to display
     */
    @Override
    public String next() {
        if (scroll != null) return scroll;

        return list.get(position++ % list.size());
    }

    @Override
    public String getOriginal() {
        return scroll;
    }
}