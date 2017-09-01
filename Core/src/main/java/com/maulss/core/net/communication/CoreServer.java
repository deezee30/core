/*
 * rv_core
 * 
 * Created on 10 July 2017 at 8:29 PM.
 */

package com.maulss.core.net.communication;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;

public class CoreServer implements Serializable {

    private static final long serialVersionUID = -2400216533348037042L;

    // TODO
    public static CoreServer THIS;

    private final String internalName;
    private final String address;
    private final int port;

    public CoreServer(final String internalName,
                      final String address) {
        this(internalName, address, 25565);
    }

    public CoreServer(final String internalName,
                      final String address,
                      final int port) {
        this.internalName = Validate.notNull(internalName, "internalName");
        this.address = Validate.notNull(address, "address");
        this.port = port;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getFriendlyName() {
        return WordUtils.capitalize(internalName.replace("-", " ").replace("_", " "));
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CoreServer that = (CoreServer) o;

        return new EqualsBuilder()
                .append(port, that.port)
                .append(internalName, that.internalName)
                .append(address, that.address)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(internalName)
                .append(address)
                .append(port)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("internalName", internalName)
                .append("address", address)
                .append("port", port)
                .toString();
    }
}