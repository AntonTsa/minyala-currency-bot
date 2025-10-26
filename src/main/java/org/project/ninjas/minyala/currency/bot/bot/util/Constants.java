package org.project.ninjas.minyala.currency.bot.bot.util;

import lombok.Data;

/***/
@Data
public class Constants {
    private Constants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /***/
    public static enum Decimal {
        /***/
        ONE("1"),
        /***/
        TWO("2"),
        /***/
        THREE("3");

        private final String displayName;

        Decimal(String displayName) {
            this.displayName = displayName;
        }

        /**
         * @return displayName.
         * */
        public String getDisplayName() {
            return displayName;
        }
    }

    /***/
    public static enum Banks {
        /***/
        PRIVAT("ПриватБанк"),
        /***/
        MONO("МоноБанк"),
        /***/
        NBU("НБУ");

        private final String displayName;

        Banks(String displayName) {
            this.displayName = displayName;
        }

        /**
         * @return displayName.
         * */
        public String getDisplayName() {
            return displayName;
        }
    }
}
