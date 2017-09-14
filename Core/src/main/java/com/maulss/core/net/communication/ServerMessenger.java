/*
 * Part of core.
 * 
 * Created on 10 July 2017 at 9:39 PM.
 */

package com.maulss.core.net.communication;

import com.maulss.core.Logger;

public interface ServerMessenger {

    Logger getLogger();

    String receive(final CoreServer from,
                   final String command);

    void send(final CoreServer to,
              final String command);

    CoreServerRegistry getServerRegistry();
}