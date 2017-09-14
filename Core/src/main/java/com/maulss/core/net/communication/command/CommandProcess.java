/*
 * Part of core.
 * 
 * Created on 15 July 2017 at 2:52 PM.
 */

package com.maulss.core.net.communication.command;

import java.util.Optional;

@FunctionalInterface
public interface CommandProcess {

    Optional<ReturnCommand> process();
}