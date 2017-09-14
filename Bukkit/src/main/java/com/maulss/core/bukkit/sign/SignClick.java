/*
 * Part of core.
 * 
 * Created on 10 July 2017 at 2:58 PM.
 */

package com.maulss.core.bukkit.sign;

import com.maulss.core.bukkit.player.CorePlayer;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;

public class SignClick {

    private final SignClickListener listener;
    private final SignClickType clickType;

    public SignClick(final SignClickListener listener,
                     final SignClickType clickType) {
        this.listener = Validate.notNull(listener);
        this.clickType = Validate.notNull(clickType);
    }

    public boolean trigger(final CorePlayer player,
                           final Block block,
                           final Action action) {
        if (!SignHandler.isSign(block)) return false;

        Sign sign = (Sign) block;

        if (clickType.equals(action)) {
            listener.onClick(player, sign);
            return true;
        }

        return false;
    }

    public SignClickListener getListener() {
        return listener;
    }

    public SignClickType getClickType() {
        return clickType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SignClick signClick = (SignClick) o;

        return new EqualsBuilder()
                .append(listener, signClick.listener)
                .append(clickType, signClick.clickType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(listener)
                .append(clickType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("listener", listener)
                .append("clickType", clickType)
                .toString();
    }
}