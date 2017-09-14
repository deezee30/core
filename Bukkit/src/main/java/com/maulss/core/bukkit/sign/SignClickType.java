/*
 * Part of core.
 * 
 * Created on 10 July 2017 at 2:45 PM.
 */

package com.maulss.core.bukkit.sign;

import org.bukkit.event.block.Action;

public enum SignClickType {

    LEFT_MOUSE,
    RIGHT_MOUSE,
    EITHER;

    public boolean equals(Action action) {
        switch (this) {
            case EITHER: return true;
            case LEFT_MOUSE: return action.equals(Action.LEFT_CLICK_BLOCK);
            case RIGHT_MOUSE: return action.equals(Action.RIGHT_CLICK_BLOCK);
            default: return false;
        }
    }
}