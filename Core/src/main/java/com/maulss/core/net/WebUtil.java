package com.maulss.core.net;

import com.maulss.core.CoreException;
import com.maulss.core.net.http.Form;
import com.maulss.core.service.ServiceExecutor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.lang3.Validate.notNull;

public final class WebUtil {

    /* Disable initialization */
    private WebUtil() {}


    public static Form mozillaForm() {
        return Form.create().add("User-Agent", "Mozilla/5.0");
    }

    public static void ping(final String url) {
        notNull(url, "url");
        try {
            ping(new URL(url));
        } catch (MalformedURLException e) {
            try {
                throw new CoreException(e);
            } catch (CoreException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void ping(final  URL url) {
        notNull(url, "url");
        ServiceExecutor.getCachedExecutor().execute(() -> {
            try {
                url.openStream().close();
            } catch (IOException e) {
                try {
                    throw new CoreException(e);
                } catch (CoreException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}