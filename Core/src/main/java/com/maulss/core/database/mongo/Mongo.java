/*
 * Part of core.
 * Made on 30/07/2017
 */

package com.maulss.core.database.mongo;

import com.google.common.collect.Lists;
import com.maulss.core.Logger;
import com.maulss.core.database.Credentials;
import com.maulss.core.database.Database;
import com.maulss.core.database.DatabaseException;
import com.maulss.core.database.callback.VoidCallback;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ServerSettings;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a wrapper for the client for the Mongo database that provides
 * easy connection utilities with callbacks containing results and any errors if
 * they occur.
 *
 * @see Database
 */
public final class Mongo implements Database {

    // String constants
    private static final String
            // Connection start output
            THREADS_START                   = "%s Mongo access threads started",
            CONNECTION_START                = "Connecting to Mongo at '%s:%s'",

            // Connection errors
            CONNECTION_ESTABLISHED_ERROR    = "Mongo connection already established",
            CONNECTION_ABSENT_ERROR         = "Mongo connection doesn't exist";

    // Singleton instance (for now)
    private static Mongo
            instance;

    // Logger used for outputs and debugging
    private final Logger
            logger                          = new Logger();

    // Credentials information used for Mongo authentication
    private final Credentials
            credentials;

    // Default connection listener for callbacks and timings
    private final MongoConnectionListener
            listener                        = new MongoConnectionListener(this, logger);

    // Custom codec registry used in combination with the default Mongo codec registry
    private CodecRegistry
            codecRegistry                   = MongoClients.getDefaultCodecRegistry();

    // Connection server cluster description
    private String
            description                     = "Core Mongo connection";

    // MongoClient reference for management and handling connections
    private MongoClient
            client;

    // Internal boolean used to check if database is connected
    private boolean
            init = false;

    private Mongo(final Credentials credentials) {
        this.credentials = checkNotNull(credentials, "credentials");
        logger.setPrefix("Mongo DB: " + credentials.getDatabase() + "> ");
    }

    /**
     * Registers custom Bson codecs to be used within the Mongo client.
     *
     * Default Mongo codec registry is registered on top of this.
     *
     * @param codecs
     *         codecs to register
     * @return this instance
     */
    public Mongo setCodecRegistry(final Codec<?>... codecs) {
        return codecs != null && codecs.length > 0
                ? setCodecRegistry(CodecRegistries.fromCodecs(codecs))
                : this;
    }

    /**
     * Registers custom Bson codec registry to be used within the Mongo client.
     *
     * Default Mongo codec registry is registered on top of this.
     *
     * @param registry
     *         registry to register
     * @return this instance
     */
    public Mongo setCodecRegistry(final CodecRegistry registry) {
        if (registry != null)
            this.codecRegistry = CodecRegistries.fromRegistries(this.codecRegistry, registry);
        return this;
    }

    /**
     * Sets the description for the connected server cluster for Mongo.
     *
     * {@code null} can be provided to disable description.
     *
     * @param description
     *         the description for the connection
     * @return this instance
     */
    public Mongo setDescription(@Nullable final String description) {
        this.description = description;
        return this;
    }

    /**
     * Finds the amount of maximum amount of available (physical and virtual)
     * processors and sets up n instances of concurrent simultaneous {@link
     * MongoAccessThread Mongo Access Threads}.
     *
     * These {@link Thread threads} specialise in queueing {@link
     * com.maulss.core.database.mongo.query.MongoQuery queries} and executing
     * them in their respective orders.
     *
     * The set up and queueing is set up synchronously while the execution is
     * performed concurrently.
     *
     * @return this instance
     * @see MongoAccessThread
     * @see com.maulss.core.database.mongo.query.MongoQuery
     */
    public Mongo setupThreads() {
        // create async Mongo access threads
        List<MongoAccessThread> accessThreads = Lists.newArrayList();

        int count = Runtime.getRuntime().availableProcessors();

        // open threads and keep them around
        IntStream.range(0, count - 1).forEach(c -> accessThreads.add(new MongoAccessThread()));
        accessThreads.forEach(Thread::start);

        log(THREADS_START, count);
        return this;
    }

