package org.project.ninjas.minyala.currency.bot.bot.state;

/**
 * Stages of working with bot
 */
public enum BotState {
    /**
     * Initial stage, user press button "/start"
     */
    START,
    /**
     * Main Menu handling stage
     */
    MAIN_MENU,
    /**
     * Settings menu handling stage
     */
    SETTINGS,
    /**
     * Decimal options handling stage
     */
    DECIMAL_CHOICE,
    /**
     * Bank options handling stage
     */
    BANK_CHOICE,
    /**
     * Currency options handling stage
     */
    CURRENCY_CHOICE,
    /**
     * Notify time options handling stage
     */
    NOTIFY_CHOICE
}
