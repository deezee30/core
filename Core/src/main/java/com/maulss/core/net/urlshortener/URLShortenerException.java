/*
 * rv_core
 * 
 * Created on 28 June 2017 at 7:12 PM.
 */

package com.maulss.core.net.urlshortener;

import com.maulss.core.CoreException;

public class URLShortenerException extends CoreException {

    public URLShortenerException(String message, Object... components) {
        super(message, components);
    }

    public URLShortenerException(String message, Throwable cause) {
        super(message, cause);
    }

    public URLShortenerException(Throwable cause) {
        super(cause);
    }

    public URLShortenerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}