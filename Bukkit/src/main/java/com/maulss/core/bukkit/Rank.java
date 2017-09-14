/*
 * Part of core.
 * Made on 02/09/2017
 */

package com.maulss.core.bukkit;

import org.bukkit.ChatColor;

public interface Rank extends Comparable<Rank> {

    /**
     * Gets the rank id.
     *
     * <p>A higher rank {@code ID} correlates to a higher rank</p>
     *
     * @return the id
     */
    int getId();

    /**
     * Gets the rank's internal or friendly name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the rank's associated chat color.
     *
     * @return the color
     */
    ChatColor getColor();

    /**
     * Gets the rank's friendly display name
     *
     * <p>Includes the rank's chat color and friendly name</p>
     *
     * @return the display name
     */
    default String getDisplayName() {
        return getColor() + getName();
    }

    /**
     * Gets the rank's format, including colored prefix and display name.
     *
     * <p>Default format is {@code &8[%COLOR%%DISPLAY_NAME%&8]&7}</p>
     *
     * @return the format
     */
    String getFormat();

    @Override
    default int compareTo(Rank o) {
        return getId() - o.getId();
    }
}