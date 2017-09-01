/*
 * Part of core.
 * Made on 05/08/2017
 */

package com.maulss.core.database.callback;

import com.maulss.core.Logger;
import org.apache.commons.lang3.Validate;

public class UnhandledCallback<T> implements DatabaseCallback<T> {

    private final Logger logger;

    public UnhandledCallback(final Logger logger) {
        this.logger = Validate.notNull(logger, "logger");
    }

    @Override
    public void onResult(final T result,
                         final Throwable error) {
        if (error != null) {
            logger.log("An unhandled database error occured with no callbacks to resort to!");
            try {
                throw error;
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}