    /**
     * @return the {@code Client} that handles the Mongo connection
     */
    @Nullable
    public MongoClient getClient() {
        return client;
    }

    /**
     * @return whether or not the Mongo database is connected
     */
    @Override
    public boolean isConnected() {
        return init;
    }

    /**
     * Internally sets if the database is connected.
     *
     * This is used by the {@link MongoConnectionListener Mongo listener} to
     * update as soon as a callback is returned.
     *
     * @param connected
     *         whether or not the Mongo database is connected
     */
    void setConnected(final boolean connected) {
        init = connected;
    }

    /**
     * Connects to the Mongo database based on the settings already provided in
     * this Mongo instance.
     *
     * By default, a single cluster connection is established with a timeout of
     * {@code 3} seconds.
     *
     * As soon as the connection is opened on another thread, the callback is
     * called with a {@code Throwable} if any errors occur. If no errors occur,
     * then {@code null} is passed through the callback instead.
     *
     * @param callback
     *         Void callback that's executed on connect
     * @return this instance
     */
    @Override
    public Mongo connect(final VoidCallback callback) {
        checkNotNull(callback);

        log(CONNECTION_START, credentials.getAddress(), credentials.getPort());

        if (init) {
            callback.onResult(new DatabaseException(CONNECTION_ESTABLISHED_ERROR));
            return this;
        }

        listener.getTimer().start();
        listener.setOpenCallback(callback);

        client = MongoClients.create(MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .clusterSettings(ClusterSettings.builder()
                        .hosts(Collections.singletonList(new ServerAddress(
                                credentials.getAddress(),
                                credentials.getPort()
                        ))).mode(ClusterConnectionMode.SINGLE)
                        .serverSelectionTimeout(3, TimeUnit.SECONDS)
                        .description(description)
                        .build()
                ).serverSettings(ServerSettings.builder()
                        .addServerListener(listener)
                        .addServerMonitorListener(listener)
                        .build()
                ).credentialList(Collections.singletonList(MongoCredential.createCredential(
                        credentials.getUser(),
                        credentials.getDatabase(),
                        credentials.getPass().toCharArray()
                ))).build()
        );

        return this;
    }

    /**
     * Attempts to disconnect from the Mongo database.
     *
     * As soon as the connection is closed on another thread, the callback is
     * called with a {@code Throwable} if any errors occur. If no errors occur,
     * then {@code null} is passed through the callback instead.
     *
     * @param callback
     *         void callback that's executed on disconnect
     * @return this instance
     */
    @Override
    public Mongo disconnect(final VoidCallback callback) {
        checkNotNull(callback);

        if (!init) {
            callback.onResult(new DatabaseException(CONNECTION_ABSENT_ERROR));
            return this;
        }

        listener.getTimer().start();
        listener.setCloseCallback(callback);

        client.close();
        client = null;

        init = false;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("credentials", credentials)
                .append("codecRegistry", codecRegistry)
                .append("description", description)
                .append("init", init)
                .toString();
    }

    /**
     * Sets up a new Mongo instance with the provided {@link Credentials}.
     *
     * If an instance has already been set up, a {@link IllegalStateException}
     * is thrown.
     *
     * @param credentials
     *         The credentials to pass to the Mongo database
     * @return new {@code Mongo} instance
     * @throws IllegalStateException
     *         If setup has already been called
     */
    public static Mongo setup(final Credentials credentials) {
        if (instance == null) return instance = new Mongo(credentials);
        throw new IllegalStateException(CONNECTION_ESTABLISHED_ERROR);
    }

    /**
     * Returns the current instance of Mongo database.
     *
     * If {@link #setup(Credentials)} hasn't been called yet, then an {@link
     * IllegalStateException} will be thrown.
     *
     * @return current {@code Mongo} instance
     * @throws IllegalStateException
     *         If {@link #setup(Credentials)} hasn't been called yet.
     */
    public static Mongo get() {
        if (instance == null)
            throw new IllegalStateException(CONNECTION_ABSENT_ERROR);
        return instance;
    }
}