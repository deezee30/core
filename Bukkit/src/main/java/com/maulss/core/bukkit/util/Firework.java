/*
 * Part of core.
 */

package com.maulss.core.bukkit.util;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

public final class Firework {

    private static final FireworkEffect.Type[]  TYPES   = FireworkEffect.Type.values();
    private static final Color[]                COLORS  = new Color[] {
            Color.AQUA, Color.BLACK,  Color.BLUE,   Color.FUCHSIA,
            Color.GRAY, Color.GREEN,  Color.LIME,   Color.MAROON,
            Color.NAVY, Color.OLIVE,  Color.ORANGE, Color.PURPLE,
            Color.RED,  Color.SILVER, Color.TEAL,   Color.YELLOW,
            Color.WHITE
    };

    private final Random r = new Random();
    private final Location location;

    public Firework(final Location location) {
        this.location = location;
    }

    public org.bukkit.entity.Firework spawn() {
        org.bukkit.entity.Firework fw
                = location.getWorld().spawn(location, org.bukkit.entity.Firework.class);

        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(
                FireworkEffect.builder()
                .flicker(r.nextBoolean())
                .withColor(COLORS[r.nextInt(COLORS.length)])
                .withFade(COLORS[r.nextInt(COLORS.length)])
                .with(TYPES[r.nextInt(TYPES.length)])
                .trail(r.nextBoolean())
                .build()
        );
        int fwp = r.nextInt(2) + 1;
        fwm.setPower(fwp);
        fw.setFireworkMeta(fwm);

        return fw;
    }
}