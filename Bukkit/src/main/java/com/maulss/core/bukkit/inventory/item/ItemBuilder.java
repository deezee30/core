/*
 * Part of core.
 */

package com.maulss.core.bukkit.inventory.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.maulss.core.bukkit.Core;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;
import java.util.regex.Pattern;

public class ItemBuilder implements Cloneable {

    private int amount;
    private Color color;
    private short data;
    private final HashMap<Enchantment, Integer> enchants = Maps.newHashMap();
    private List<String> lore = Lists.newArrayList();
    private Material type;
    private String title;

    public ItemBuilder(ItemStack item) {
        this(item.getType(), item.getDurability());
        amount = item.getAmount();
        enchants.putAll(item.getEnchantments());
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                title = meta.getDisplayName();
            }
            if (meta.hasLore()) {
                lore.addAll(meta.getLore());
            }
            if (meta instanceof LeatherArmorMeta) {
                setColor(((LeatherArmorMeta) meta).getColor());
            }
        }
    }

    public ItemBuilder(Material mat) {
        this(mat, 1);
    }

    public ItemBuilder(Material mat, int amount) {
        this(mat, amount, (short) 0);
    }

    public ItemBuilder(Material mat, int amount, short data) {
        this.type = Validate.notNull(mat);
        this.amount = Validate.notNull(amount);
        this.data = Validate.notNull(data);
    }

    public ItemBuilder(Material mat, short data) {
        this(mat, 1, data);
    }

    public ItemBuilder addEnchantment(Enchantment enchant, int level) {
        Validate.notNull(enchant);
        if (enchants.containsKey(enchant)) {
            enchants.remove(enchant);
        }

        enchants.put(enchant, level);
        return this;
    }

    public ItemBuilder addLore(String... lores) {
        if (lores != null) {
            Collections.addAll(this.lore, lores);
        }

        return this;
    }

    public ItemBuilder addLore(String lore, int maxLength) {
        if (lore != null) {
            this.lore.addAll(split(lore, maxLength));
        }

        return this;
    }

    public ItemBuilder addLores(List<String> lores) {
        if (lores != null) {
            this.lore.addAll(lores);
        }

        return this;
    }

    public ItemBuilder addLores(List<String> lores, int maxLength) {
        if (lores != null) {
            lores.forEach(lore -> addLore(lore, maxLength));
        }

        return this;
    }

    public int getAmount() {
        return amount;
    }

    public Color getColor() {
        return color;
    }

    public short getData() {
        return data;
    }

    public HashMap<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    private static ArrayList<String> split(String string, int maxLength) {
        String[] split = string.split(" ");
        string = "";
        ArrayList<String> newString = new ArrayList<>();
        for (String aSplit : split) {
            string += (string.length() == 0 ? "" : " ") + aSplit;
            if (ChatColor.stripColor(string).length() > maxLength) {
                newString.add((newString.size() > 0 ? ChatColor.getLastColors(newString.get(newString.size() - 1)) : "") + string);
                string = "";
            }
        }
        if (string.length() > 0)
            newString.add((newString.size() > 0 ? ChatColor.getLastColors(newString.get(newString.size() - 1)) : "") + string);
        return newString;
    }

    public ItemStack build() {
        return buildWithLocaleSupport(Core.getSettings().getDefaultLocale());
    }

    public ItemStack buildWithLocaleSupport(String locale) {
        if (type == null) {
            type = Material.AIR;
        }

        ItemStack item = new ItemStack(type, amount, data);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (title != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7" + Core.getSettings().get(locale, title)));
            }

            if (!lore.isEmpty()) {
                List<String> lore = Lists.newLinkedList();
                for (String l : this.lore) {
                    String l0 = Core.getSettings().get(locale, l);
                    if (l0 == null) l0 = "%n";
                    Collections.addAll(lore, ChatColor.translateAlternateColorCodes('&', "&7" + l0).split(Pattern.quote("%n")));
                }
                meta.setLore(lore);
            }

            if (meta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta) meta).setColor(color);
            }

            item.setItemMeta(meta);
        }

        item.addUnsafeEnchantments(enchants);
        return item;
    }

    @Override
    public ItemBuilder clone() throws CloneNotSupportedException {
        ItemBuilder newBuilder = new ItemBuilder(type);
        newBuilder.setTitle(title);
        lore.forEach(newBuilder::addLore);
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            newBuilder.addEnchantment(entry.getKey(), entry.getValue());
        }
        newBuilder.setColor(color);
        return newBuilder;
    }

    public int getEnchantmentLevel(Enchantment enchant) {
        return enchants.get(enchant);
    }

    public boolean hasEnchantment(Enchantment enchant) {
        return enchants.containsKey(enchant);
    }

    public ItemBuilder setData(short data) {
        this.data = data;
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setColor(Color color) {
        if (!type.name().contains("LEATHER_")) {
            throw new IllegalArgumentException("Can only dye leather materials!");
        }
        this.color = color;
        return this;
    }

    public ItemBuilder setTitle(String title) {
        this.title = title;

        return this;
    }

    public ItemBuilder setTitle(String title, int maxLength) {
        if (title != null && ChatColor.stripColor(title).length() > maxLength) {
            ArrayList<String> lores = split(title, maxLength);
            for (int i = 1; i < lores.size(); i++) {
                lore.add(lores.get(i));
            }
            title = lores.get(0);
        }

        return setTitle(title);
    }

    public ItemBuilder setType(Material mat) {
        this.type = mat;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("amount", amount)
                .append("color", color)
                .append("data", data)
                .append("enchants", enchants)
                .append("lore", lore)
                .append("type", type)
                .append("title", title)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ItemBuilder that = (ItemBuilder) o;

        return new EqualsBuilder()
                .append(amount, that.amount)
                .append(data, that.data)
                .append(color, that.color)
                .append(enchants, that.enchants)
                .append(lore, that.lore)
                .append(type, that.type)
                .append(title, that.title)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(amount)
                .append(color)
                .append(data)
                .append(enchants)
                .append(lore)
                .append(type)
                .append(title)
                .toHashCode();
    }
}