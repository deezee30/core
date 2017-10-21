/*
 * Part of core.
 * Made on 09/09/2017
 */

package com.maulss.core.bukkit.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.maulss.core.CoreException;
import com.maulss.core.Logger;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.CoreSettings;
import com.maulss.core.bukkit.Rank;
import com.maulss.core.bukkit.hologram.HologramViewer;
import com.maulss.core.bukkit.internal.CoreRank;
import com.maulss.core.bukkit.internal.config.MainConfig;
import com.maulss.core.bukkit.inventory.item.CoreItemStackList;
import com.maulss.core.bukkit.inventory.item.IndexedItem;
import com.maulss.core.bukkit.packet.AbstractPacket;
import com.maulss.core.bukkit.player.event.*;
import com.maulss.core.bukkit.player.manager.InventoryManager;
import com.maulss.core.bukkit.player.manager.ViolationManager;
import com.maulss.core.bukkit.player.profile.CorePlayerInfo;
import com.maulss.core.bukkit.player.profile.CoreProfile;
import com.maulss.core.bukkit.player.profile.Profile;
import com.maulss.core.bukkit.player.statistic.CoinsHolder;
import com.maulss.core.bukkit.player.statistic.PremiumHolder;
import com.maulss.core.bukkit.player.statistic.RankHolder;
import com.maulss.core.bukkit.player.statistic.TokensHolder;
import com.maulss.core.bukkit.util.Firework;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.database.DatabaseKey;
import com.maulss.core.database.Value;
import com.maulss.core.database.ValueType;
import com.maulss.core.service.timer.Timer;
import com.maulss.core.util.MathUtil;
import com.mongodb.async.client.MongoCollection;
import org.apache.commons.lang3.text.WordUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * An online-only version of any user
 * ({@link Profile}) that is online.
 *
 * <p>This class can only have a single instance per online
 * player, per server.  Online player instances are stored
 * in {@link com.maulss.core.bukkit.player.manager.CorePlayerManager}.</p>
 */
