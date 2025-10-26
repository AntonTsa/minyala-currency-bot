package org.project.ninjas.minyala.currency.bot.bot.util;

import lombok.Data;
import lombok.Getter;

/**
 * This class contains constant values used throughout the application.
 */
@Data
public class Constants {
    private Constants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Enum representing decimal values as strings.
     */
    @Getter
    public enum Decimal {
        /** Decimal one. */
        ONE("1"),
        /** Decimal two. */
        TWO("2"),
        /** Decimal three. */
        THREE("3");

        private final String displayName;

        Decimal(String displayName) {
            this.displayName = displayName;
        }

    }

    /**
     * Enum representing different banks.
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
