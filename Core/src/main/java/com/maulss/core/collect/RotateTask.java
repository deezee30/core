/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.collect;

/**
 * The interface Rotate task.
 *
 * <p>Goes together with {@link ElementQueue<E>}</p>
 *
 * @param <E> the type parameter
 */
@FunctionalInterface
public interface RotateTask<E> {

    /**
     * Gets called whenever an {@link ElementQueue<E>}
     * rotates.
     *
     * @param element the element
     */
    void onRotate(final E element);
}