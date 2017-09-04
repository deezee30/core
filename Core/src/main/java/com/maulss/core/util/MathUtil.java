/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.util;

import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;
import java.util.Random;

public class MathUtil {

    private static final Random RANDOM = new Random();

    /**
     * Round a double to the nearest whole number
     *
     * @param	d The double to round
     * @return  The rounded integer
     * @see		#round(double, int)
     */
    public static int round(double d) {
        return floor(d + 0.5d);
    }

    /**
     * Round a double to the nearest whole number
     *
     * @param   f The float to round
     * @return	The rounded integer
     * @see     #round(double, int)
     */
    public static int round(float f) {
        return floor(f + 0.5f);
    }

    /**
     * Round a double to the nearest {@param places} amount of places.
     *
     * @param   d       The double to round
     * @param   places  The amount of places after the decimal point
     * @return  The rounded integer
     * @see     BigDecimal
     */
    public static double round(double d, int places) {
        return new BigDecimal(d).setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * Round a float to the nearest {@param places} amount of places.
     *
     * @param   f       The float to round
     * @param   places  The amount of places after the decimal point
     * @return  The rounded integer
     * @see     BigDecimal
     */
    public static float round(float f, int places) {
        return new BigDecimal(f).setScale(places, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * Calculates the ratio of two numbers in decimal form.
     *
     * @param   value1  The first value
     * @param   value2  The second value
     * @return  Double that has been rounded up and turned into a form of a ratio between both numbers
     * @see     Math#round(double)
     */
    public static double getRatio(int value1, int value2) {
        return Math.round(
                (value1 == 0
                        ? 0D : value2 > 0
                        ? (double) value1 / (double) value2 : value1) * 100D
        ) / 100D;
    }

    /**
     * Checks if the provided Object can be formatted into an Integer.
     *
     * @param o  The Object to check whether it can be parsed into an Integer
     * @return   True if it can be parsed, false if not
     */
    public static boolean isInteger(Object o) {
        try {
            Integer.parseInt((Validate.notNull(o)).toString());
            return true;
        } catch (NumberFormatException x) {
            return false;
        }
    }

    /**
     * Returns the largest (closest to positive infinity)
     * {@code double} value that is less than or equal to the
     * argument and is equal to a mathematical integer.  Special
     * cases: <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity or
     * positive zero or negative zero, then the result is the same as
     * the argument.</ul>
     *
     * @param   num a value.
     * @return  the largest (closest to positive infinity)
     *          value that less than or equal to the argument
     *          and is equal to a mathematical integer.
     */
    public static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    /**
     * Returns the smallest (closest to negative infinity)
     * {@code double} value that is greater than or equal to the
     * argument and is equal to a mathematical integer.  Special
     * cases: <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity or
     * positive zero or negative zero, then the result is the same as
     * the argument.  <li>If the argument value is less than zero but
     * greater than -1.0, then the result is negative zero.</ul>  Note
     * that the value of {@code ceil(num)} is exactly the same
     * as the value of {@code -Math.floor(-num)}.
     *
     * @param   num a value.
     * @return  the smallest (closest to negative infinity)
     *          value that is greater than or equal to the
     *          argument and is equal to a mathematical integer.
     */
    public static int ceil(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor + (int) (~Double.doubleToRawLongBits(num) >>> 63);
    }

    public static int random(int min, int max) {
        return min + RANDOM.nextInt(max - min);
    }

    public static double average(int... nums) {
        double sum = 0d;

        for (int n : nums) {
            sum += n;
        }

        return sum / (double) nums.length;
    }

    public static int toInt(Object object) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }

        try {
            return Integer.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) {}
        return 0;
    }

    public static float toFloat(Object object) {
        if (object instanceof Number) {
            return ((Number) object).floatValue();
        }

        try {
            return Float.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) {}
        return 0;
    }

    public static double toDouble(Object object) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }

        try {
            return Double.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) {}
        return 0;
    }

    public static long toLong(Object object) {
        if (object instanceof Number) {
            return ((Number) object).longValue();
        }

        try {
            return Long.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) {}
        return 0;
    }

    public static short toShort(Object object) {
        if (object instanceof Number) {
            return ((Number) object).shortValue();
        }

        try {
            return Short.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) {}
        return 0;
    }

    public static byte toByte(Object object) {
        if (object instanceof Number) {
            return ((Number) object).byteValue();
        }

        try {
            return Byte.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) {}
        return 0;
    }
}