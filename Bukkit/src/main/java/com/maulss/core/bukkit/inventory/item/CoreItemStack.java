/*
 * Part of core.
 */

package com.maulss.core.bukkit.inventory.item;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CoreItemStack {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    private Material material = Material.AIR;
    private int quantity = 1;

    private Optional<String> displayName = Optional.empty();
    private Optional<List<String>> lore = Optional.empty();
    private Optional<Map<Enchantment, Integer>> enchantments = Optional.empty();
    private Optional<Integer> color = Optional.empty();

    public CoreItemStack(final ItemStack item) {
        if (item != null) {
            material = item.getType();
            quantity = item.getAmount();

            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof LeatherArmorMeta) {
                    color = Optional.of(((LeatherArmorMeta) meta).getColor().asRGB());
                }
                displayName = Optional.ofNullable(meta.getDisplayName());
                lore = Optional.ofNullable(meta.getLore());
            }

            enchantments = Optional.of(item.getEnchantments());
        }
    }

    public CoreItemStack(final Container item) {
        Validate.notNull(item);
        this.material = Material.getMaterial(item.material);
        this.quantity = item.quantity;
        this.displayName = Optional.ofNullable(item.title);
        this.color = Optional.ofNullable(item.color);
        this.lore = Optional.ofNullable(item.lore);
        if (item.enchantments != null) {
            Map<Enchantment, Integer> map = Maps.newHashMapWithExpectedSize(item.enchantments.size());
            for (Map.Entry<String, Integer> entry : item.enchantments.entrySet()) {
                map.put(EnchantmentWrapper.getByName(entry.getKey()), entry.getValue());
            }

            enchantments = Optional.of(map);
        }
    }

    public Material getMaterial() {
        return material;
    }

    public int getQuantity() {
        return quantity;
    }

    public Optional<String> getDisplayName() {
        return displayName;
    }

    public Optional<List<String>> getLore() {
        return lore;
    }

    public Optional<Map<Enchantment, Integer>> getEnchantments() {
        return enchantments;
    }

    public Optional<Color> getColor() {
        return color.map(Color::fromRGB);
    }

    public ItemStack getItemStack() {
        return new ItemBuilder(material, quantity) {{
            if (displayName.isPresent())
                setTitle(displayName.get());
            if (color.isPresent())
                setColor(CoreItemStack.this.getColor().get());
            if (lore.isPresent())
                addLores(lore.get());
            enchantments.ifPresent(map -> map.forEach(this::addEnchantment));
        }}.build();
    }

    public String toJson() {
        Map<String, Integer> enchantments = null;
        if (this.enchantments.isPresent()) {
            enchantments = Maps.newHashMapWithExpectedSize(this.enchantments.get().size());
            for (Map.Entry<Enchantment, Integer> entry : this.enchantments.get().entrySet()) {
                enchantments.put(entry.getKey().getName(), entry.getValue());
            }
        }

        return GSON.toJson(new Container(
                material.name(),
                quantity,
                displayName.orElse(null),
                color.orElse(null),
                lore.orElse(null),
                enchantments
        ));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CoreItemStack that = (CoreItemStack) o;

        return new EqualsBuilder()
                .append(quantity, that.quantity)
                .append(color, that.color)
                .append(material, that.material)
                .append(displayName, that.displayName)
                .append(lore, that.lore)
                .append(enchantments, that.enchantments)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(material)
                .append(quantity)
                .append(displayName)
                .append(lore)
                .append(enchantments)
                .append(color)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("material", material)
                .append("quantity", quantity)
                .append("displayName", displayName)
                .append("lore", lore)
                .append("enchantments", enchantments)
                .append("color", color)
                .toString();
    }

    public static CoreItemStack fromJSONString(String json) {
        return new CoreItemStack(GSON.fromJson(json, Container.class));
    }

    public static final class Container {

        private final String material;
        private final int quantity;

        private String title;
        private Integer color;
        private List<String> lore;
        private Map<String, Integer> enchantments;

        public Container(final String material) {
            this(material, 1);
        }

        public Container(final String material,
                         final int quantity) {
            this(material, quantity, null, null, null, null);
        }

        public Container(final String material,
                         final int quantity,
                         final String title,
                         final Integer color,
                         final List<String> lore,
                         final Map<String, Integer> enchantments) {
            this.material = Validate.notNull(material);
            this.quantity = Validate.notNull(quantity);

            this.title = title;
            this.color = color;
            this.lore = lore;
            this.enchantments = enchantments;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getTitle() {
            return title;
        }

        public Integer getColor() {
            return color;
        }

        public List<String> getLore() {
            return lore;
        }

        public Map<String, Integer> getEnchantments() {
            return enchantments;
        }

        public String getMaterial() {

            return material;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("material", material)
                    .append("quantity", quantity)
                    .append("title", title)
                    .append("color", color)
                    .append("lore", lore)
                    .append("enchantments", enchantments)
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Container that = (Container) o;

            return new EqualsBuilder()
                    .append(quantity, that.quantity)
                    .append(color, that.color)
                    .append(material, that.material)
                    .append(title, that.title)
                    .append(lore, that.lore)
                    .append(enchantments, that.enchantments)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(material)
                    .append(quantity)
                    .append(title)
                    .append(color)
                    .append(lore)
                    .append(enchantments)
                    .toHashCode();
        }
    }
}