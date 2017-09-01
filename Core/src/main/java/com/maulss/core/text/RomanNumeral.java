/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.text;

import com.google.common.annotations.Beta;

/**
 * An object of type RomanNumeral is an integer between 1 and 3999.  It can be constructed
 * either from an integer or from a string that represents a Roman numeral in this range.
 * The function {@link #toString()} will return a standardized Roman numeral representation
 * of the number. The function {@link #toInt()} will return the number as a value of type int.
 */
@Beta
public final class RomanNumeral {

	private final int num;

	private static final int[] numbers = {
			1000, 900, 500, 400,
			100, 90, 50, 40, 10,
			9, 5, 4, 1
	};

	private static final String[] letters = {
			"M", "CM", "D", "CD", "C",
			"XC", "L", "XL", "X",
			"IX", "V", "IV", "I"
	};

	/**
	 * Creates the Roman number with the int value specified by the parameter.
	 *
	 * @throws  NumberFormatException
	 *          If arabic is not in the range 1 to 3999 inclusive.
	 */
	public RomanNumeral(int arabic) {
		if (arabic < 1) throw new NumberFormatException("Value of RomanNumeral must be positive.");
		if (arabic > 3999) throw new NumberFormatException("Value of RomanNumeral must be 3999 or less.");
		num = arabic;
	}


	/**
	 * Creates the Roman number with the given representation. For example,
	 * {@code RomanNumeral("xvii")} is {@code 17}.  If the parameter is not a legal Roman numeral,
	 * a {@link NumberFormatException} is thrown.  Both upper and lower case letters are allowed.
	 *
	 * @throws  NumberFormatException
	 *          If the parameter is not a legal Roman numeral.
	 */
	public RomanNumeral(String roman) {
		if (roman.length() == 0)
			throw new NumberFormatException("An empty string does not define a Roman numeral.");

		roman = roman.toUpperCase();

		int i = 0;
		int arabic = 0;

		while (i < roman.length()) {

			char letter = roman.charAt(i);
			int number = letterToNumber(letter);

			++i;

			if (i == roman.length()) {
				arabic += number;
			} else {
				int nextNumber = letterToNumber(roman.charAt(i));
				if (nextNumber > number) {
					arabic += nextNumber - number;
					++i;
				} else {
					arabic += number;
				}
			}

		}

		if (arabic > 3999)
			throw new NumberFormatException("Roman numeral must have value 3999 or less.");

		num = arabic;
	}

	/**
	 * Find the integer value of letter considered as a Roman numeral.
	 * The letter must be upper case.
	 *
	 * @throws  NumberFormatException
	 *          If letter is not a legal Roman numeral.
	 */
	private int letterToNumber(char letter) {
		switch (letter) {
			case 'I':
				return 1;
			case 'V':
				return 5;
			case 'X':
				return 10;
			case 'L':
				return 50;
			case 'C':
				return 100;
			case 'D':
				return 500;
			case 'M':
				return 1000;
			default:
				throw new NumberFormatException("Illegal character \"" + letter + "\" in Roman numeral");
		}
	}

	/**
	 * Return the standard representation of this Roman numeral.
	 */
	@Override
	public String toString() {
		StringBuilder roman = new StringBuilder(numbers.length);
		int N = num;
		for (int i = 0; i < numbers.length; ++i) {
			while (N >= numbers[i]) {
				roman.append(letters[i]);
				N -= numbers[i];
			}
		}
		return roman.toString();
	}


	/**
	 * Return the value of this Roman numeral as an int.
	 */
	public int toInt() {
		return num;
	}
}