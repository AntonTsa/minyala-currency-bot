package org.project.ninjas.minyala.currency.bot.bot.state;

import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Generic interface for bot state handling.
 */
public interface BotStateInvoker {
    /**
     * @return the state is handling
     */
    BotState getInvokedState();

    /**
     * Method for handling update in particular state.
     *
     * @param update - user's action
     * @return application response
     */
    BotResponse invoke(Update update);
}
