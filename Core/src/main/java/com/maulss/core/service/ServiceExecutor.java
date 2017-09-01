/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.service;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class ServiceExecutor {

    // Used by pasters and url shorteners
    // Will never be shut down as it will be reused
    private static final ListeningExecutorService executor = newAsyncExecutor();

    private ServiceExecutor() {}

    public static ListeningExecutorService getCachedExecutor() {
        return executor;
    }

    public static ListeningExecutorService newAsyncExecutor() {
        return MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
    }

    public static ListeningExecutorService newAsyncExecutor(final ThreadFactory tf) {
        return MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1, tf));
    }

    public static ListeningExecutorService newAsyncExecutor(final String name) {
        return newAsyncExecutor(new ThreadFactoryBuilder().setNameFormat(name + "-%d").build());
    }
}