/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.collect;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.function.Predicate;

/**
 * A sub class of {@link ArrayList<E>} that supports
 * additional useful methods and {@code JSON} integration.
 *
 * @param <E> the type parameter
 * @see ArrayList<E>
 */
public class EnhancedList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 3550244475910399738L;
    private final static Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .create();

    /**
     * Instantiates a new Enhanced list.
     */
    public EnhancedList() {}

    /**
     * Instantiates a new Enhanced list.
     *
     * @param initialCapacity the initial capacity
     */
    public EnhancedList(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Instantiates a new Enhanced list.
     *
     * @param elements the elements
     */
    @SafeVarargs
    public EnhancedList(final E... elements) {
        this(Arrays.asList(elements));
    }

    /**
     * Instantiates a new Enhanced list.
     *
     * @param c the collection to inherit elements from
     */
    public EnhancedList(final Collection<? extends E> c) {
        super(c);
    }

    /**
     * Adds an element provided {@param check} is {@code true}
     *
     * @param check   check to see if element needs to be added
     * @param element the element to add
     * @return whether or not the element was added
     * @see #add(Object)
     */
    public final boolean addIf(final Predicate<E> check,
                               final E element) {
        return addIf(check.test(element), element);
    }

    /**
     * Adds an element provided {@param check} is {@code true}
     *
     * @param check   check to see if element needs to be added
     * @param element the element to add
     * @return whether or not the element was added
     * @see #add(Object)
     */
    public final boolean addIf(final boolean check,
                               final E element) {
        return check && add(element);
    }

    /**
     * Removes an element provided {@param check} is {@code true}
     *
     * @param check   check to see if element needs to be removed
     * @param element the element to remove
     * @return whether or not the element was removed
     * @see #remove(Object)
     */
    public final boolean removeIf(final Predicate<E> check,
                                  final E element) {
        return removeIf(check.test(element), element);
    }

    /**
     * Removes an element provided {@param check} is {@code true}
     *
     * @param check   check to see if element needs to be removed
     * @param element the element to remove
     * @return whether or not the element was removed
     * @see #remove(Object)
     */
    public final boolean removeIf(final boolean check,
                                  final E element) {
        return check && remove(element);
    }

    /**
     * Gets a random element from this list.
     *
     * <p>If the size is {@code 0}, {@code null}
     * is returned.</p>
     *
     * @return a random element from list
     */
    public final E getRandomElement() {
        return size() == 0 ? null : get(size() == 1 ? 0 : new Random().nextInt(size()));
    }

    /**
     * Gets a random element from this list excluding
     * {@param elements}.
     *
     * <p>If the size is {@code 0}, {@code null}
     * is returned.</p>
     *
     * @return a random element from list
     */
    @SafeVarargs
    public final E getRandomElementExcluding(final E... elements) {
        // Create a copy of this list with the specified elements removed
        EnhancedList<E> list = new EnhancedList<>(this);
        list.removeAll(Arrays.asList(elements));
        return list.getRandomElement();
    }

    /**
     * @return a copy of the current list that is immutable
     */
    public final ImmutableList<E> getImmutableElements() {
        return ImmutableList.copyOf(this);
    }

    /**
     * Converts the list to a readable {@code String}.
     *
     * <p>Each element in the current instance will be
     * appended one after another followed by {@code ,}</p>
     *
     * <p>Each element in the readable list will be converted
     * to their {@code toString()} value.</p>
     *
     * @return readable string
     * @see #toReadableList(String, boolean)
     */
    public String toReadableList() {
        // use spaces as a separator by default
        return toReadableList(", ");
    }

    /**
     * Converts the list to a readable {@code String}.
     *
     * <p>Each element in the current instance will be
     * appended one after another followed by {@param
     * separator}.</p>
     *
     * <p>Each element in the readable list will be converted
     * to their {@code toString()} value.</p>
     *
     * @param separator    the separator to separate items
     * @return readable string
     * @see #toReadableList(String, boolean)
     */
    public String toReadableList(final String separator) {
        // do not append "and" instead of the last separator by default
        return toReadableList(separator, false);
    }

    /**
     * Converts the list to a readable {@code String}.
     *
     * <p>Each element in the current instance will be
     * appended one after another followed by {@param
     * separator}.</p>
     *
     * <p>If {@param concatenator} is {@code true}, then
     * an {@code and} will be appended instead of the last
     * {@param separator}</p>
     *
     * <p>Each element in the readable list will be converted
     * to their {@code toString()} value.</p>
     *
     * <p>For example, this is how a readable list will look
     * with {@code separator} being {@code , } and {@code
     * concaternator} set to {@code true}:
     * <pre>
     * <code>
     * EnhancedList<String> shoppingList = new EnhancedList<>(5);
     * shoppingList.add("milk");
     * shoppingList.add("eggs");
     * shoppingList.add("bread");
     * shoppingList.add("juice");
     * shoppingList.add("cake");
     *
     * System.out.println(shoppingList.toReadableList(", ", true));
     * </code>
     * </pre>
     * Output:
     * <pre>
     * <code>
     * milk, eggs, bread, juice and cake
     * </code>
     * </pre>
     * </p>
     *
     * @param separator    the separator to separate items
     * @param concaterator whether or not to append the {@code and}
     * @return readable string
     */
    public String toReadableList(final String separator,
                                 final boolean concaterator) {
        Validate.notNull(separator);

        StringBuilder builder = new StringBuilder();

        int len = size();
        for (int x = 0; x < len; ++x) {
            builder.append(get(x).toString());

            // if concaterator == true, append the word "and" instead
            // of the last separator to make the list more readable.
            // For example, having a list such as [String1,String2,String3]
            // will look like this with separator ", " and concaterator
            // disabled: "String1, String2, String3" and enabled:
            // "String1, String2 and String3".
            if (concaterator) {
                if (x <= len - 3) builder.append(separator);
                if (x == len - 2) builder.append(" and ");
            } else {
                if (x != len - 1) builder.append(separator);
            }
        }

        return builder.toString();
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

    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof EnhancedList))
            return false;

        List<?> l = (List<?>) other;

        for (int x = 0; x < size(); ++x) {
            if (!get(x).equals(l.get(x))) return false;
        }

        return true;
    }

    /**
     * Construct a list of elements provided by
     * the {@code json string}.
     *
     * @param <T>  the type parameter
     * @param json the json
     * @return the enhanced list
     */
    public static <T> EnhancedList<T> fromJson(final String json) {
        return fromJson(gson, json);
    }

    /**
     * Construct a list of elements provided by
     * the {@code json string}.
     *
     * @param <T>  the type parameter
     * @param gson the gson
     * @param json the json
     * @return the enhanced list
     */
    public static <T> EnhancedList<T> fromJson(final Gson gson,
                                               final String json) {
        return (gson == null ? EnhancedList.gson : gson).fromJson(
                Validate.notNull(json),
                new TypeToken<EnhancedList<T>>() {}.getType()
        );
    }
}