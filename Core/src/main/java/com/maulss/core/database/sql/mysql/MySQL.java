/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.sql.mysql;

import com.maulss.core.database.Credentials;
import com.maulss.core.database.DatabaseException;
import com.maulss.core.database.sql.SQLDatabase;
import com.maulss.core.service.ServiceExecutor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MySQL extends SQLDatabase {

    // String constants
    private static final String
            // MySQL connector-J driver class path
            DRIVER              = "com.mysql.jdbc.Driver",

            // Error messages
            DRIVER_NOT_FOUND    = "MySQL driver is not installed on the system",
            DEFAULT_CREDENTIALS = "Credentials have been provided but haven't been changed from default";

    // Async executor
    private static final ExecutorService
            MYSQL_EXECUTOR      = ServiceExecutor.newAsyncExecutor("MySQL");

    // Internal checking to make sure credentials aren't default
    private final boolean
            creds;

    // MySQL connection configuration
    private final Properties
            properties;
    private final String
            url,
            database;

    public MySQL(final Credentials credentials) {
        super(MYSQL_EXECUTOR);
        checkNotNull(credentials, "credentials");
        creds = credentials.isSet();
        properties = new Properties();
        properties.setProperty("user", credentials.getUser());
        properties.setProperty("password", credentials.getPass());
        this.url = credentials.getAddress();
        this.database = credentials.getDatabase();
    }

    public MySQL(final String url,
                 final String database,
                 final Properties properties) {
        super(MYSQL_EXECUTOR);
        creds = true;
        this.properties = checkNotNull(properties, "properties");
        this.url = checkNotNull(url, "url");
        this.database = checkNotNull(database, "database");
    }

    @Override
    protected Connection open() throws DatabaseException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ignored) {
            throw new DatabaseException(DRIVER_NOT_FOUND);
        }

        if (!creds) {
            throw new DatabaseException(DEFAULT_CREDENTIALS);
        }

        try {
            return DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public String getName() {
        return database;
    }
}