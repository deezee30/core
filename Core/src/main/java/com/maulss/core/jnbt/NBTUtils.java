package com.maulss.core.jnbt;

import com.maulss.core.jnbt.type.*;
import com.maulss.core.math.Vector3D;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.maulss.core.jnbt.NBTConstants.*;

/**
 * A class which contains NBT-related utility methods.
 */
public final class NBTUtils {

    /**
     * Default private constructor.
     */
    private NBTUtils() {}

    /**
     * Gets the type name of a tag.
     *
     * @param clazz the tag class
     * @return The type name.
     */
    public static String toTypeName(final Class<? extends Tag> clazz) {
        if (clazz.equals(ByteArrayTag.class))       return "TAG_Byte_Array";
        else if (clazz.equals(ByteTag.class))       return "TAG_Byte";
        else if (clazz.equals(CompoundTag.class))   return "TAG_Compound";
        else if (clazz.equals(DoubleTag.class))     return "TAG_Double";
        else if (clazz.equals(EndTag.class))        return "TAG_End";
        else if (clazz.equals(FloatTag.class))      return "TAG_Float";
        else if (clazz.equals(IntTag.class))        return "TAG_Int";
        else if (clazz.equals(ListTag.class))       return "TAG_List";
        else if (clazz.equals(LongTag.class))       return "TAG_Long";
        else if (clazz.equals(ShortTag.class))      return "TAG_Short";
        else if (clazz.equals(StringTag.class))     return "TAG_String";
        else if (clazz.equals(IntArrayTag.class))   return "TAG_Int_Array";
        else throw new IllegalArgumentException("Invalid tag classs ("
                    + clazz.getName() + ").");
    }

    /**
     * Gets the type code of a tag class.
     *
     * @param clazz the tag class
     * @return The type code.
     * @throws IllegalArgumentException if the tag class is invalid.
     */
    public static int toTypeCode(final Class<? extends Tag> clazz) {
        if (clazz.equals(ByteArrayTag.class))       return TYPE_BYTE_ARRAY;
        else if (clazz.equals(ByteTag.class))       return TYPE_BYTE;
        else if (clazz.equals(CompoundTag.class))   return TYPE_COMPOUND;
        else if (clazz.equals(DoubleTag.class))     return TYPE_DOUBLE;
        else if (clazz.equals(EndTag.class))        return TYPE_END;
        else if (clazz.equals(FloatTag.class))      return TYPE_FLOAT;
        else if (clazz.equals(IntTag.class))        return TYPE_INT;
        else if (clazz.equals(ListTag.class))       return TYPE_LIST;
        else if (clazz.equals(LongTag.class))       return TYPE_LONG;
        else if (clazz.equals(ShortTag.class))      return TYPE_SHORT;
        else if (clazz.equals(StringTag.class))     return TYPE_STRING;
        else if (clazz.equals(IntArrayTag.class))   return TYPE_INT_ARRAY;
        else throw new IllegalArgumentException("Invalid tag classs ("
                    + clazz.getName() + ").");
    }

    /**
     * Convert a type ID to its corresponding {@link Tag} class.
     *
     * @param id type ID
     * @return tag class
     * @throws IllegalArgumentException thrown if the tag ID is not valid
     */
    public static Class<? extends Tag> fromTypeCode(final int id) {
        switch (id) {
        case TYPE_END:          return EndTag.class;
        case TYPE_BYTE:         return ByteTag.class;
        case TYPE_SHORT:        return ShortTag.class;
        case TYPE_INT:          return IntTag.class;
        case TYPE_LONG:         return LongTag.class;
        case TYPE_FLOAT:        return FloatTag.class;
        case TYPE_DOUBLE:       return DoubleTag.class;
        case TYPE_BYTE_ARRAY:   return ByteArrayTag.class;
        case TYPE_STRING:       return StringTag.class;
        case TYPE_LIST:         return ListTag.class;
        case TYPE_COMPOUND:     return CompoundTag.class;
        case TYPE_INT_ARRAY:    return IntArrayTag.class;
        default:
            throw new IllegalArgumentException("Unknown tag type ID of " + id);
        }
    }

    /**
     * Read a vector from a list tag containing ideally three values: the
     * X, Y, and Z components.
     *
     * <p>For values that are unavailable, their values will be 0.</p>
     *
     * @param listTag the list tag
     * @return a vector
     */
    public static Vector3D toVector(final ListTag listTag) {
        checkNotNull(listTag);
        return new Vector3D(listTag.asDouble(0), listTag.asDouble(1), listTag.asDouble(2));
    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items    The parent tag map
     * @param key      The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws IllegalArgumentException if the tag does not exist or the tag is not of the
     *                                  expected type
     */
    public static <T extends Tag> T getChildTag(final Map<String, Tag> items,
                                                 final String key,
                                                 final Class<T> expected) {
        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
        }

        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }

        return expected.cast(tag);
    }
}