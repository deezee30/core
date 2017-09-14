/*
 * Part of core.
 * 
 * Created on 01 July 2017 at 12:59 AM.
 */

package com.maulss.core.bukkit.displaybar.actionbar;

import com.maulss.core.bukkit.packet.AbstractPacket;
import com.maulss.core.bukkit.player.CorePlayer;
import org.apache.commons.lang3.Validate;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ActionBarUpdate extends BukkitRunnable {

    // packets are custom for each player due to the possibility
    // of them having different locales set
    private final AtomicReference<Map<CorePlayer, AbstractPacket>>
            respectivePackets = new AtomicReference<>();
    private final CorePlayer[] players;
    private final ActionBar bar;
    private final int times;

    private int count = 0;
    private boolean cancel, update = false;

    public ActionBarUpdate(final ActionBar bar,
                           final int times,
                           final CorePlayer... players) {
        this.bar = Validate.notNull(bar);
        this.times = times;
        this.players = Validate.noNullElements(players);
    }

    public void scheduleCancel() {
        cancel = true;
    }

    public void scheduleUpdate() {
        update = true;
    }

    @Override
    public void run() {
        if (cancel || count == times) {
            cancel();
            return;
        }

        if (update || count == 0) {
            respectivePackets.set(ActionBarHelper.getRespectiveTitles(bar, players));
            update = false;
        }

        for (Map.Entry<CorePlayer, AbstractPacket> entry : respectivePackets.get().entrySet()) {
            entry.getKey().sendPacket(entry.getValue());
        }

        count++;
    }
}