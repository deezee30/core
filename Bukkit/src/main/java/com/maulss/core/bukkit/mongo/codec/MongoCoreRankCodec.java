/*
 * Part of core.
 */

package com.maulss.core.bukkit.mongo.codec;

import com.maulss.core.bukkit.internal.CoreRank;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public final class MongoCoreRankCodec implements Codec<CoreRank> {

    @Override
    public CoreRank decode(final BsonReader reader,
                           final DecoderContext decoderContext) {
        return CoreRank.byName(reader.readString());
    }

    @Override
    public void encode(final BsonWriter writer,
                       final CoreRank value,
                       final EncoderContext encoderContext) {
        writer.writeString(value.getName());
    }

    @Override
    public Class<CoreRank> getEncoderClass() {
        return CoreRank.class;
    }
}