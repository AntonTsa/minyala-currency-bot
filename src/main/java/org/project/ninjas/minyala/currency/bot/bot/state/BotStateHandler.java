package org.project.ninjas.minyala.currency.bot.bot.state;

import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Інтерфейс для обробників станів (State pattern).
 */
public interface BotStateHandler {
    BotState getHandledState();

    BotResponse handle(Update update);
}
