/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.service.timer;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public final class Timer implements Runnable, Serializable {

    private static final long serialVersionUID = -2561071298328943487L;

    private boolean running = false;
    private long startNanos;
    private long finishNanos;
    private Runnable onFinish;

    @Override
    public void run() {
        running = true;
        startNanos = System.nanoTime();
    }

    public Timer start() {
        run();
        return this;
    }

    public Timer forceStop() {
        if (running) {
            finish();
        }

        return this;
    }

    public long getTime(TimeUnit time) {
        return time.convert(finishNanos - startNanos, TimeUnit.NANOSECONDS);
    }

    public Timer onFinishExecute(Runnable runnable) {
        onFinish = runnable;
        return this;
    }

    private void finish() {
        running = false;
        finishNanos = System.nanoTime();

        if (onFinish != null) onFinish.run();
    }

    public boolean isRunning() {
        return running;
    }
}