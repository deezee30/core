/*
 * Part of core.
 */

package com.maulss.core.bukkit.internal.config;

import com.maulss.core.bukkit.Core;

import java.util.List;

public final class MainConfig extends CoreConfigFile {

    private static final MainConfig INSTANCE = new MainConfig();

    public boolean debug;
    public String defaultLocale;
    public boolean chatFormat;
    public int chatClearLines;
    public int maxMessages;
    public int chatSpamViolationsPermitted;
    public int chatViolationCooldown;
    public boolean efficientWorldManagement;
    public List<String> allowedCmds;

    private MainConfig() {
        Core.get().logger().enableDebugging(debug);
    }

    @Override
    protected String getConfigName() {
        return "config.yml";
    }

    @Override
    protected String[] getPaths() {
        return new String[] {
                "debug",
                "default-locale",

                "chat.format",
                "chat.clear-lines",
                "chat.max-messages" ,
                "chat.spam-violations-permitted",
                "chat.violation-cooldown",

                "efficient-world-management",

                "allowed-commands-when-disabled"
        };
    }

    public static boolean isDebug() {
        return INSTANCE.debug;
    }

    public static String getDefaultLocale() {
        return INSTANCE.defaultLocale;
    }

    public static boolean doFormatChat() {
        return INSTANCE.chatFormat;
    }

    public static int getClearChatLines() {
        return INSTANCE.chatClearLines;
    }

    public static int getMaxMessages() {
        return INSTANCE.maxMessages;
    }

    public static int getChatSpamViolationsPermitted() {
        return INSTANCE.chatSpamViolationsPermitted;
    }

    public static int getChatViolationCooldown() {
        return INSTANCE.chatViolationCooldown;
    }

    public static boolean isEfficientWorldManagement() {
        return INSTANCE.efficientWorldManagement;
    }

    public static List<String> getAllowedCommands() {
        return INSTANCE.allowedCmds;
    }
}