/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.service.timer;

import java.util.concurrent.Callable;

public abstract class TimedCallableTask<T> extends TimedTask<T> implements Callable<T> {

    @Override
    public T call() throws Exception {
        return process();
    }
}