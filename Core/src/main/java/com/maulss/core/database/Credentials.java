/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

public final class Credentials {

    // Used to detect defaults
    private static final String CHANGE_ME = "CHANGE_ME";

    private final String address;
    private final int port;
    private final String database;
    private final String user;
    private final String pass;
    private boolean set = false;

    public Credentials(final String address,
                       final int port,
                       final String database,
                       final String user,
                       final String pass) {
        this.address  = Validate.notNull(address);
        this.port = port;
        this.database = Validate.notNull(database);
        this.user = Validate.notNull(user);
        this.pass = Validate.notNull(pass);

        set = !address.equals(CHANGE_ME)
                && !database.equals(CHANGE_ME)
                && !user.equals(CHANGE_ME)
                && !pass.equals(CHANGE_ME);
    }

    public Credentials(final Map<String, Object> data) {
        this(
                (String) data.get("address"),
                (int)    data.get("port"),
                (String) data.get("database"),
                (String) data.get("username"),
                (String) data.get("password")
        );
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public boolean isSet() {
        return set;
    }

    public Map<String, Object> serialize() {
        return new ImmutableMap.Builder<String, Object>()
                .put("address", address)
                .put("port", port)
                .put("database", database)
                .put("username", user)
                .put("password", pass)
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("address", address)
                .append("port", port)
                .append("database", database)
                .append("user", user)
                .append("pass", pass)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Credentials that = (Credentials) o;

        return new EqualsBuilder()
                .append(address, that.address)
                .append(port, that.port)
                .append(database, that.database)
                .append(user, that.user)
                .append(pass, that.pass)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(address)
                .append(port)
                .append(database)
                .append(user)
                .append(pass)
                .toHashCode();
    }
}