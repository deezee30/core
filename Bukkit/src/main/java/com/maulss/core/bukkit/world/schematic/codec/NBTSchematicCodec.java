/*
 * Part of core.
 * 
 * Created on 16 July 2017 at 11:57 PM.
 */

package com.maulss.core.bukkit.world.schematic.codec;

import com.maulss.core.jnbt.NBTInputStream;
import com.maulss.core.jnbt.NBTOutputStream;
import com.maulss.core.jnbt.NamedTag;
import com.maulss.core.jnbt.Tag;
import com.maulss.core.jnbt.type.*;
import com.maulss.core.bukkit.world.schematic.Schematic;
import com.maulss.core.bukkit.world.schematic.SchematicData;
import com.maulss.core.math.Vector3D;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.maulss.core.jnbt.NBTUtils.getChildTag;

public class NBTSchematicCodec implements SchematicCodec {

    private static final int MAX_SIZE = Short.MAX_VALUE - Short.MIN_VALUE;

    NBTSchematicCodec() {}

    @Override
    public SchematicData decode(final File file) throws SchematicCodecException {
        try (
                InputStream is = new FileInputStream(file);
                NBTInputStream nbtStream = new NBTInputStream(is)
        ) {
            Vector3D origin;
            Vector3D offset;

            // Schematic tag
            NamedTag rootTag = nbtStream.readNamedTag();
            nbtStream.close();
            if (!rootTag.getName().equals("Schematic")) {
                throw new SchematicCodecException("Tag \"Schematic\" does not exist or is not first");
            }

            CompoundTag schematicTag = (CompoundTag) rootTag.getTag();

            // Check
            Map<String, Tag> schematic = schematicTag.getValue();
            if (!schematic.containsKey("Blocks")) {
                throw new SchematicCodecException("Schematic file is missing a \"Blocks\" tag");
            }

            // Get information
            short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
            short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
            short height = getChildTag(schematic, "Height", ShortTag.class).getValue();

            // In case origin is needed
            /*
            try {
                int originX = getChildTag(schematic, "WEOriginX", IntTag.class).getValue();
                int originY = getChildTag(schematic, "WEOriginY", IntTag.class).getValue();
                int originZ = getChildTag(schematic, "WEOriginZ", IntTag.class).getValue();
                origin = new Vector3D(originX, originY, originZ);
            } catch (Exception e) {
                // No origin data
            }*/

            // In case offset is needed
            /*
            try {
                int offsetX = getChildTag(schematic, "WEOffsetX", IntTag.class).getValue();
                int offsetY = getChildTag(schematic, "WEOffsetY", IntTag.class).getValue();
                int offsetZ = getChildTag(schematic, "WEOffsetZ", IntTag.class).getValue();
                offset = new Vector3D(offsetX, offsetY, offsetZ);
            } catch (Exception e) {
                // No offset data
            }*/

            // Check type of Schematic
            String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
            if (!materials.equals("Alpha")) {
                throw new SchematicCodecException("Schematic file is not an Alpha schematic");
            }

            // Get blocks
            byte[] blockId = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
            byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
            byte[] addId = new byte[0];
            short[] blocks = new short[blockId.length]; // Have to later combine IDs

            // We support 4096 block IDs using the same method as vanilla Minecraft, where
            // the highest 4 bits are stored in a separate byte array.
            if (schematic.containsKey("AddBlocks")) {
                addId = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
            }

            // Combine the AddBlocks data with the first 8-bit block ID
            for (int index = 0; index < blockId.length; index++) {
                if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
                    blocks[index] = (short) (blockId[index] & 0xFF);
                } else {
                    if ((index & 1) == 0) {
                        blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
                    } else {
                        blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
                    }
                }
            }

            // Need to pull out tile entities
            List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class).getValue();
            Map<BlockVector, Map<String, Tag>> tileEntitiesMap = new HashMap<>();

            for (Tag tag : tileEntities) {
                if (!(tag instanceof CompoundTag)) continue;
                CompoundTag t = (CompoundTag) tag;

                int x = 0;
                int y = 0;
                int z = 0;

                Map<String, Tag> values = new HashMap<>();

                for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
                    if (entry.getKey().equals("x")) {
                        if (entry.getValue() instanceof IntTag) {
                            x = ((IntTag) entry.getValue()).getValue();
                        }
                    } else if (entry.getKey().equals("y")) {
                        if (entry.getValue() instanceof IntTag) {
                            y = ((IntTag) entry.getValue()).getValue();
                        }
                    } else if (entry.getKey().equals("z")) {
                        if (entry.getValue() instanceof IntTag) {
                            z = ((IntTag) entry.getValue()).getValue();
                        }
                    }

                    values.put(entry.getKey(), entry.getValue());
                }

