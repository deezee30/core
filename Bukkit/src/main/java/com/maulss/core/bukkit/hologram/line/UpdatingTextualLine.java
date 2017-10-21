/*
 * Part of core.
 */

package com.maulss.core.bukkit.hologram.line;

import com.maulss.core.bukkit.Core;
import org.bukkit.Bukkit;

public abstract class UpdatingTextualLine extends TextualLine {

    public UpdatingTextualLine(final String text,
                               final int updateSpeedTicks) {
        super(text);

        /*
         * If updateSpeedTicks is not provided; ie, it's less than 1,
         * then update(String) will have to be called manually each
         * time the line needs to update. The methods getText(),
         * setText(String) and onUpdate() will in that case be
         * redundant unless the developer handles them manually
         * as well.
         */
        if (updateSpeedTicks > 0) {
            Bukkit.getScheduler().runTaskTimer(
                    Core.get(),
                    () -> {
                        String newText = onUpdate();
                        setText(newText);
                        update(newText);
                    },
                    updateSpeedTicks,
                    updateSpeedTicks
            );
        }
    }

    public abstract String onUpdate();
}