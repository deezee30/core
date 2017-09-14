/*
 * Part of core.
 * 
 * Created on 21 June 2017 at 2:19 PM.
 */

package com.maulss.core.math;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maulss.core.collect.EnhancedList;

import java.util.Collection;

public class Vector3DList extends EnhancedList<Vector3D> {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public Vector3DList() {}

    public Vector3DList(final int initialCapacity) {
        super(initialCapacity);
    }

    public Vector3DList(final Vector3D... elements) {
        super(elements);
    }

    public Vector3DList(final Collection<Vector3D> c) {
        super(c);
    }

    @Override
    public Gson getGson() {
        return gson;
    }

    public static Vector3DList fromJson(final String json) {
        return gson.fromJson(json, Vector3DList.class);
    }
}