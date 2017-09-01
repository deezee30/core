package com.maulss.core.jnbt.type;

import com.maulss.core.jnbt.Tag;

/**
 * The {@code TAG_End} tag.
 */
public final class EndTag extends Tag {

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String toString() {
        return "TAG_End";
    }
}