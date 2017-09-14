/*
 * Part of core.
 * 
 * Created on 10 July 2017 at 2:10 PM.
 */

package com.maulss.core.bukkit.sign;

import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.bukkit.player.manager.CorePlayerManager;
import com.maulss.core.collect.EnhancedMap;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public final class SignHandler {

    private static final SignHandler instance = new SignHandler();

    private final EnhancedMap<Sign, SignClick> signListeners = new EnhancedMap<>();

    private SignHandler() {}

    public static void registerSignClickListener(final Block sign,
                                                 final SignClickListener listener,
                                                 final SignClickType clickType) {
        instance.signListeners.putIf(isSign(sign), (Sign) sign, new SignClick(listener, clickType));
    }

    public static void unregister(final Block sign) {
        instance.signListeners.removeIf(isSign(sign), (Sign) sign);
    }

    public static void handleSignClick(final Block block,
                                       final Player player,
                                       final Action action) {
        if (!action.equals(Action.LEFT_CLICK_BLOCK)
                || !action.equals(Action.RIGHT_CLICK_BLOCK)
                || !isSign(block)) {
            return;
        }

        Sign sign = (Sign) block;

        if (instance.signListeners.containsKey(sign)) {
            CorePlayer cPlayer = CorePlayerManager.getInstance().get(player.getUniqueId());
            instance.signListeners.get(sign).trigger(cPlayer, block, action);
        }
    }

    public static boolean isSign(final Block block) {
        return isSign(block.getType());
    }

    public static boolean isSign(final Material mat) {
        Validate.notNull(mat);
        switch (mat) {
            case SIGN:
            case SIGN_POST:
                return true;
            default:
                return false;
        }
    }

    public static SignHandler getInstance() {
        return instance;
    }
}