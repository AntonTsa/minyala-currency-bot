package org.project.ninjas.minyala.currency.bot.bot;

import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Data Transfer Object to send response from BotStateHandler to {@link BotController}.
 *
 * @param message - {@link SendMessage} response from BotStateHandler
 * @param nextState next state to set
 */
public record BotResponse(
        SendMessage message,
        BotState nextState
) {}
