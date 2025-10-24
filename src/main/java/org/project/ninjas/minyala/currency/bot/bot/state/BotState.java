package org.project.ninjas.minyala.currency.bot.bot.state;

/**
 * Stages of working with bot.
 */
public enum BotState {
    /**
     * Initial stage, user press button "/start".
     */
    HANDLE_START,
    /**
     * Main Menu handling stage.
     */
    HANDLE_MAIN_MENU,
    /**
     * Settings menu handling stage.
     */
    HANDLE_SETTINGS,
    /**
     * Decimal options handling stage.
     */
    HANDLE_DECIMAL_CHOICE,
    /**
     * Bank options handling stage.
     */
    BANK_CHOICE,
    /**
     * Currency options handling stage.
     */
    CURRENCY_CHOICE,
    /**
     * Notify time options handling stage.
     */
    NOTIFY_CHOICE,

    GET_INFO
}
