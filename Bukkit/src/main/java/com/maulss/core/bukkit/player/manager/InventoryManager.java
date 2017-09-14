/*
 * Part of core.
 */

package com.maulss.core.bukkit.player.manager;

import com.google.common.collect.ImmutableList;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.inventory.item.IndexedItem;
import com.maulss.core.bukkit.inventory.item.ItemBuilder;
import com.maulss.core.bukkit.player.CorePlayer;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class InventoryManager {

    private final CorePlayer player;

    public InventoryManager(final CorePlayer player) {
        this.player = Validate.notNull(player);
    }

    public void createLocaleMenu() {
        ImmutableList<String> locales = Core.getSettings().getLocales();

        Inventory inv = ci(
                player,
                getFitAmount(locales.size()),
                "menu.locale"
        );

        for (int x = 0; x < locales.size(); ++x) {
            String language = locales.get(x);

            inv.setItem(x, new ItemBuilder(Material.BOOK) {{
                setTitle(ChatColor.AQUA + WordUtils.capitalize(language));
            }}.build());
        }

        player.getPlayer().openInventory(inv);
    }

    private Inventory setItem(final Inventory inventory,
                              final IndexedItem item) {
        inventory.setItem(item.getSlot(), item.buildWithLocaleSupport(player.getLocale()));
        return inventory;
    }

    public static Inventory ci(final CorePlayer player,
                               final int rows,
                               final String title) {
        return Bukkit.getServer().createInventory(
                player.getPlayer(),
                Math.abs(rows * 9) > 54
                        ? 54
                        : Math.abs(rows * 9),
                Core.getSettings().get(player.getLocale(), title)
        );
    }

    public static int getFitAmount(int x) {
        int y = 1;
        for (int z = 1; z <= 6; ++z)
            if (x > 9 * z) ++y;
        return y;
    }
}