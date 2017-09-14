/*
 * Part of core.
 * 
 * Created on 13 July 2017 at 3:05 PM.
 */

package com.maulss.core.net.communication;

import com.maulss.core.Logger;

public interface SocketApp {

    String getName();

    Logger getLogger();

    default void log(final String log,
                     final Object... replacements) {
        getLogger().log(log, replacements);
    }
}