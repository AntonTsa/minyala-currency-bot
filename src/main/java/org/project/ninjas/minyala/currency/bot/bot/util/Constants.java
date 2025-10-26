package org.project.ninjas.minyala.currency.bot.bot.util;

import lombok.Data;
import lombok.Getter;

/**
 * Class contains constants used in the bot.
 */
@Data
public class Constants {
    private Constants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Enumeration for decimal options.
     */
    @Getter
    public enum Decimal {
        /**
         * One decimal place.
         */
        ONE("1"),
        /**
         * Two decimal places.
         */
        TWO("2"),
        /**
         * Three decimal places.
         */
        THREE("3");

        private final String displayName;

        Decimal(String displayName) {
            this.displayName = displayName;
        }

    }

    /**
     * Enumeration for bank options.
     */
    @Getter
    public enum Banks {
        /** PrivatBank. */
        PRIVAT("ПриватБанк"),
        /** MonoBank. */
        MONO("МоноБанк"),
        /** National Bank of Ukraine. */
        NBU("НБУ");

        private final String displayName;

        Banks(String displayName) {
            this.displayName = displayName;
        }

    }
}
