/*
 * Part of core.
 */
package com.maulss.core.bukkit.player.event;

import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CorePlayerDeathByPlayerEvent extends CoreConnectedPlayerEvent {

    private final CorePlayer killer;
    private boolean autoRespawn = true;
    private boolean victimMsg = true;
    private boolean killerMsg = true;
    private boolean playSound = true;
    private boolean clearDrops = true;
    private boolean clearExp = true;

    public CorePlayerDeathByPlayerEvent(final PlayerDeathEvent event) {
        super(CorePlayerManager.getInstance().get(event));
        killer = CorePlayerManager.getInstance().get(getPlayer().getPlayer().getKiller().getName());
        String deathMsg = event.getDeathMessage();
        if (deathMsg != null)
            event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', deathMsg));
        Validate.notNull(killer, "Killer doesn't exist!");
    }

    public CorePlayer getKiller() {
        return killer;
    }

    public boolean doAutoRespawn() {
        return autoRespawn;
    }

    public void autoRespawn(boolean autoRespawn) {
        this.autoRespawn = autoRespawn;
    }

    public boolean doSendVictimMessage() {
        return victimMsg;
    }

    public void sendVictimMessage(boolean victimMsg) {
        this.victimMsg = victimMsg;
    }

    public boolean doSendKillerMessage() {
        return killerMsg;
    }

    public void sendKillerMessage(boolean killerMsg) {
        this.killerMsg = killerMsg;
    }

    public boolean doPlaySound() {
        return playSound;
    }

    public void playSound(boolean playSound) {
        this.playSound = playSound;
    }

    public boolean doClearDrops() {
        return clearDrops;
    }

    public void clearDrops(boolean clearDrops) {
        this.clearDrops = clearDrops;
    }

    public boolean doClearExp() {
        return clearExp;
    }

    public void clearExp(boolean clearExp) {
        this.clearExp = clearExp;
    }
}