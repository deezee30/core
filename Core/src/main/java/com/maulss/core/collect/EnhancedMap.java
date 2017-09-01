/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.collect;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.function.BiPredicate;

/**
 * A sub class of {@link LinkedHashMap} that supports
 * additional useful methods and {@code JSON} integration.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @see LinkedHashMap
 */
public class EnhancedMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = -2780608617302194763L;
    private final static Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .create();

    /**
     * Instantiates a new Enhanced map.
     */
    public EnhancedMap() {}

    /**
     * Instantiates a new Enhanced map.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     */
    public EnhancedMap(final int initialCapacity,
                       final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Instantiates a new Enhanced map.
     *
     * @param initialCapacity the initial capacity
     */
    public EnhancedMap(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Instantiates a new Enhanced map.
     *
     * @param m the map to inherit the entries from
     */
    public EnhancedMap(final Map<? extends K, ? extends V> m) {
        super(m);
    }

    /**
     * Instantiates a new Enhanced map.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @param accessOrder     the access order
     */
    public EnhancedMap(final int initialCapacity,
                       final float loadFactor,
                       final boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    /**
     * Adds an element provided {@param check} is {@code true}
     *
     * @param check check to see if element needs to be added
     * @param key   the key of the entry to add
     * @param value the corresponding value to add to the key
     * @return      whether or not the element was added
     * @see         #put(Object, Object)
     */
    public final boolean putIf(final BiPredicate<K, V> check,
                               final K key,
                               final V value) {
        return putIf(check.test(key, value), key, value);
    }

    /**
     * Adds an element provided {@param check} is {@code true}
     *
     * @param check check to see if element needs to be added
     * @param key   the key of the entry to add
     * @param value the corresponding value to add to the key
     * @return      whether or not the element was added
     * @see         #put(Object, Object)
     */
    public final boolean putIf(final boolean check,
                               final K key,
                               final V value) {
        if (!check) return false;
        put(key, value);
        return true;
    }

    /**
     * Removes an element provided {@param check} is {@code true}
     *
     * @param check check to see if element needs to be removed
     * @param key   the key of the entry to remove
     * @param value the corresponding value to remove to the key
     * @return      whether or not the element was removed
     * @see         #remove(Object)
     */
    public final boolean removeIf(final BiPredicate<K, V> check,
                                  final K key,
                                  final V value) {
        return removeIf(check.test(key, value), key);
    }

    /**
     * Removes an element provided {@param check} is {@code true}
     *
     * @param check check to see if element needs to be removed
     * @param key   the key of the entry to remove
     * @return      whether or not the element was removed
     * @see         #remove(Object)
     */
    public final boolean removeIf(final boolean check,
                                  final K key) {
        if (!check) return false;
        remove(key);
        return true;
    }

    /**
     * Gets a random entry from this map.
     *
     * <p>If the size is {@code 0}, {@code null}
     * is returned.</p>
     *
     * @return a random entry from map
     */
    public final Map.Entry<K, V> getRandomEntry() {
        return new EnhancedList<>(entrySet()).getRandomElement();
    }

    /**
     * Gets a random entry from this map, exluding
     * {@param entries}.
     *
     * <p>If the size is {@code 0}, {@code null}
     * is returned.</p>
     *
     * @return a random entry from map
     */
    @SafeVarargs
    public final Map.Entry<K, V> getRandomEntryExcluding(final Map.Entry<K, V>... entries) {
        // Create a copy of this map with the specified entries removed
        return new EnhancedList<>(entrySet()).getRandomElementExcluding(entries);
    }

    /**
     * @return a copy of the current map that is immutable
     */
    public final ImmutableMap<K, V> getImmutableEntries() {
        return ImmutableMap.copyOf(this);
    }

    /**
     * Revert the map so that the head is the tail and
     * the tail is the head.
     *
     * @return the reversed map
     */
    public EnhancedMap<K, V> revert() {
        int x = size();

        // Create a new HashMap with entries backward to this instance
        HashMap<K, V> tempMap = new HashMap<>(x);
        for (; x > 0; --x) {
            Map.Entry<K, V> entry = getEntry(x - 1).get();
            tempMap.put(entry.getKey(), entry.getValue());
        }

        // Clear the current instance of the map
        clear();
        // Put the reverted entries from the HashMap back into the current instance
        putAll(tempMap);
        return this;
    }

    /**
     * Gets entry based on index.
     *
     * @param position the position
     * @return the entry
     */
    public Optional<Map.Entry<K, V>> getEntry(int position) {
        position = Math.abs(position);

        Set<Map.Entry<K, V>> entries = entrySet();

        int j = 0;
        for (Map.Entry<K, V> entry : entries)
            if (j++ == position) return Optional.of(entry);

        return Optional.empty();
    }

    /**
     * Gets index based on key.
     *
     * @param key the key
     * @return the index
     */
    public int getIndex(final K key) {
        Validate.notNull(key);

        int x = 0;
        for (Map.Entry<K, V> entry : entrySet()) {
            // For maps such that (K == V) we check for equality in entry.getValue() too
            if (entry.getKey().equals(key) || entry.getValue().equals(key)) return x;
            ++x;
        }

        throw null;
    }

    /**
     * Gets local {@link Gson} instance to use for
     * json serialization and deserialization
     *
     * @return gson
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Serializes all elements in this class to Json
     *
     * @return the string
     */
    public String toJson() {
        return getGson().toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }

    /**
     * Construct a map of entries provided by
     * the {@code json string}.
     *
     * @param <K>  the type key parameter
     * @param <V>  the type value parameter
     * @param json the json
     * @return the enhanced map
     */
    public static <K, V> EnhancedMap<K, V> fromJson(final String json) {
        return fromJson(gson, json);
    }

    /**
     * Construct a map of entries provided by
     * the {@code json string}.
     *
     * @param <K>  the type key parameter
     * @param <V>  the type value parameter
     * @param gson the gson
     * @param json the json
     * @return the enhanced map
     */
    public static <K, V> EnhancedMap<K, V> fromJson(final Gson gson,
                                                    final String json) {
        return (gson == null ? EnhancedMap.gson : gson).fromJson(
                Validate.notNull(json),
                new TypeToken<EnhancedMap<K, V>>() {}.getType()
        );
    }

    /**
     * Sort map based on either the key or value and
     * either in ascending or descending order.
     *
     * @param <M>  the type map parameter
     * @param <K>  the type key parameter
     * @param <V>  the type value parameter
     * @param map  the map to sort
     * @param type the type of sorting
     * @return the sorted map
     * @see SortType
     */
    public static <M extends Map<K, V>,
            K extends Comparable<? super K>,
            V extends Comparable<? super V>> M sort(final M map,
                                                    final SortType type) {
        Validate.notNull(map);
        Validate.notNull(type);

        LinkedList<Map.Entry<K, V>> entries = new LinkedList<>(map.entrySet());
        entries.sort((o1, o2) -> type.isKey() ?
                o1.getKey().compareTo(o2.getKey()) :
                o1.getValue().compareTo(o2.getValue()));

        EnhancedMap<K, V> tempMap = new EnhancedMap<>(map.size());
        for (Map.Entry<K, V> entry : entries) {
            tempMap.put(entry.getKey(), entry.getValue());
        }

        map.clear();
        map.putAll(type.isAscending() ? tempMap.revert() : tempMap);
        return map;
    }

    /**
     * The enum Sort type.
     */
    public enum SortType {

        /**
         * Represents the sorting type where the Key is ascending.
         */
        ASC_KEY(true,  true),

        /**
         * Represents the sorting type where the Value is ascending.
         */
        ASC_VAL(true,  false),

        /**
         * Represents the sorting type where the Key is descending.
         */
        DES_KEY(false, true),

        /**
         * Represents the sorting type where the Value is descending.
         */
        DES_VAL(false, false);

        private final boolean ascending, key;

        /**
         * Instantiates a new Sort type.
         *
         * @param ascending the ascending
         * @param key       the key
         */
        SortType(final boolean ascending,
                 final boolean key) {
            this.ascending = ascending;
            this.key = key;
        }

        /**
         * Is ascending boolean.
         *
         * @return the boolean
         */
        public boolean isAscending() {
            return ascending;
        }

        /**
         * Is key boolean.
         *
         * @return the boolean
         */
        public boolean isKey() {
            return key;
        }
    }
}