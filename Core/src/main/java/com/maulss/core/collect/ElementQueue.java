/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.collect;

import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The type Element queue.
 *
 * @param <E> the type parameter
 */
public class ElementQueue<E> extends EnhancedList<E> {

    private static final long serialVersionUID = 5216613009142713027L;

    private RotateTask<E> onRotate = element -> {};

    /**
     * Instantiates a new Element queue.
     */
    public ElementQueue() {}

    /**
     * Instantiates a new Element queue.
     *
     * @param initialCapacity the initial capacity
     */
    public ElementQueue(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Instantiates a new Element queue.
     *
     * @param elements the elements
     */
    @SafeVarargs
    public ElementQueue(final E... elements) {
        super(elements);
    }

    /**
     * Instantiates a new Element queue.
     *
     * @param c the collection to inherit elements from
     */
    public ElementQueue(final Collection<E> c) {
        super(c);
    }

    /**
     * Rotates the queue by 1 position
     * @see #rotate(int)
     */
    public void rotate() {
        rotate(1);
    }

    /**
     * Rotates the queue by {@param distance} positions
     *
     * @param distance the distance
     * @see Collections#rotate(List, int)
     */
    public void rotate(final int distance) {
        if (distance >= size()) return;

        E first = get(0);
        Collections.rotate(this, distance);
        onRotate.onRotate(first);
    }

    /**
     * Call a {@link RotateTask} whenever the
     * queue rotates
     *
     * @param onRotate the on rotate
     */
    public final void onRotate(final RotateTask<E> onRotate) {
        this.onRotate = Validate.notNull(onRotate);
    }
}