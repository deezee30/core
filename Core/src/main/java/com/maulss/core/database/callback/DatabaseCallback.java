/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.callback;

import com.mongodb.async.SingleResultCallback;

@FunctionalInterface
public interface DatabaseCallback<T> extends SingleResultCallback<T> {

    default void onResult(T result) {
        // No errors
        onResult(result, null);
    }
}