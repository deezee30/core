package com.maulss.core.jnbt.type;

import com.maulss.core.jnbt.Tag;

/**
 * The {@code TAG_Short} tag.
 */
public final class ShortTag extends Tag {

    private final short value;

    /**
     * Creates the tag with an empty name.
     *
     * @param value the value of the tag
     */
    public ShortTag(short value) {
        super();
        this.value = value;
    }

    @Override
    public Short getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TAG_Short(" + value + ")";
    }
}