public final class  CorePlayer
        extends     CoreProfile
        implements  HologramViewer, RankHolder, CoinsHolder, TokensHolder, PremiumHolder {

    private static final Core
            CORE            = Core.get();
    private static final CoreSettings
            SETTINGS        = Core.getSettings();
    private static final MongoCollection<Document>
            COLLECTION      = Core.getPlayerCollection();
    private static final PluginManager
            PLUGIN_MANAGER  = CORE.getServer().getPluginManager();

    /*
     * Cached profiles may include names/uuids of players that may not exist.
     * To check if they are a real profile simply call hasPlayed() or hasEverPlayed().
     */
    private static final EnhancedList<CorePlayer>
            CACHED_PLAYERS  = new EnhancedList<>();

    /*
     * Delegates to the virtual in-game player that this class is wrapped around
     * if that player is connected. If not, this can be null and many operations
     * will be unsupported.
     */
    private transient Player
            player          = null;

    /*
     * Used as a callback for concurrent loading of offline players.
     */
    private transient Consumer<CorePlayer>
            onLoad          = null;

    /*
     * Represents an inventory GUI manager
     */
    private transient InventoryManager
            invManager      = new InventoryManager(this);

    /*
     * Represents a violation manager
     */
    private transient ViolationManager
            violations      = new ViolationManager(this);

    /*
     * Recorded IP and name histories including the current present ones
     */
    private transient EnhancedList<String>
            ipHistory       = new EnhancedList<>(),
            nameHistory     = new EnhancedList<>();

    /*
     * Chosen locale pack by the player, synced across the network.
     * Unless chosen specifically, resorts to default, which is usually English
     */
    private transient String
            locale          = SETTINGS.getLocaleOrDefault(MainConfig.getDefaultLocale());

    /*
     * Player's rank. Resorts to default if wasn't applied
     */
    private transient Rank
            rank            = CoreRank.DEFAULT;

    /*
     * Useful utilities for general management
     */
    private transient boolean
            // Whether or not the player logged into the current server for the first time
            newcomer        = true,
            // Whether or not other players can see this player
            vanished        = false,
            // Premium (VIP) status
            premium         = false,
            // Whether or not the player can currently use commands.
            // There are exceptions that can be set in the CoreSettings
            commandsBlocked = false,
            // Whether or not the player can freely talk in chat
            muted           = false,
            // Whether or not the player can absorb damage
            damageable      = true,
            // Whether or not the player can build and/or break blocks
            constructable   = true,
            // Whether or not the player is allowed to move
            movable         = true,
            // Whether or not the player's hunger bar can change
            hungry          = true;

    /*
     * Sometimes applied to new additions of coins as a factor
     */
    private transient float
            coinMultiplier  = 1.0f;

    /*
     * General cross-server economy management
     */
    private transient int
            coins           = 0,
            tokens          = 0;

    // ================================ //
    // ==== Construction ============== //
    // ================================ //

    // For offline or fake players
    private CorePlayer(final String name,
                       final Consumer<CorePlayer> callback) {
        super(name);
        this.onLoad = callback;
        CACHED_PLAYERS.add(this);
    }

    // For offline or fake players
    private CorePlayer(final UUID uuid,
                       final Consumer<CorePlayer> callback) {
        super(uuid);
        this.onLoad = callback;
        CACHED_PLAYERS.add(this);
    }

    // For online connected players
    private CorePlayer(final Player player,
                       final String assumedHostName) {
        super(player.getUniqueId(), player.getName());

        this.player = player;

        // Player's name and stats may have changed since his last query
        // - Remove him from cache
        CACHED_PLAYERS.remove(this);

        nameHistory.add(player.getName());
        ipHistory.add(assumedHostName);
    }

    @Override
    public void onLoad(final Optional<Document> document) {
        newcomer = document.isPresent();

        if (newcomer) {
            // Player has played before - get document
            Document stats = document.get();

            locale  = stats.getString(CorePlayerInfo.LOCALE.getKey());
            rank    = CoreRank.byName(stats.getString(CorePlayerInfo.RANK.getKey()));
            premium = stats.getBoolean(CorePlayerInfo.PREMIUM.getKey());
            coins   = stats.getInteger(CorePlayerInfo.COINS.getKey());
            tokens  = stats.getInteger(CorePlayerInfo.TOKENS.getKey());

            // Load name history
            List<String> nameHistory = (List<String>) stats.get(CorePlayerInfo.NAME_HISTORY.getKey());
            this.nameHistory.ensureCapacity(nameHistory.size());
            nameHistory.forEach(s -> this.nameHistory.addIf(!nameHistory.contains(s), s));

            // Load IP history
            List<String> ipHistory = (List<String>) stats.get(CorePlayerInfo.IP_HISTORY.getKey());
            this.ipHistory.ensureCapacity(ipHistory.size());
            ipHistory.forEach(s -> this.ipHistory.addIf(!ipHistory.contains(s), s));

            if (!isOnline()) {
                if (onLoad != null)
                    onLoad.accept(this);
                return;
            }

            // Submit bulk update if the user is connected
            update(new ImmutableMap.Builder<DatabaseKey, Value>()
                    .put(CorePlayerInfo.NAME,           new Value<>(getName()))
                    .put(CorePlayerInfo.NAME_HISTORY,   new Value<>(nameHistory))
                    .put(CorePlayerInfo.IP_HISTORY,     new Value<>(ipHistory))
                    .put(CorePlayerInfo.PLAYING,        new Value<>(true))
                    .put(CorePlayerInfo.LAST_LOGIN,     new Value<>(System.currentTimeMillis() / 1000L))
                    .build());

        } else {
            if (!isOnline()) {
                if (onLoad != null)
                    onLoad.accept(null);
                return;
            }

            // Player never played before - insert new document if the player is connected
            Map<DatabaseKey, Object> doc = Maps.newHashMap();

            CorePlayerInfo.UUID.append(doc, getUuid());
            CorePlayerInfo.NAME.append(doc, getName());
            CorePlayerInfo.NAME_HISTORY.append(doc, nameHistory);
            CorePlayerInfo.IP_HISTORY.append(doc, ipHistory);
            CorePlayerInfo.FIRST_LOGIN.append(doc, System.currentTimeMillis() / 1000L);
            CorePlayerInfo.LAST_LOGIN.append(doc, System.currentTimeMillis() / 1000L);
            CorePlayerInfo.LAST_LOGOUT.append(doc);
            CorePlayerInfo.PLAYING.append(doc);
            CorePlayerInfo.COINS.append(doc);
            CorePlayerInfo.TOKENS.append(doc);
            CorePlayerInfo.RANK.append(doc);
            CorePlayerInfo.PREMIUM.append(doc);
            CorePlayerInfo.LOCALE.append(doc);

            insert(doc, error -> {
                if (error != null) {
                    getLogger().debug("Failed to insert '%s' into db: %s", getName(), error);
                    error.printStackTrace();
                } else {
                    getLogger().debug("New player '%s' successfully created", getName());
                }
            });
        }

        getLogger().debug("%s's language is %s", getName(), WordUtils.capitalize(locale));

        // Delay task until the CraftPlayer instance has been fully loaded
        new BukkitRunnable() {

            @Override
            public void run() {

                // Check how long it takes to load the player in other events via Part of core.
                final Timer eventTimer = new Timer().start();
                eventTimer.onFinishExecute(() -> getLogger().debug(
                        "'%s' was loaded in other plugins in %sms",
                        getName(), eventTimer.getTime(TimeUnit.MILLISECONDS)));

                /*
                 * Call event for other plugins using Part of core.
                 * to load CorePlayer instances after this instance
                 * loads. Make sure player actually logged in and
                 * is online before calling all other events.
                 */
                if (player.isOnline()) {
                    player.setDisplayName(getRank().getColor() + player.getName());

                    CorePlayerPostLoadEvent event = new CorePlayerPostLoadEvent(
                            CorePlayer.this, newcomer);

                    CORE.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        player.kickPlayer("You have been refused access to the server!");
                    }

                    // Give player the default items
                    giveLoginItems();
                } else {
                    // Player has been revoked access to server
                    destroy();
                }

                eventTimer.forceStop();
            }
        }.runTask(CORE);
    }

    // ================================ //
    // ==== Player data =============== //
    // ================================ //

    public Player getPlayer() {
        if (!isOnline()) throw new PlayerNotConnectedException();
        return player;
    }

    /**
     * @return  Whether or not this is the first time the
     *          player is playing this server.
     */
    public boolean isNew() {
        return newcomer;
    }

    @Override
    public String getDisplayName() {
        return isOnline() ? player.getDisplayName() : getName();
    }

    public String getIp() {
        if (isOnline()) return player.getAddress().getHostName();
        else return ipHistory.isEmpty() ? null : ipHistory.get(ipHistory.size() - 1);
    }

    public EnhancedList<String> getIpHistory() {
        return ipHistory;
    }

    public EnhancedList<String> getNameHistory() {
        return nameHistory;
    }

    @Override
    public ImmutableList<String> getStatisticValues() {
        if (!played) {
            try {
                throw new CoreException("Player " + getName() + " never played before!");
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }

        return new ImmutableList.Builder<String>()
                .add("~")
                .add("~&e======= " + getDisplayName() + (newcomer ? " &4&lNEW " : " ") + "&e=======")
                .add("~&eRank: " + getRank().getDisplayName())
                .add("~&eCoins: &3" + coins)
                .add("~&eTokens: &3" + tokens)
                .add("~&ePremium: " + (isPremium() ? "&2True" : "&4False"))
                .add("~&eCurrently " + (isOnline() ? "&2Online" : "&4Offline"))
                .add("~")
                .build();
    }

    // ================================ //
    // ==== Coins ===================== //
    // ================================ //

    @Override
    public int getCoins() {
        return coins;
    }

    @Override
    public void setCoins(Value<Integer> value,
                         final boolean applyMultiplier) throws CoinValueChangeException {
        // If possible, apply coin multiplier
        if (applyMultiplier && value.getType().equals(ValueType.GIVE)) {
            value = new Value<>(
                    MathUtil.floor((double) value.getValue() * getCoinMultiplier()),
                    ValueType.GIVE
            );
        }

        CoinValueChangeEvent event = new CoinValueChangeEvent(this, value);
        PLUGIN_MANAGER.callEvent(event);
        if (!event.isCancelled()) {
            int newCoins = event.getNewCoins();

            update(CorePlayerInfo.COINS, newCoins, (result, t) -> {
                // Update cache only if database update was successful
                if (t == null) coins = newCoins;
            });
        }
    }

    @Override
    public float getCoinMultiplier() {
        return coinMultiplier;
    }

    @Override
    public void setCoinMultiplier(final float factor) {
        this.coinMultiplier = factor;
    }

    // ================================ //
    // ==== Tokens ==================== //
    // ================================ //

    @Override
    public int getTokens() {
        return tokens;
    }

    @Override
    public void setTokens(final Value<Integer> value) throws TokenValueChangeException {
        TokenValueChangeEvent event = new TokenValueChangeEvent(this, value);
        PLUGIN_MANAGER.callEvent(event);
        if (!event.isCancelled()) {
            int newTokens = event.getNewTokens();

            update(CorePlayerInfo.TOKENS, newTokens, (result, t) -> {
                // Update cache only if database update was successful
                if (t == null) tokens = newTokens;
            });
        }
    }

    // ================================ //
    // ==== Premium =================== //
    // ================================ //

    @Override
    public boolean isPremium() {
        return premium;
    }

    @Override
    public void setPremium(final boolean premium) {
        PremiumStatusChangeEvent event = new PremiumStatusChangeEvent(this, premium);
        PLUGIN_MANAGER.callEvent(event);
        if (!event.isCancelled()) {
            update(CorePlayerInfo.PREMIUM, premium, (result, t) -> {
                // Update cache only if database update was successful
                if (t == null) this.premium = premium;
            });
        }
    }

    // ================================ //
    // ==== Ranking =================== //
    // ================================ //

    @Override
    public Rank getRank() {
        return rank;
    }

    @Override
    public void setRank(final Rank rank) {
        setRank(rank, true);
    }

    public void setRank(final Rank rank,
                        final boolean updateDisplayName) {
        RankChangeEvent event = new RankChangeEvent(this, rank);
        PLUGIN_MANAGER.callEvent(event);
        if (!event.isCancelled()) {
            update(CorePlayerInfo.RANK, rank, (result, t) -> {
                // Update cache only if database update was successful
                if (t == null) {
                    this.rank = rank;
                    if (updateDisplayName) {
                        player.setDisplayName(rank.getFormat() + " " + getName());
                    }
                }
            });
        }
    }

    /**
     * @return  whether or not the player is defined
     *          as a helper *or higher* in the database
     */
    public final boolean isHelper() {
        return rank instanceof CoreRank && isAllowedFor(CoreRank.HELPER);
    }

    /**
     * @return  whether or not the player is defined
     *          as a mod *or higher* in the database
     */
    public final boolean isMod() {
        return rank instanceof CoreRank && isAllowedFor(CoreRank.MOD);
    }

    /**
     * @return  whether or not the player is defined
     *          as an admin in the database
     */
    public final boolean isAdmin() {
        return rank instanceof CoreRank && isAllowedFor(CoreRank.ADMIN);
    }

    // ================================ //
    // ==== Locale Management ========= //
    // ================================ //

    /**
     * Gets the player's selected cached language.
     *
     * @return the locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Updates the current locale of the player in cache and database.
     *
     * <p>The locale provided must be registered on the current server
     * session via {@link CoreSettings#addLocale(String)}</p>
     *
     * @param   locale
     *          The new locale for the player.
     * @return  The provided locale.
     */
    public String setLocale(final String locale) {
        return setLocale(locale, false);
    }

    /**
     * Updates the current locale of the player in cache and database.
     *
     * <p>The locale provided must be registered on the current server
     * session via {@link CoreSettings#addLocale(String)}</p>
     *
     * <p>If {@param refreshLoginItems} is {@code true}, the player's
     * login items ({@link CoreSettings#getLoginItems()}) are given to
     * the player again, in case they have been localized.</p>
     *
     * @param   locale
     *          The new locale for the player.
     * @param   refreshLoginItems
     *          If the login items should be refreshed
     * @return  The provided locale.
     */
    public String setLocale(final String locale,
                            final boolean refreshLoginItems) {
        update(CorePlayerInfo.LOCALE, locale, (result, t) -> {
            if (t == null) this.locale = locale;
        });

        if (refreshLoginItems)
            giveLoginItems();

        return locale;
    }

    /**
     * Sends multiple localized messages from the locales via the provided
     * paths and replacements.
     *
     * <p>The returned messages from the paths are always formattable
     * via {@link java.util.Formatter}.</p>
     *
     * <p>If the message returned from the path provided is equal
     * to the path provided, the message returned is taken as an
     * already defined message.</p>
     *
     * <p>If the message returned equals {@link Logger#getNoPrefixChar()}
     * or if the path returned is still remained as a path, then the
     * message is blocked.</p>
     *
     * @param   paths
     *          Multiple paths for the messages.
     * @param   components
     *          The replacements for variables.
     * @throws  NullPointerException
     *          If any component is {@code null}.
     * @throws  java.util.IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section
     *          of the formatter class specification.
     * @see     CoreSettings#get(String, String)
     * @see     java.util.Formatter
     * @see     String#format(String, Object...)
     * @see     #sendMessage(String, Object...)
     * @see     #sendMessage(String, String[], Object...)
     * @see     #sendMessage(String, Map)
     */
    public void sendMessages(final String[] paths,
                             final Object... components) {
        for (String path : paths) {
            sendMessage(path, components);
        }
    }

    /**
     * Sends a localized message from the locales via the provided
     * path and replacements.
     *
     * <p>The returned message from the path is always formattable
     * via {@link java.util.Formatter}.</p>
     *
     * <p>If the message returned from the path provided is equal
     * to the path provided, the message returned is taken as an
     * already defined message.</p>
     *
     * <p>If the message returned equals {@link Logger#getNoPrefixChar()}
     * or if the path returned is still remained as a path, then the
     * message is blocked.</p>
     *
     * @param   path
     *          The path for the message.
     * @param   components
     *          The replacements for variables.
     * @throws  NullPointerException
     *          If any component is {@code null}.
     * @throws  java.util.IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section
     *          of the formatter class specification.
     * @see     CoreSettings#get(String, String)
     * @see     java.util.Formatter
     * @see     String#format(String, Object...)
     * @see     #sendMessage(String, String[], Object...)
     * @see     #sendMessage(String, Map)
     */
    public void sendMessage(final String path,
                            final Object... components) {
        if (!isOnline() || path == null) return;

        String message = SETTINGS.get(locale, path);

        /*
         * Check if the path actually exists in the messages cache.
         * If not, block the message if it's a path or send it if it's not.
         */
        if (message.equals(path)) {
            if (!path.contains(" ") && !path.equals(String.valueOf(getLogger().getNoPrefixChar()))) {
                getLogger().debug("Blocking message '%s' from player %s (locale: %s)", path, this, locale);
                return;
            }
        }

        handleMessage(String.format(message, components));
    }

    /**
     * Sends a localized message from the locales via the provided
     * path and replacements.
     *
     * <p>If the message returned from the path provided is equal
     * to the path provided, the message returned is taken as an
     * already defined message.</p>
     *
     * <p>If the message returned equals {@link Logger#getNoPrefixChar()}
     * or if the path returned is still remained as a path, then the
     * message is blocked.</p>
     *
     * <p>If the amount of {@param keys} does not equal to the amount of
     * {@param values}, the minimum of the two will be the number of
     * iterations (replacements) performed.</p>
     *
     * <p>If either any of the {@param keys} or {@param values} is {@code
     * null} then that iteration is skipped.</p>
     *
     * <p>For example, invoking:
     * <code>
     * sendMessage(
     *         "Hello, %NAME%!",        // Constructed message. Can also be a path.
     *         new String[] {"%NAME%"}, // A String array of variables to search
     *         "Steve"                  // An array of replacements to be used instead of variables in order.
     * );
     * </code>
     * would build the message {@code Hello, Steve!}.</p>
     *
     * @param   path
     *          The path for the message. Can also be a plain message.
     * @param   keys
     *          The String array of variables to search.
     * @param   vals
     *          An array of replacements to be used instead of variables
     *          in order.
     * @see     CoreSettings#get(String, String)
     * @see     Logger#constructReplacements(String, String[], Object...)
     * @see     #sendMessage(String, Object...)
     * @see     #sendMessage(String, Map)
     */
    public void sendMessage(final String path,
                            final String[] keys,
                            final Object... vals) {
        handleMessage(Logger.constructReplacements(SETTINGS.get(locale, path), keys, vals));
    }

    /**
     * Sends a localized message from the locales via the provided
     * path and replacements.
     *
     * <p>If the message returned from the path provided is equal
     * to the path provided, the message returned is taken as an
     * already defined message.</p>
     *
     * <p>If the message returned equals {@link Logger#getNoPrefixChar()}
     * or if the path returned is still remained as a path, then the
     * message is blocked.</p>
     *
     * <p>If either any of the keys or values in {@param replacements}
     * is {@code null} then that iteration is skipped.</p>
     *
     * <p>For example, invoking:
     * <code>
     * sendMessage(
     *         "Hello, %NAME%!",                          // Constructed message. Can also be a path.
     *         new ImmutableMap.Builder<String, Object>() // A map of variables and their replacements.
     *         .put("%NAME%", "Steve")
     *         .build(),
     * );
     * </code>
     * would build the message {@code Hello, Steve!}.</p>
     *
     * @param   path
     *          The path for the message. Can also be a plain message.
     * @param   replacements
     *          A map of keys (variables to replace) and their values
     *          (replacements) that correspond to each other.
     * @see     CoreSettings#get(String, String)
     * @see     Logger#constructReplacements(String, Map)
     * @see     #sendMessage(String, Object...)
     * @see     #sendMessage(String, String[], Object...)
     */
    public void sendMessage(final String path,
                            final Map<String, Object> replacements) {
        handleMessage(Logger.constructReplacements(SETTINGS.get(locale, path), replacements));
    }

    private void handleMessage(final String message) {

        /*
         * Furnish the message further by applying color codes,
         * components, prefix and player's set locale and send it to player.
         */
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                getLogger().prefix(message, SETTINGS.get(locale, "chat.prefix"))
        ));
    }

    // ================================ //
    // ==== Inventory State =========== //
    // ================================ //

    /**
     * @return  The current inventory state of the player
     *          in {@link CoreItemStackList} form.
     * @see     CoreItemStackList
     */
    public CoreItemStackList getItems() {
        if (!isOnline()) throw new PlayerNotConnectedException();
        return new CoreItemStackList(player.getInventory().getContents());
    }

    /**
     * @return  The current armor state of the player
     *          in {@link CoreItemStackList} form.
     * @see     CoreItemStackList
     */
    public CoreItemStackList getArmor() {
        if (!isOnline()) throw new PlayerNotConnectedException();
        return new CoreItemStackList(player.getInventory().getArmorContents());
    }

    /**
     * Gives the player standard default items registered
     * via {@link CoreSettings#getLoginItems()}
     */
    public void giveLoginItems() {
        if (!isOnline()) return;

        reset();
        clear();

        PlayerInventory inv = player.getInventory();
        SETTINGS.getLoginItems().entrySet().stream()
                .filter(entry -> entry.getValue().test(this))
                .forEach(entry -> {
                    IndexedItem item = entry.getKey();
                    inv.setItem(item.getSlot(), item.buildWithLocaleSupport(locale));
                });
    }

    /**
     * Clears the inventory and armor contents for the player.
     */
    public void clear() {
        if (!isOnline()) return;

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    /**
     * Resets the player's state entirely.
     *
     * <p>
     *  <ul>
     *      <li>Experience is set to {@code 0F}.</li>
     *      <li>Level is set to {@code 0}.</li>
     *      <li>Compass target is set to the player's last location.</li>
     *      <li>Food level is set to {@code 20}.</li>
     *      <li>Health is set to {@link Player#getMaxHealth()}.</li>
     *      <li>Saturation is set to {@code 100F}.</li>
     *      <li>Exhaustion is set to {@code 0F}.</li>
     *      <li>Fly speed is set to {@code 0.1F}.</li>
     *      <li>Walk speed is set to {@code .2F}.</li>
     *      <li>Fire ticks are set to {@code 0}.</li>
     *      <li>All potion effects are removed.</li>
     *      <li>Vanish is toggled off.</li>
     *      <li>Invisibility watch is toggled off.</li>
     *      <li>{@link GameMode} is set to {@link GameMode#SURVIVAL}.</li>
     *  </ul>
     * </p>
     *
     * <p>A delay of {@code 1L} is scheduled before removing the fire ticks.
     * This is to prevent CraftBukkit incosistencies when CraftPlayer isn't
     * fully spawned yet, in case this is being called after a player dies
     * or logs in.</p>
     */
    public void reset() {
        if (!isOnline()) return;

        player.setExp(0F);
        player.setLevel(0);

        player.setCompassTarget(player.getLocation());

        player.setFoodLevel(20);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

        player.setSaturation(100F);
        player.setExhaustion(0F);

        player.setFlySpeed(.1F);
        player.setWalkSpeed(.2F);

        /*
         * A delay is needed to remove fire ticks since this method
         * can be called before CraftPlayer instance is fully spawned.
         */
        Bukkit.getScheduler().scheduleSyncDelayedTask(CORE,
                () -> player.setFireTicks(0), 1L);

        for (PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }

        if (vanished) toggleVanish(true);

        // Inverse equals to check for player's gamemode being null (ie: when player logs in)
        if (!GameMode.SURVIVAL.equals(player.getGameMode())) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    // ================================ //
    // ==== Managers ================== //
    // ================================ //

    /**
     * @return  inventory manager
     * @see     InventoryManager
     */
    public InventoryManager getInvManager() {
        if (!isOnline()) throw new PlayerNotConnectedException();
        return invManager;
    }

    /**
     * @return  violation manager
     * @see     ViolationManager
     */
    public ViolationManager getViolations() {
        return violations;
    }

    // ================================ //
    // ==== Utilities ================= //
    // ================================ //

    /**
     * Gets the current {@link Location} of the player.
     *
     * @return  A new copy of {@code Location} containing the position of this entity.
     * @see     Player#getLocation() Player#getLocation();
     */
    public Location getLocation() {
        if (!isOnline()) throw new PlayerNotConnectedException();
        return player.getLocation();
    }

    /**
     * Spawns a random fireowork at the player's location.
     *
     * <p>Color, length and type are made completely random.</p>
     */
    public void spawnFirework() {
        if (isOnline()) new Firework(player.getLocation()).spawn();
    }

    /**
     * Enables and disables other players from seeing the player.
     *
     * @param silent Whether or not to output a message to the player.
     */
    public void toggleVanish(final boolean silent) {
        if (!isOnline()) return;

        if (vanished = !vanished) {
            for (CorePlayer player : PLAYER_MANAGER) {
                player.player.hidePlayer(this.player);
            }
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.INVISIBILITY,
                    Integer.MAX_VALUE, 1, false));
            if (!silent) sendMessage("vanish.enable");
        } else {
            for (CorePlayer player : PLAYER_MANAGER) {
                player.player.showPlayer(this.player);
            }
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            if (!silent) sendMessage("vanish.disable");
        }
    }

    /**
     * @return whether or not the player is vanished for other players.
     */
    public boolean isVanished() {
        if (!isOnline()) throw new PlayerNotConnectedException();
        return vanished;
    }

    /**
     * If BungeeCord is installed, attempts to send the player
     * to the server provided.
     *
     * @param server The server to send the player to.
     */
    public void connect(final String server) {
        if (!isOnline()) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(CORE, "BungeeCord", out.toByteArray());
    }

    public void sendPacket(final AbstractPacket packet) {
        packet.sendPacket(player);
    }

    // ================================ //
    // ==== Basic Core Limitations ==== //
    // ================================ //

    /**
     * Returns whether or not the player can currently use commands.
     *
     * <p>An example of when this limitation is ideal is when a
     * player joins an arena and has gets restricted to a certain
     * allowed commands so that he doesn't abuse them and, for example,
     * teleport out of the arena.</p>
     *
     * <p>Commands that bypass this limitation can get registered via
     * {@link CoreSettings#addAllowedCommand(String)} or added to the
     * {@code config.yml} file in the Core.</p>
     *
     * @return whether or not the player can currently use commands
     */
    public boolean isCommandsBlocked() {
        return commandsBlocked;
    }

    /**
     * Sets whether or not the player can currently use commands.
     *
     * <p>An example of when this limitation is ideal is when a
     * player joins an arena and has gets restricted to a certain
     * allowed commands so that he doesn't abuse them and, for example,
     * teleport out of the arena.</p>
     *
     * <p>Commands that bypass this limitation can get registered via
     * {@link CoreSettings#addAllowedCommand(String)} or added to the
     * {@code config.yml} file in the Core.</p>
     *
     * @param commandsBlocked whether or not the player can currently use commands
     */
    public void setCommandsBlocked(final boolean commandsBlocked) {
        if (!isOnline()) throw new PlayerNotConnectedException();
        this.commandsBlocked = commandsBlocked;
    }

    /**
     * @return whether or not the player can talk
     */
    public boolean isMuted() {
        return muted;
    }

    /**
     * Sets whether or not the player can talk
     *
     * @param muted whether or not the player can talk
     */
    public void setMuted(final boolean muted) {
        if (!isOnline()) throw new PlayerNotConnectedException();
        this.muted = muted;
    }

    /**
     * @return whether or not the player can get damaged by any means
     */
    public boolean isDamageable() {
        return damageable;
    }

    /**
     * Sets whether or not the player can get damaged by any means
     *
     * @param damageable whether or not the player can get damaged by any means
     */
    public void setDamageable(final boolean damageable) {
        if (!isOnline()) throw new PlayerNotConnectedException();
        this.damageable = damageable;
    }

    /**
     * @return whether or not the player can place or break blocks
     */
    public boolean isConstructable() {
        return constructable;
    }

    /**
     * Sets whether or not the player can place or break blocks
     *
     * @param constructable whether or not the player can place or break blocks
     */
    public void setConstructable(final boolean constructable) {
        if (!isOnline()) throw new PlayerNotConnectedException();
        this.constructable = constructable;
    }

    /**
     * @return whether or not the player can move
     */
    public boolean isMovable() {
        return movable;
    }

    /**
     * Sets whether or not the player can move
     *
     * @param movable whether or not the player can move
     */
    public void setMovable(final boolean movable) {
        if (!isOnline()) throw new PlayerNotConnectedException();
        this.movable = movable;
    }

    /**
     * @return whether or not the player's hunger can change
     */
    public boolean canGetHungry() {
        return hungry;
    }

    /**
     * Sets whether or not the player's hunger can change
     *
     * @param hungry whether or not the player's hunger can change
     */
    public void setCanGetHungry(final boolean hungry) {
        if (!isOnline()) throw new PlayerNotConnectedException();
        this.hungry = hungry;
    }

    // ================================ //
    // ==== Internal ================== //
    // ================================ //

    /**
     * Terminates the this player's accessibility with the server.
     */
    public void destroy() {
        if (!isOnline()) throw new PlayerNotConnectedException();

        // Send bulk update
        update(new ImmutableMap.Builder<DatabaseKey, Value>()
                .put(CorePlayerInfo.LAST_LOGOUT,    new Value<>(System.currentTimeMillis() / 1000L))
                .put(CorePlayerInfo.PLAYING,        new Value<>(false))
                .build());

        // Destroy violation managers to prevent memory leaks
        violations.destroy();

        PLAYER_MANAGER.remove(this);

        // Add offline player to cache
        CACHED_PLAYERS.add(this);
    }

    @Override
    public boolean isOnline() {
        return player != null && player.isOnline();
    }

    @Override
    public CorePlayer toCorePlayer() {
        return this;
    }

    @Override
    public MongoCollection<Document> getCollection() {
        return COLLECTION;
    }

    /**
     * Creates a new instance of this player if he doesn't exist,
     * or returns an existing instance.
     *
     * @param player The delegate to use for this instance.
     * @return The single instance of the online player.
     * @see com.maulss.core.bukkit.player.manager.CorePlayerManager#add(Player, Optional)
     */
    public static CorePlayer createIfAbsent(final Player player) {
        return PLAYER_MANAGER.add(player);
    }

    @Deprecated
    public static CorePlayer _init(final Player player,
                                   final Optional<String> hostName) {
        return new CorePlayer(player, hostName.orElseGet(() -> player.getAddress().getHostName()));
    }

    // ================================ //
    // ==== Offline Profiles ========== //
    // ================================ //

    public static void get(final String name,
                           final Consumer<CorePlayer> callback) {
        CorePlayer player = PLAYER_MANAGER.get(name);
        if (player != null) {
            callback.accept(player);
        } else {
            // player is offline - find him in cache
            for (CorePlayer cachePlayer : CACHED_PLAYERS) {
                if (cachePlayer.getName().equalsIgnoreCase(name)) {
                    // we found the player
                    callback.accept(cachePlayer);
                }
            }

            // not connected and not in cache - request offline player
            new CorePlayer(name, callback);
        }
    }

    public static void get(final UUID uuid,
                           final Consumer<CorePlayer> callback) {
        CorePlayer player = PLAYER_MANAGER.get(uuid);
        if (player != null) {
            callback.accept(player);
        } else {
            // player is offline - find him in cache
            for (CorePlayer cachePlayer : CACHED_PLAYERS) {
                if (cachePlayer.getUuid().equals(uuid)) {
                    // we found the player
                    callback.accept(cachePlayer);
                }
            }

            // not connected and not in cache - request offline player
            new CorePlayer(uuid, callback);
        }
    }

    public static Optional<CorePlayer> getOnline(final String name) {
        return Optional.ofNullable(PLAYER_MANAGER.get(name));
    }

    public static Optional<CorePlayer> getOnline(final UUID uuid) {
        return Optional.ofNullable(PLAYER_MANAGER.get(uuid));
    }
}