/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A class containing static utility methods and caches which are intended as reflective conveniences.
 * Unless otherwise noted, upon failure methods will return {@code null}.
 */
public final class Reflection {

	/**
	 * Contains loaded fields in a cache.
	 */
	private static final Map<Class<?>, Map<String, Field>> loadedFields = new HashMap<>();

	/**
	 * Contains loaded methods in a cache.
	 *
	 * The map maps [types to maps of [method names to maps of [parameter types to method instances]]].
	 */
	private static final Map<Class<?>, Map<String, Map<AtomicReference<Class<?>[]>, Method>>> loadedMethods = new HashMap<>();

	/* Disable initialization */
	private Reflection() {}

	/**
	 * Creates a new instance of a class provided that a constructor
	 * has been found that accepts the arguments provided in {@param args}.
	 *
	 * If the class provided is null or a fitting constructor wasn't found,
	 * {@code null} is returned.  Otherwise the newly created instance is returned.
	 *
	 * @param   c     Class to create the instance of
	 * @param   args  The constructor arguments.
	 *
	 * @return  Instance of the class or {@code null} if unable to create the object
	 */
	public static Object newInstance(Class<?> c, Object... args) {
		if (c == null) return null;
		try {
			for (Constructor<?> constructor : c.getDeclaredConstructors()) {
				if (constructor.getGenericParameterTypes().length == args.length) {
					return constructor.newInstance(args);
				}
			}
		} catch (Exception ignored) { /* Constructor wasn't found, and hence wasn't called */ }
		return null;
	}

	/**
	 * Attempts to get the NMS handle of a CraftBukkit object.
	 *
	 * <p>The only match currently attempted by this method is a retrieval by using
	 * a parameterless {@code getHandle()} method implemented by the runtime type of
	 * the specified object.
	 *
	 * @param   obj The object for which to retrieve an NMS handle
	 *
	 * @return  The NMS handle of the specified object, or {@code null} if it could
	 *          not be retrieved using {@code getHandle()}
	 */
	public synchronized static Object getHandle(Object obj) {
		try {
			return getMethod(obj.getClass(), "getHandle").invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves a {@link Field} instance declared by the specified
	 * class with the specified name.  Java access modifiers are ignored during this
	 * retrieval.  No guarantee is made as to whether the field returned will be an
	 * instance or static field.
	 *
	 * <p>A global caching mechanism within this class is used to store fields.  Combined
	 * with synchronization, this guarantees that no field will be reflectively looked up
	 * twice.
	 *
	 * <p>If a field is deemed suitable for return,
	 * {@link Field#setAccessible(boolean)} will be invoked with an argument
	 * of {@code true} before it is returned.  This ensures that callers do not have to check
	 * or worry about Java access modifiers when dealing with the returned instance.
	 *
	 * @param   clazz  The class which contains the field to retrieve
	 * @param   name   The declared name of the field in the class
	 *
	 * @return  A field object with the specified name declared by the specified class
	 * @see     Class#getDeclaredField(String)
	 */
	public synchronized static Field getField(Class<?> clazz,
											  String name) {
		Map<String, Field> loaded;
		if (!loadedFields.containsKey(clazz)) {
			loaded = new HashMap<>();
			loadedFields.put(clazz, loaded);
		} else {
			loaded = loadedFields.get(clazz);
		}
		if (loaded.containsKey(name)) {
			// If the field is loaded (or cached as not existing), return the relevant value, which might be null
			return loaded.get(name);
		}
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			loaded.put(name, field);
			return field;
		} catch (Exception e) {
			// Error loading
			e.printStackTrace();
			// Cache field as not existing
			loaded.put(name, null);
			return null;
		}
	}

	/**
	 * Retrieves a {@link Method} instance declared by the specified class
	 * with the specified name and argument types.  Java access modifiers are ignored during
	 * this retrieval. No guarantee is made as to whether the field  returned will be an
	 * instance or static field.
	 *
	 * <p>A global caching mechanism within this class is used to store method.  Combined
	 * with synchronization, this guarantees that no method will be reflectively looked up
	 * twice.
	 *
	 * <p>If a method is deemed suitable for return,
	 * {@link Method#setAccessible(boolean)} will be invoked with an argument
	 * of {@code true} before it is returned.  This ensures that callers do not have to check
	 * or worry about Java access modifiers when dealing with the returned instance.
	 *
	 * <p>This method does <em>not</em> search superclasses of the specified type for methods
	 * with the specified signature.  Callers wishing this behavior should use {@link
	 * Class#getDeclaredMethod(String, Class...)}.
	 *
	 * @param   clazz The class which contains the method to retrieve
	 * @param   name  The declared name of the method in the class
	 * @param   args  The formal argument types of the method
	 *
	 * @return  A method object with the specified name declared by the specified class
	 */
	public synchronized static Method getMethod(Class<?> clazz,
												String name,
												Class<?>... args) {
		if (!loadedMethods.containsKey(clazz)) {
			loadedMethods.put(clazz, new HashMap<>());
		}

		Map<String, Map<AtomicReference<Class<?>[]>, Method>> loadedMethodNames = loadedMethods.get(clazz);
		if (!loadedMethodNames.containsKey(name)) {
			loadedMethodNames.put(name, new HashMap<>());
		}

		Map<AtomicReference<Class<?>[]>, Method> loadedSignatures = loadedMethodNames.get(name);
		AtomicReference<Class<?>[]> wrappedArg = new AtomicReference<>(args);
		if (loadedSignatures.containsKey(wrappedArg)) {
			return loadedSignatures.get(wrappedArg);
		}

		for (Method m : clazz.getMethods())
			if (m.getName().equals(name) && Arrays.equals(args, m.getParameterTypes())) {
				m.setAccessible(true);
				loadedSignatures.put(wrappedArg, m);
				return m;
			}
		loadedSignatures.put(wrappedArg, null);
		return null;
	}
}