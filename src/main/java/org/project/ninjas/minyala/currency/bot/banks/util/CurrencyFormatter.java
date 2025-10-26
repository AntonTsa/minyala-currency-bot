package org.project.ninjas.minyala.currency.bot.banks.util;

/**
 * Utility class for formatting currency values with custom precision.
 */
public final class CurrencyFormatter {

    private CurrencyFormatter() {
        // Utility class should not be instantiated.
    }

    /**
     * Formats a numeric value with a given number of decimal digits.
     *
     * @param value  the numeric value to format
     * @param digits number of digits after the decimal point
     * @return formatted string representation
     */
    public static String format(double value, int digits) {
        //noinspection MalformedFormatString
        return String.format("%." + digits + "f", value);
    }
}
