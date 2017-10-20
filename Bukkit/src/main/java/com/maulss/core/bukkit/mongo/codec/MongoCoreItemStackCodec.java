/*
 * Part of core.
 */

package com.maulss.core.bukkit.mongo.codec;


import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.maulss.core.bukkit.inventory.item.CoreItemStack;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;

@Beta
public class MongoCoreItemStackCodec implements Codec<CoreItemStack> {

    @Override
    public CoreItemStack decode(final BsonReader reader,
                                final DecoderContext decoderContext) {
        reader.readStartDocument();

        final String mat = reader.readString("material");
        final int qtty = reader.readInt32("quantity");

        String title = null;
        Integer color = null;
        List<String> lore = null;
        Map<String, Integer> enchantments = null;

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String field = reader.readName();

            if (field.equals("title")) {
                title = reader.readString();
            }

            if (field.equals("color")) {
                color = reader.readInt32();
            }

            if (field.equals("lore")) {
                lore = Lists.newArrayList();

                reader.readStartArray();
                String line;
                while ((line = reader.readString()) != null) {
                    lore.add(line);
                }
                reader.readEndArray();
            }

            if (field.equals("enchantments")) {
                enchantments = Maps.newHashMap();

                reader.readStartDocument();
                String enchantment;
                while ((enchantment = reader.readString()) != null) {
                    enchantments.put(enchantment, reader.readInt32());
                }
                reader.readEndDocument();
            }
        }

        reader.readEndDocument();

        return new CoreItemStack(new CoreItemStack.Container(mat, qtty, title, color, lore, enchantments));
    }

    @Override
    public void encode(final BsonWriter writer,
                       final CoreItemStack value,
                       final EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeString("material", value.getMaterial().name());
        writer.writeInt32("quantity", value.getQuantity());

        if (value.getDisplayName().isPresent()) {
            writer.writeString("title", value.getDisplayName().get());
        }

        if (value.getColor().isPresent()) {
            writer.writeInt32("color", value.getColor().get().asRGB());
        }

        if (value.getLore().isPresent()) {
            writer.writeStartArray("lore");
            value.getLore().get().forEach(writer::writeString);
            writer.writeEndArray();
        }

        if (value.getEnchantments().isPresent()) {
            writer.writeStartDocument();
            for (Map.Entry<Enchantment, Integer> entry : value.getEnchantments().get().entrySet()) {
                writer.writeInt32(entry.getKey().getName(), entry.getValue());
            }
            writer.writeEndDocument();
        }

        writer.writeEndDocument();
    }

    @Override
    public Class<CoreItemStack> getEncoderClass() {
        return CoreItemStack.class;
    }
}