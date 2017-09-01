/*
 * rv_core
 * 
 * Created on 15 July 2017 at 2:18 PM.
 */

package com.maulss.core.net.communication;

import com.maulss.core.collect.EnhancedList;

import java.util.Collection;

public final class CoreServerRegistry {

    private final EnhancedList<CoreServer> servers = new EnhancedList<>();

    public void add(CoreServer server) {
        servers.add(server);
    }

    public static CoreServerRegistry newRegistry(CoreServer... servers) {
        CoreServerRegistry registry = new CoreServerRegistry();
        for (CoreServer server : servers) {
            registry.add(server);
        }
        return registry;
    }

    public static CoreServerRegistry newRegistry(Collection<CoreServer> servers) {
        CoreServerRegistry registry = new CoreServerRegistry();
        registry.servers.addAll(servers);
        return registry;
    }

    public static CoreServer getCurrentServer() {
        return CoreServer.THIS;
    }
}