package com.maulss.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * A utility class used for management with {@link UUID}s,
 * specifically converting Strings to UUIDs and backwards.
 *
 * @see UUID
 */
public final class UUIDUtil {

	/* Disable instantiation */
	private UUIDUtil() {}

	/**
	 * Gets the String with length {@code 32} version of
	 * a {@link UUID} with all dashes removed.
	 *
	 * @param   id The {@link UUID} to parse
	 *
	 * @return  The String version of {@param id}.
	 * @see     UUID
	 */
	public static synchronized String fromUuid(final UUID id) {
		return fromUuid(String.valueOf(id));
	}

	/**
	 * Gets the String with length {@code 32} version of a
	 * {@link UUID#toString()} with all dashes removed.
	 *
	 * @param   id The {@link UUID#toString()} to parse
	 *
	 * @return  The String version of {@param id}.
	 */
	public static synchronized String fromUuid(final String id) {
		return id.replaceAll("-", "");
	}

	/**
	 * Gets the {@link UUID#fromString(String)}
	 * from a String by adding in dashes at appropriate
	 * places and creating a new UUID instance.
	 *
	 * @param   id The UUID in String form, without the dashes
	 *
	 * @return  A new instance of {@link UUID}.
	 * @see     UUID#fromString(String)
	 */
	public static synchronized UUID fromString(final String id) {
		return UUID.fromString(id.length() == 32 ?
				id.substring(0, 8) + "-" +
				id.substring(8, 12) + "-" +
				id.substring(12, 16) + "-" +
				id.substring(16, 20) + "-" +
				id.substring(20, 32) : id
		);
	}

	/**
	 * Checks whether the String provided is fit to be an
	 * actual {@link UUID}.
	 *
	 * @param   str The String to check
	 *
	 * @return  True if it meets the requirements of a UUID.
	 * @see     UUID
	 */
	public static synchronized boolean isUuid(final String str) {

		// Check if str is natively in the form of a real UUID
		try {
			// noinspection ResultOfMethodCallIgnored
			UUID.fromString(str);
			return true;
		} catch (IllegalArgumentException ignored) {
			if (str.length() != 32) {
				return false;
			}

			// Also check if str can qualify for a real UUID provided the dashes are taken out
			if (StringUtils.countMatches(str, "-") != 4) {
				try {
					fromString(str);
					return true;
				} catch (IllegalArgumentException ignored1) {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	/**
	 * Checks whether {@param obj} is fit for properties of a {@link UUID}.
	 * If so, parses UUID to a String adding all dashes to make a 36-length String.
	 *
	 * @param   obj The object to check for
	 *
	 * @return  The parsed String to be used in the database.
	 * @see     #isUuid(String)
	 * @see     #fromString(String)
	 */
	public static synchronized String checkForValidUuid(final Object obj) {
		String str = String.valueOf(obj);
		return isUuid(str) ? str.length() == 32 ? fromString(str).toString() : str : str;
	}
}