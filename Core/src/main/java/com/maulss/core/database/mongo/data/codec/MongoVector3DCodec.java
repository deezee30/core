/*
 * Part of core.
 * 
 * Created on 07 July 2017 at 12:14 AM.
 */

package com.maulss.core.database.mongo.data.codec;

import com.maulss.core.math.Vector3D;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class MongoVector3DCodec implements Codec<Vector3D> {

    @Override
    public Vector3D decode(final BsonReader reader,
                           final DecoderContext decoderContext) {
        reader.readStartDocument();
            double x = reader.readDouble("x");
            double y = reader.readDouble("y");
            double z = reader.readDouble("z");
        reader.readEndDocument();

        return new Vector3D(x, y, z);
    }

    @Override
    public void encode(final BsonWriter writer,
                       final Vector3D value,
                       final EncoderContext encoderContext) {
        writer.writeStartDocument();
            writer.writeDouble("x", value.getX());
            writer.writeDouble("y", value.getY());
            writer.writeDouble("z", value.getZ());
        writer.writeEndDocument();
    }

    @Override
    public Class<Vector3D> getEncoderClass() {
        return Vector3D.class;
    }
}