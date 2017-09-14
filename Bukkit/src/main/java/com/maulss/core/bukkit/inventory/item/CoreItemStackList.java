/*
 * Part of core.
 */

package com.maulss.core.bukkit.inventory.item;

import com.maulss.core.bukkit.Core;
import com.maulss.core.collect.EnhancedList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public final class CoreItemStackList extends EnhancedList<CoreItemStack> {

    public CoreItemStackList() {}

    public CoreItemStackList(final ItemStack... itemStacks) {
        for (ItemStack is : itemStacks) add(new CoreItemStack(is));
    }

    public CoreItemStackList(final CoreItemStack... itemStacks) {
        this.addAll(Arrays.asList(itemStacks));
    }

    public CoreItemStack remove(final Material material) {
        return remove(getFirstSlot(material));
    }

    public ItemStack[] toItemStack() {
        ItemStack[] is = new ItemStack[size()];
        for (int x = 0; x < size(); ++x) {
            is[x] = get(x).getItemStack();
        }

        return is;
    }

    public int getFirstSlot(final Material mat) {
        for (int x = 0; x < size(); x++) {
            if (get(x) != null) {
                if (get(x).getMaterial().equals(mat)) {
                    return x;
                }
            }
        }

        // if mat has not been found in the list of items return -1
        return -1;
    }

    public void setFirstNull(final CoreItemStack item) {
        int x = 0;
        for (CoreItemStack i : this) {
            if (i == null
                    || i.getItemStack() == null
                    || i.getItemStack().getType().equals(Material.AIR)) {
                set(x, item);
                return;
            }
            ++x;
        }
    }

    @Override
    public CoreItemStack[] toArray() {
        return toArray(new CoreItemStack[size()]);
    }
}