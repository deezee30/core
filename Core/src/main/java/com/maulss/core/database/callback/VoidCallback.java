/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.callback;

import javax.annotation.Nullable;

@FunctionalInterface
public interface VoidCallback extends DatabaseCallback<Void> {

    @Override
    default void onResult(@Nullable final Void result,
                          @Nullable final Throwable t) {
        // No result callbacks don't need a result
        onResult(t);
    }

    default void onResult() {
        // Callback with no result and no errors
        onResult((Throwable) null);
    }

    void onResult(@Nullable final Throwable throwable);
}