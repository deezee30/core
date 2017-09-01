package com.maulss.core.net.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Used with {@link HttpRequest#bodyForm(Form)}.
 */
public final class Form {

    private final List<String> elements = new ArrayList<>();

    private Form() {}

    /**
     * Add a key/value to the form.
     *
     * @param key   the key
     * @param value the value
     *
     * @return this object
     */
    public Form add(final String key,
                    final String value) {
        notNull(key, "key");
        notNull(value, "value");
        try {
            elements.add(URLEncoder.encode(key, "UTF-8") +
                    "=" + URLEncoder.encode(value, "UTF-8"));
            return this;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String element : elements) {
            if (first) {
                first = false;
            } else {
                builder.append("&");
            }
            builder.append(element);
        }
        return builder.toString();
    }

    /**
     * Create a new form.
     *
     * @return a new form
     */
    public static Form create() {
        return new Form();
    }
}