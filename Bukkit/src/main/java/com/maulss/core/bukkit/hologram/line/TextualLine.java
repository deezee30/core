/*
 * Part of core.
 * 
 * Created on 23 July 2017 at 3:01 PM.
 */

package com.maulss.core.bukkit.hologram.line;

import org.apache.commons.lang3.Validate;

public class TextualLine extends HologramLine {

    private String text;

    public TextualLine(final String text) {
        setText(text);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(final String text) {
        this.text = Validate.notNull(text, "text");
    }
}