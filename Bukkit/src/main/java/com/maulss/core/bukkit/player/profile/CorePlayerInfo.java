/*
 * Part of core.
 * Made on 09/09/2017
 */

package com.maulss.core.bukkit.player.profile;

import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.internal.CoreRank;
import com.maulss.core.database.DatabaseKey;

import java.util.Collections;

public class CorePlayerInfo extends PlayerInfo {

    public static final DatabaseKey
            NAME_HISTORY    = new CorePlayerInfo("nameHistory", Collections.emptyList()),
            IP_HISTORY      = new CorePlayerInfo("ipHistory",   Collections.emptyList()),
            FIRST_LOGIN     = new CorePlayerInfo("firstLogin",  null),
            LAST_LOGIN      = new CorePlayerInfo("lastLogin",   null),
            LAST_LOGOUT     = new CorePlayerInfo("lastLogout",  null),
            PLAYING         = new CorePlayerInfo("playing",     true),
            COINS           = new CorePlayerInfo("coins",       0),
            TOKENS          = new CorePlayerInfo("tokens",      0),
            RANK            = new CorePlayerInfo("rank",        CoreRank.DEFAULT),
            PREMIUM         = new CorePlayerInfo("premium",     false),
            LOCALE          = new CorePlayerInfo("locale",      Core.getSettings().getDefaultLocale());

    public CorePlayerInfo(final String key,
                          final Object def) {
        super(key, def);
    }
}