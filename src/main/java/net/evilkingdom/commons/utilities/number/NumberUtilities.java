package net.evilkingdom.commons.utilities.number;

/*
 * This file is part of Commons (Server), licensed under the MIT License.
 *
 *  Copyright (c) kodirati (kodirati.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

import net.evilkingdom.commons.utilities.number.enums.NumberFormatType;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class NumberUtilities {

    /**
     * Allows you to check if a string is an integer.
     *
     * @param string ~ The string that needs to be checked.
     * @return If checked string is an integer.
     */
    public static boolean isInteger(final String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Allows you to check if a string is a double.
     *
     * @param string ~ The string that needs to be checked.
     * @return If checked string is an double.
     */
    public static boolean isDouble(final String string) {
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Allows you to check if a string is a long.
     *
     * @param string ~ The string that needs to be checked.
     * @return If checked string is a long.
     */
    public static boolean isLong(final String string) {
        try {
            Long.parseLong(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Allows you to format a number.
     *
     * @param number ~ The number to be formatted.
     * @param numberFormatType ~ The number format type the number should use.
     * @return The formatted number.
     */
    public static String format(final double number, final NumberFormatType numberFormatType) {
        final NumberFormat numberFormat = NumberFormat.getInstance();
        if (numberFormatType == NumberFormatType.MULTIPLIER) {
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(0);
            return numberFormat.format(number);
        } else if (numberFormatType == NumberFormatType.COMMAS) {
            numberFormat.setGroupingUsed(true);
            return numberFormat.format(number);
        } else if (numberFormatType == NumberFormatType.LETTERS) {
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(0);
            if (number < 1000.0) {
                return numberFormat.format(number);
            } else if (number < 1000000.0) {
                return numberFormat.format((number / 1000.0)) + "k";
            } else if (number < 1.0E9) {
                return numberFormat.format((number / 1000000.0)) + "m";
            } else if (number < 1.0E12) {
                return numberFormat.format((number / 1.0E9)) + "b";
            } else if (number < 1.0E15) {
                return numberFormat.format((number / 1.0E12)) + "t";
            } else if (number < 1.0E18) {
                return numberFormat.format((number / 1.0E15)) + "q";
            } else if (number < 1.0E21) {
                return numberFormat.format((number / 1.0E18)) + "qt";
            }
        }
        return String.valueOf(number);
    }

    /**
     * Allows you to check if a chance is selected.
     * This should be used highly for chance based systems like crates, enchants, etc.
     *
     * @param chance ~ The chance you want.
     * @return If the chance was selected.
     */
    public static boolean chanceOf(final double chance) {
        return new Random().nextInt(100000) == (chance * 1000);
    }

}
