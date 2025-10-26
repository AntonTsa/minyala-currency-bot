package org.project.ninjas.minyala.currency.bot.bot.util;

/**
 * Bot state identifiers and callback labels.
 */
public final class BotStateLabelConstants {

    /** Start state. */
    public static final String START = "START";

    /** Main menu state. */
    public static final String MAIN_MENU = "MAIN_MENU";

    /** Settings menu state. */
    public static final String SETTINGS_MENU = "SETTINGS_MENU";

    /** Current info view. */
    public static final String CURRENT_INFO = "CURRENT_INFO";

    /** Decimal precision choice. */
    public static final String DECIMAL_CHOICE = "DECIMAL_CHOICE";

    /** Bank choice. */
    public static final String BANK_CHOICE = "BANK_CHOICE";

    /** Currency choice. */
    public static final String CURRENCY_CHOICE = "CURRENCY_CHOICE";

    /** Notification time choice. */
    public static final String NOTIFY_CHOICE = "NOTIFY_CHOICE";

    /** Go back one level. */
    public static final String BACK = "BACK";

    /** Go back to main menu. */
    public static final String BACK_ALL = "BACKALL";

    private BotStateLabelConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}
