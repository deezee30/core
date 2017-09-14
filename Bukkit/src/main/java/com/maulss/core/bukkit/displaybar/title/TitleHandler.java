/*
 * Part of core.
 * 
 * Created on 21 June 2017 at 8:15 PM.
 */

package com.maulss.core.bukkit.displaybar.title;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.packet.wrapper.WrapperPlayServerTitle;
import com.maulss.core.bukkit.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class TitleHandler {

    TitleHandler() {}

    public void send(final WrapperPlayServerTitle packet,
                     final CorePlayer... players) {
        for (CorePlayer player : players) {
            player.sendPacket(packet);
        }
    }

    public WrapperPlayServerTitle buildClearPacket() {
        WrapperPlayServerTitle packet = new WrapperPlayServerTitle();

        packet.setAction(EnumWrappers.TitleAction.CLEAR);

        return packet;
    }

    public WrapperPlayServerTitle buildTimingPacket(final TitleMessage message) {
        WrapperPlayServerTitle packet = new WrapperPlayServerTitle();

        packet.setAction(EnumWrappers.TitleAction.TIMES);
        if (message.getStay().isPresent())
            packet.setStay(message.getStay().get());
        if (message.getFadeIn().isPresent())
            packet.setFadeIn(message.getFadeIn().get());
        if (message.getFadeOut().isPresent())
            packet.setFadeOut(message.getFadeOut().get());

        return packet;
    }

    public WrapperPlayServerTitle buildTitlePacket(final CorePlayer player,
                                                   final TitleMessage message) {
        WrapperPlayServerTitle packet = new WrapperPlayServerTitle();

        packet.setAction(message.getType().equals(TitleMessage.Type.TITLE)
                ? EnumWrappers.TitleAction.TITLE : EnumWrappers.TitleAction.SUBTITLE);
        if (message.getMessage().isPresent()) {
            packet.setTitle(WrappedChatComponent.fromText(
                    ChatColor.translateAlternateColorCodes('&', Core.getSettings().get(
                            player.getLocale(),
                            message.getMessage().get()
                    ))
            ));
        }

        return packet;
    }

    public void handleTitleSendPacket(final TitleMessage title,
                                      final CorePlayer... players) {
        Bukkit.getScheduler().runTaskLater(Core.get(), () -> {
            boolean animated = title.isAnimated();

            for (CorePlayer player : players) {
                // send the timings first if they're animated
                if (animated && title.getType().equals(TitleMessage.Type.TITLE))
                    send(buildTimingPacket(title), player);

                send(buildTitlePacket(player, title), player);
            }
        }, title.isDelayed() ? title.getAfter().get() : 0L);
    }
}