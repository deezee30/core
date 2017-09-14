/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.config;

import com.maulss.core.Logger;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.CoreSettings;
import com.maulss.core.bukkit.internal.ConsoleOutput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class MessagesConfig {

    private static final Core PLUGIN_INSTANCE = Core.get();
    private static final CoreSettings SETTINGS = Core.getSettings();

    static {
        SETTINGS.findAndRegisterLocales(PLUGIN_INSTANCE);

        Logger logger = Core.get().logger();
        logger.setDebugPrefix("Core [Debug] -> ");
        logger.setNoPrefixChar((char) 126);
        logger.setPrefix(
                ChatColor.translateAlternateColorCodes('&', SETTINGS.get("chat.prefix"))
        );

        logger.setOutput(new ConsoleOutput(logger, Bukkit.getConsoleSender()));

        SETTINGS.tryPasteLocales();
    }

    private MessagesConfig() {}
}