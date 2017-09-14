/*
 * Part of core.
 */

package com.maulss.core.bukkit.inventory;

import com.google.common.collect.ImmutableList;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.inventory.item.IndexedItem;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.collect.EnhancedList;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.List;

public final class InventoryMenu implements Serializable {

    private static final int ELEMENTS_PER_ROW	= 9;
    private static final int MIN_ROWS			= 1;
    private static final int MAX_ROWS			= 6;
    private static final int MIN_SIZE			= MIN_ROWS * ELEMENTS_PER_ROW;
    private static final int MAX_SIZE			= MAX_ROWS * ELEMENTS_PER_ROW;

    private final String titlePath;
    private transient final EnhancedList<IndexedItem> items = new EnhancedList<>(MAX_SIZE);
    private int rows = MIN_ROWS;

    public InventoryMenu(final String titlePath,
                         final CoreInventoryClickEvent event) {
        Validate.notNull(event);
        this.titlePath = Validate.notNull(titlePath);
        Core.getSettings().registerInventory(titlePath, event);
    }

    public void setRows(final int rows) {
        this.rows = isRowCountApplicable(rows) ? 0 : rows;
    }

    public void setItem(final int slot,
                        final ItemStack item) {
        Validate.notNull(item);
        setItem(new IndexedItem(item, slot));
    }

    public void setItem(final IndexedItem item) {
        Validate.notNull(item);
        int slot = item.getSlot();

        // Replace old item with new one if slot is the same
        items.removeIf(getBySlot(slot) != null, getBySlot(slot));
        items.addIf(isSizeApplicable(slot), item);
    }

    public ImmutableList<IndexedItem> getItems() {
        return items.getImmutableElements();
    }

    public void open(final CorePlayer... players) {
        for (CorePlayer player : players) {
            Player p = player.getPlayer();
            String locale = player.getLocale();
            Inventory inv = Bukkit.createInventory(
                    p,
                    getApplicableRows() * ELEMENTS_PER_ROW,
                    Core.getSettings().get(locale, titlePath)
            );

            for (IndexedItem item : items) {
                inv.setItem(item.getSlot(), item.buildWithLocaleSupport(locale));
            }

            p.openInventory(inv);
        }
    }

    private IndexedItem getBySlot(final int slot) {
        for (IndexedItem item : items) {
            if (slot == item.getSlot()) {
                return item;
            }
        }

        return null;
    }

    private int getApplicableRows() {
        if (rows == 0 || rows * ELEMENTS_PER_ROW < getLastSlot(items)) {
            return getRowCountFromSize(getLastSlot(items));
        } else {
            return rows;
        }
    }

    public static int getLastSlot(final List<IndexedItem> items) {
        int x = 0;

        for (IndexedItem item : items) {
            if (item.getSlot() > x) {
                x = item.getSlot();
            }
        }

        return x;
    }

    public static boolean isRowCountApplicable(final int rows) {
        return rows >= MIN_ROWS && rows <= MAX_ROWS;
    }

    public static boolean isSizeApplicable(final int size) {
        return size >= 0 && size <= MAX_SIZE;
    }

    public static int getRowCountFromSize(final int x) {
        int y = 1;
        for (int z = MIN_ROWS; z <= MAX_ROWS; ++z)
            if (x > ELEMENTS_PER_ROW * z) ++y;
        return y;
    }
}