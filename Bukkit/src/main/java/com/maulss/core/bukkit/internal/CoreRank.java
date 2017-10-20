/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal;

import com.google.common.collect.ImmutableList;
import com.maulss.core.Logger;
import com.maulss.core.bukkit.Rank;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public final class CoreRank implements Rank {

    private static List<CoreRank> RANKS = new ArrayList<>(5);

    public static final CoreRank
            DEFAULT = new CoreRank(0,       "Member",   ChatColor.GRAY),
            HELPER  = new CoreRank(4,       "Helper",   ChatColor.AQUA),
            MOD     = new CoreRank(5,       "Mod",      ChatColor.GREEN),
            DEV     = new CoreRank(10,      "Dev",      ChatColor.DARK_GREEN),
            ADMIN   = new CoreRank(9999,    "Admin",    ChatColor.BLUE);

    static {
        RANKS = ImmutableList.copyOf(RANKS);
    }

    private final int id;
    private final String name;
    private final ChatColor color;

    private CoreRank(int id, String name, ChatColor color) {
        this.id = id;
        this.name = name;
        this.color = color;
        RANKS.add(this);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChatColor getColor() {
        return color;
    }

    @Override
    public String getFormat() {
        return ChatColor.translateAlternateColorCodes('&',
                Logger.buildMessage("&8[%s&8]&7", getDisplayName()));
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @param name
     *         the name of the rank to get
     * @return The rank according to the name specified
     */
    public static CoreRank byName(String name) {
        Validate.notNull(name);

        for (CoreRank rank : RANKS) {
            if (rank.name.equalsIgnoreCase(name)) return rank;
        }

        return DEFAULT;
    }

    public static List<CoreRank> values() {
        return RANKS;
    }
}