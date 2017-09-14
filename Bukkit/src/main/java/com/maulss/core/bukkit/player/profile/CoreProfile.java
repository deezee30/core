/*
 * Part of core.
 * Made on 02/09/2017
 */

package com.maulss.core.bukkit.player.profile;

import com.google.common.collect.ImmutableList;
import com.maulss.core.CoreException;
import com.maulss.core.Logger;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.player.statistic.StatisticHolder;
import com.maulss.core.database.DatabaseKey;
import com.maulss.core.database.callback.DatabaseCallback;
import com.maulss.core.database.callback.VoidUpdateResult;
import com.maulss.core.database.mongo.MongoIdentity;
import com.maulss.core.database.mongo.data.MongoDataOperator;
import com.maulss.core.service.timer.Timer;
import com.maulss.core.util.UUIDUtil;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * An abstract version of expanded {@link Profile}.
 *
 * <p>This class should be used for any scenario where a virtual server user is
 * involved who is expected to be interacted with a Mongo database, regardless
 * whether he could be online or offline.  This profile is designed to store the
 * fundamental data for online and offline players, who are eligible for
 * interaction with the database and the in-game server world.</p>
 *
 * <p>Provided that the sub class holds this data for an online player, this
 * class is expected to be instantiated only once, per online player, per
 * server.  Otherwise, there is no limit on how many profiles can be made.</p>
 *
 * <p>Upon instantiation, the essential data is requested from the database.  If
 * the user was not found in the database (or the database connection is
 * closed), either the {@link UUID} or name of the user can end up being {@code
 * null} (but not both).  This still allows offline profiles to store it in some
 * sort of cache.  An example of profile caching system can be found in the sub
 * class {@link CoreProfile}.
 *
 * <p>To check if the {@code UUID} and name of the player are both real and thus
 * making the user a valid user, simply check with {@link #hasPlayed()}.</p>
 *
 * @see Profile
 * @see CoreProfile
 * @see CoreProfile
 */
public abstract class CoreProfile
        implements Profile, StatisticHolder, MongoIdentity {

    /*
     * In case the player is offline and the UUID or name has
     * not been found in the general database, either the UUID
     * or name can result to being null, as it can be used to
     * store for cache purposes.
     *
     * If both values are null, profile will not be generated.
     */
    private volatile UUID   uuid;
    private volatile String name;

    /**
     * Used for timing this class.  Can be accessed and interacted with via sub
     * classes.  It is recommented to stop the timer after all loading is done
     * via {@link Timer#forceStop()}.
     */
    protected volatile transient Timer
            timer           = new Timer();

    protected transient boolean
            loaded          = false,
            played          = false,
            everPlayed      = false;
    protected transient StatisticHolder
            statisticHolder = this;
    protected transient Logger
            logger          = Core.get().logger();

    // ================================ //
    // ==== Construction ============== //
    // ================================ //

    /**
     * @see #CoreProfile(Optional, Optional)
     */
    protected CoreProfile(final UUID uuid) {
        this(Optional.of(uuid), Optional.empty());
    }

    /**
     * @see #CoreProfile(Optional, Optional)
     */
    protected CoreProfile(final String name) {
        this(Optional.empty(), Optional.of(name));
    }

    /**
     * @see #CoreProfile(Optional, Optional)
     */
    protected CoreProfile(final UUID uuid,
                          final String name) {
        this(Optional.of(uuid), Optional.of(name));
    }

    /**
     * Instantiates a user instance used to store essential data.
     *
     * <p>Either the {@param uuid} or {@param name} provided can be {@link
     * Optional} but not both.  This class attempts to find the user in the
     * pre-defined database collection. If the database connection is not set up
     * or the user has not been found, the profile data will not be loaded.</p>
     *
     * <p>Class instantiation should be done so in {@link #onLoad(Optional)}, as
     * described in {@link #onLoad(Optional)} JavaDocs.</p>
     *
     * <p>If both, {@code UUID} and username are provided and not null, then it
     * is assumed that they are correct and match each other. In this case, a
     * search in the {@link Core#getPlayerCollection() main player collection}
     * will <b>NOT</b> be made.</p>
     *
     * @param uuid
     *         A {@code UUID} that the user may have
     * @param name
     *         A name that the user may have
     * @see #onLoad(Optional)
     */
    protected CoreProfile(final Optional<UUID> uuid,
                          final Optional<String> name) {
        this.uuid = uuid.orElse(null);
        this.name = name.orElse(null);

        if (!uuid.isPresent() && !name.isPresent()) {
            logger.debug("Created a fake player but both ID and name are null");
            return;
        }

        // Record how long it takes to load the profile
        timer.start().onFinishExecute(() -> logger.debug(
                "Generated %s profile '%s' with ID '%s' in %sms",
                CoreProfile.this.getClass().getSimpleName(),
                this.name,
                this.uuid,
                timer.getTime(TimeUnit.MILLISECONDS)
        ));

        Bukkit.getScheduler().runTaskAsynchronously(Core.get(), () -> {
            boolean internalFirst = !uuid.isPresent() || !name.isPresent();

            if (internalFirst) {
                DatabaseKey data = PlayerInfo.UUID;
                Object value = this.uuid;

                // if uuid is null, use name instead
                if (!uuid.isPresent()) {
                    data = PlayerInfo.NAME;
                    value = this.name;
                }

                Core.getPlayerCollection()
                        .find(Filters.eq(data.getKey(), value))
                        .first((document, throwable) -> {

                            logger.logIf(
                                    throwable != null,
                                    "Error loading '%s' ('%s'): %s",
                                    this.name,
                                    this.uuid,
                                    throwable
                            );

                            Optional<Document> downloadedDoc = Optional.ofNullable(document);
                            if (downloadedDoc.isPresent()) {
                                this.name = document.getString(PlayerInfo.NAME.getKey());
                                this.uuid = UUIDUtil.fromString(
                                        document.getString(PlayerInfo.UUID.getKey()));

                                // now retrieve document from relative player collection
                                refreshStats();
                            } else {
                                // player never played before
                                finishLoading(Optional.empty());
                            }
                        });
            } else {
                // name and UUID are defined - load relative player collection instead
                refreshStats();
            }
        });
    }

    private void finishLoading(Optional<Document> document) {
        // finish loading on the main thread
        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.get(), () -> {
            loaded = true;
            if (isOnline()) played = true;
            onLoad(document);
            timer.forceStop();
        });
    }

    /**
     * Sets up and loads the player after the statistics have been downloaded.
     *
     * <p>Because the Core relies on processing database tasks asynchronously,
     * this method is essential for such calls and will get called as soon as
     * the process is finished and returned.</p>
     *
     * <p>Whenever a player instance is being set up and needs to access certain
     * statistics from the player's {@link #getCollection() collection}, the
     * initialization of instance should be made in this method.</p>
     *
     * <p>If the collection isn't provided by the sub class, then the default
     * collection statistics will be passed in the arguments, in case the
     * profile needs them to finish loading, eg: {@link CoreProfile}.</p>
     *
     * <p>If the player is not found in the database, ie: the player hasn't
     * played the server before - then {@link Optional#empty()} is passed.</p>
     *
     * @param document
     *         the optional document that holds the statistics
     */
    protected abstract void onLoad(Optional<Document> document);

    /**
     * Redownload the {@link #getCollection() collection document} again
     * asynchronously and call {@link #onLoad(Optional)} with the newly
     * downloaded stats.
     *
     * <p>Typically used for updating cached offline players</p>
     */
    protected final void refreshStats() {
        // Async download custom stats from database
        retrieve((result, t) -> {
            logger.logIf(t != null, "Error loading '%s' ('%s'): %s", name, uuid, t);
            everPlayed = true;
            finishLoading(Optional.ofNullable(result));
        });
    }

    // ================================ //
    // ==== Profile Data ============== //
    // ================================ //

    @Override
    public final UUID getUuid() {
        return uuid;
    }

    @Override
    public final String getName() {
        return name;
    }

    /**
     * @return Friendly display name of the player
     */
    public abstract String getDisplayName();

    @Override
    public boolean hasPlayed() {
        return played;
    }

    @Override
    public boolean hasEverPlayed() {
        return everPlayed;
    }

    /**
     * @return whether or not the instance has finished database lookup and is
     * loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    // ================================ //
    // ==== Utilities ================= //
    // ================================ //

    public final void updateNameSilently(final String name) {
        updateName(name, null);
    }

    public final void updateName(final String name) {
        updateName(name, (result, t) -> t.printStackTrace());
    }

    /**
     * Updates the player's name in memory and in the main player database.
     *
     * The player must already exist in the {@link Core#getPlayerCollection()
     * main player collection}.
     *
     * @param name
     *         The new name to update to
     * @param callback
     *         Optional (nullable) callback that gets called whenever the
     *         operation completes and updates memory
     * @throws NullPointerException
     *         If name provided is null
     */
    public final void updateName(final String name,
                                 @Nullable final DatabaseCallback<UpdateResult> callback) {
        try {
            if (name == null)
                throw new NullPointerException("Provided name is null");
            if (!everPlayed)
                throw new CoreException("Attempted updating an unknown player's name");
        } catch (final Exception e) {
            e.printStackTrace();
        }

        if (this.name.equals(name)) {

            // do not update name and simply return callback if provided
            if (callback != null)
                callback.onResult(new VoidUpdateResult());

            return;
        }

        Core.getPlayerCollection().updateOne(getSearchQuery(),
                new Document(MongoDataOperator.$SET.getOperator(),
                        new Document(PlayerInfo.NAME.getKey(), name)), (result, t) -> {
                    if (t != null) {
                        CoreProfile.this.name = name;
                    }

                    if (callback != null)
                        callback.onResult(result, t);
                });
    }

    /**
     * Sets the provided statistic holder as a source of statistics for this
     * profile instance.
     *
     * By default, simple and basic statistics are returned via {@link
     * #getStatisticValues()}. This method allows for custom implementations.
     *
     * @param statisticHolder
     *         The new statistic holder
     * @see StatisticHolder
     */
    public void setStatisticHolder(@Nullable final StatisticHolder statisticHolder) {
        this.statisticHolder = statisticHolder;
    }

    @Override
    public ImmutableList<String> getStatisticValues() {

        /*
         * If statHolder equals to the current instance (this
         * instance of CoreProfile), then return custom lines
         * for statistics.  Otherwise let statHolder decide
         * which statistics to return.
         */
        if (!equals(statisticHolder)) {
            return statisticHolder.getStatisticValues();
        }

        if (!played) {
            try {
                throw new CoreException("Player " + name + " never played before!");
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }

        return new ImmutableList.Builder<String>()
                .add("~")
                .add("~&e======= " + getDisplayName() + " &e=======")
                .add("~&eID: &3" + uuid)
                .add("~&eCurrently " + (isOnline() ? "&2Online" : "&4Offline"))
                .add("~")
                .build();
    }

    // ================================ //
    // ==== Internal ================== //
    // ================================ //

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public final String toString() {
        return name;
    }
}