                BlockVector vec = new BlockVector(x, y, z);
                tileEntitiesMap.put(vec, values);
            }

            return new SchematicData(
                    blocks, blockData,
                    new Vector3D(width, height, length),
                    null, null, tileEntitiesMap);
        } catch (IOException e) {
            throw new SchematicCodecException(e);
        }
    }

    @Override
    public File encode(final Schematic schematic,
                       final World world) throws SchematicCodecException {
        File file = schematic.getDefaultFile();

        try {
            // attempt create new empty file if doesn't exist
            file.createNewFile();

            SchematicData data = schematic.getData();

            Vector3D size = data.getSize();
            int width = size.getFloorX();
            int height = size.getFloorY();
            int length = size.getFloorZ();

            if (width > MAX_SIZE) {
                throw new SchematicCodecException("Width of region too large for a .schematic");
            }
            if (height > MAX_SIZE) {
                throw new SchematicCodecException("Height of region too large for a .schematic");
            }
            if (length > MAX_SIZE) {
                throw new SchematicCodecException("Length of region too large for a .schematic");
            }

            HashMap<String, Tag> sch = new HashMap<>();
            sch.put("Width", new ShortTag((short) width));
            sch.put("Length", new ShortTag((short) length));
            sch.put("Height", new ShortTag((short) height));
            sch.put("Materials", new StringTag("Alpha"));

            // In case origin is needed
            /*
            sch.put("WEOriginX", new IntTag(clipboard.getOrigin().getBlockX()));
            sch.put("WEOriginY", new IntTag(clipboard.getOrigin().getBlockY()));
            sch.put("WEOriginZ", new IntTag(clipboard.getOrigin().getBlockZ()));
            */

            // In case offset is needed
            /*
            sch.put("WEOffsetX", new IntTag(clipboard.getOffset().getBlockX()));
            sch.put("WEOffsetY", new IntTag(clipboard.getOffset().getBlockY()));
            sch.put("WEOffsetZ", new IntTag(clipboard.getOffset().getBlockZ()));
            */

            // Copy
            byte[] blocks = new byte[width * height * length];
            byte[] addBlocks = null;
            byte[] blockData = new byte[width * height * length];
            ArrayList<Tag> tileEntities = new ArrayList<>();

            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    for (int z = 0; z < length; ++z) {
                        Block block = world.getBlockAt(x, y, z);
                        int id = block.getTypeId();
                        int index = y * width * length + z * width + x;

                        // Save 4096 IDs in an AddBlocks section
                        if (id > 255) {
                            if (addBlocks == null) { // Lazily create section
                                addBlocks = new byte[(blocks.length >> 1) + 1];
                            }

                            addBlocks[index >> 1] = (byte) (((index & 1) == 0)
                                    ? addBlocks[index >> 1] & 0xF0 | (id >> 8) & 0xF
                                    : addBlocks[index >> 1] & 0xF | ((id >> 8) & 0xF) << 4);
                        }

                        blocks[index] = (byte) id;
                        blockData[index] = block.getData();

                        // Save tile entities if needed
                        // Get the list of key/values from the block
                        /*
                        CompoundTag rawTag = block.getNbtData();
                        if (rawTag != null) {
                            Map<String, Tag> values = new HashMap<>();
                            for (Map.Entry<String, Tag> entry : rawTag.getValue().entrySet()) {
                                values.put(entry.getKey(), entry.getValue());
                            }

                            values.put("id", new StringTag(block.getNbtId()));
                            values.put("x", new IntTag(x));
                            values.put("y", new IntTag(y));
                            values.put("z", new IntTag(z));

                            CompoundTag tileEntityTag = new CompoundTag(values);
                            tileEntities.add(tileEntityTag);
                        }*/
                    }
                }
            }

            sch.put("Blocks", new ByteArrayTag(blocks));
            sch.put("Data", new ByteArrayTag(blockData));
            sch.put("Entities", new ListTag(CompoundTag.class, new ArrayList<>()));
            sch.put("TileEntities", new ListTag(CompoundTag.class, tileEntities));
            if (addBlocks != null) {
                sch.put("AddBlocks", new ByteArrayTag(addBlocks));
            }

            // Build and output
            CompoundTag schematicTag = new CompoundTag(sch);

            try (NBTOutputStream stream = new NBTOutputStream(new FileOutputStream(file))) {
                stream.writeNamedTag("Schematic", schematicTag);
            }
        } catch (IOException e) {
            throw new SchematicCodecException(e);
        }

        return file;
    }
}