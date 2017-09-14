/*
 * Part of core.
 * Made on 01/09/2017
 */

package com.maulss.core.bukkit;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.maulss.core.bukkit.chat.filter.ChatBlockFilter;
import com.maulss.core.bukkit.chat.filter.ChatFilters;
import com.maulss.core.bukkit.inventory.CoreInventoryClickEvent;
import com.maulss.core.bukkit.inventory.item.IndexedItem;
import com.maulss.core.bukkit.player.profile.CoreProfile;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.collect.EnhancedMap;
import com.maulss.core.net.paster.PasteException;
import com.maulss.core.net.paster.Paster;
import com.maulss.core.text.StringUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public final class CoreSettings {

    private String defaultLocale = "english";

    private final EnhancedMap<String, String>
            messages			= new EnhancedMap<>();
    private final EnhancedMap<IndexedItem, Predicate<CoreProfile>>
            loginItems			= new EnhancedMap<>();
    private final EnhancedList<String>
            allowedCommands		= new EnhancedList<>(),
            locales				= new EnhancedList<>();
    private final EnhancedMap<String, CoreInventoryClickEvent>
            inventories			= new EnhancedMap<>();
    private final AtomicBoolean
            premiumChat			= new AtomicBoolean(false);
    private final ChatFilters
            chatFilters			= ChatFilters.getInstance();

    CoreSettings() {}

    public boolean isPremiumChat() {
        return premiumChat.get();
    }

    public void setPremiumChat(final boolean premiumChat) {
        this.premiumChat.set(premiumChat);
        Core.get().broadcast("premiumchat." + (premiumChat ? "enable" : "disable"));
    }

    public void addChatFilter(final ChatBlockFilter filter) {
        chatFilters.addFilter(Validate.notNull(filter));
    }

    public ChatFilters getChatFilters() {
        return chatFilters;
    }

    public void addAllowedCommands(final List<String> commands) {
        allowedCommands.addAll(commands);
    }

    public void addAllowedCommand(final String command) {
        allowedCommands.add(command);
    }

    public ImmutableList<String> getAllowedCommands() {
        return allowedCommands.getImmutableElements();
    }

    public boolean isCommandAllowed(String command) {
        return allowedCommands.contains(command);
    }

    public void addLoginItem(final IndexedItem item) {
        addLoginItem(item, player -> true);
    }

    public void addLoginItem(final IndexedItem item,
                             final Predicate<CoreProfile> action) {
        loginItems.put(Validate.notNull(item), Validate.notNull(action));
    }

    public void clearLoginItems() {
        loginItems.clear();
    }

    public ImmutableMap<IndexedItem, Predicate<CoreProfile>> getLoginItems() {
        return loginItems.getImmutableEntries();
    }

    public void registerInventory(final String path,
                                  final CoreInventoryClickEvent event) {
        inventories.put(Validate.notNull(path), Validate.notNull(event));
    }

    public void registerInventories(final Map<String, CoreInventoryClickEvent> events) {
        inventories.putAll(events);
    }

    public ImmutableMap<String, CoreInventoryClickEvent> getRegisteredInventories() {
        return inventories.getImmutableEntries();
    }

    public void registerCommands(final JavaPlugin plugin,
                                 final Map<String, CommandExecutor> commandExecutorMap) {
        for (Map.Entry<String, CommandExecutor> entry : commandExecutorMap.entrySet()) {
            plugin.getCommand(entry.getKey()).setExecutor(entry.getValue());
        }
    }

    public void registerListeners(final JavaPlugin instance,
                                  final Listener... listeners) {
        for (Listener listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }

    public void findAndRegisterLocales(final JavaPlugin plugin) {
        Validate.notNull(plugin);
        InputStream resource = plugin.getResource("locale/" + defaultLocale + ".yml");
        File localeDir = new File(String.format(
                "%s%slocale",
                plugin.getDataFolder().getParent(),
                File.separator
        ));

        if (resource != null) {
            ConfigFile.check(
                    new File(localeDir, defaultLocale + ".yml"),
                    resource // locale/english.yml
            );
        }

        findAndRegisterLocales(localeDir);
    }

    public void findAndRegisterLocales(final File directory) {
        Validate.notNull(directory);
        if (!directory.exists() && !directory.mkdirs()) return;

        for (File file : directory.listFiles()) {
            String configName = file.getName();
            String locale = StringUtil.remove(configName, ".yml").toLowerCase();

            addLocale(locale);

            FileConfiguration config = new YamlConfiguration();

            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                Core.get().log("Skipping loading %s: ");
                e.printStackTrace();
                continue;
            }

            for (String path : config.getKeys(true)) {
                String message = config.getString(path);
                addMessage(locale, path, message);
            }
        }
    }

    public String get(final String path) {
        return get(defaultLocale, path);
    }

    public String get(final String locale,
                      final String path) {
        if (locale == null || StringUtils.isEmpty(path) || path.contains(" ")) {
            return path;
        }

        String message = messages.get(locale.toLowerCase() + "." + path);
        if (message == null) message = messages.get(defaultLocale + "." + path);
        if (message == null || message.equalsIgnoreCase("null")) {
            return path.equals("chat.prefix") ? "" : path;
        }
        return message;
    }

    public void addMessage(String locale,
                           final String path,
                           final String message) {
        Validate.notNull(path);
        locale = locale.toLowerCase();

        if (!locales.contains(locale)) {
            locale = getDefaultLocale();
            addLocale(locale);
        }

        messages.put(
                locale + "." + path,
                StringUtils.isEmpty(message) || message.equalsIgnoreCase("null")
                        ? "null"
                        : ChatColor.translateAlternateColorCodes('&', message)
        );
    }

    public void addLocale(final String locale) {
        locales.addIf(
                Core.get().logIf(
                        !Strings.isNullOrEmpty(locale) && !locales.contains(locale),
                        "New locale detected: `%s`",
                        WordUtils.capitalize(locale)
                ), locale
        );
    }

    public boolean isLocaleRegistered(final String locale) {
        return locales.contains(locale);
    }

    public ImmutableList<String> getLocales() {
        return locales.getImmutableElements();
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(final String defaultLocale) {
        addLocale(this.defaultLocale = Validate.notNull(defaultLocale));
    }

    public ImmutableMap<String, String> getAllMessages() {
        return messages.getImmutableEntries();
    }

    public String getLocaleOrDefault(final String locale) {
        return locales.contains(locale.toLowerCase()) ? locale : getDefaultLocale();
    }

    public void initClasses(final String... classPaths) {
        for (String classPath : classPaths) {
            try {
                Class.forName(classPath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void tryPasteLocales() {
        Bukkit.getScheduler().runTaskAsynchronously(Core.get(), () -> {
            StringBuilder sb = new StringBuilder();
            String oldLocale = null;
            for (Map.Entry<String, String> msg : Core.getSettings().getAllMessages().entrySet()) {
                String[] parts = msg.getKey().split("\\.");
                String locale = parts[0];
                String path = StringUtils.join(ArrayUtils.subarray(parts, 1, parts.length + 1), ".");
                String message = Core.getSettings().get(locale, path);

                if (message != null && !message.equalsIgnoreCase("null")) {
                    if (message.startsWith("MemorySection")) continue;

                    message = ChatColor.stripColor(message);
                }

                if (oldLocale == null || !locale.equals(oldLocale)) {
                    oldLocale = locale;

                    sb.append("\n").append(locale).append(":");
                }

                sb.append(String.format("\n\t\"%-48s => \"%s\"", path + "\"", message));
            }

            try {
                Paster.hastebin(sb.toString()).paste();
            } catch (PasteException e) {
                Core.get().log("Did not paste locale-supported messages: %s", e);
            }
        });
    }
}