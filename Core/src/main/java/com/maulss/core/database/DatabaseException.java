/*
 * Part of core.
 * Made on 30/07/2017
 */

package com.maulss.core.database;

import com.maulss.core.CoreException;

public class DatabaseException extends CoreException {

    public DatabaseException() {}

    public DatabaseException(String message, Object... components) {
        super(message, components);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }

    public DatabaseException(String message, Throwable cause,
                             boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}