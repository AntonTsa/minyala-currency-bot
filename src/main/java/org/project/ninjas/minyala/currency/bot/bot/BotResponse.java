package org.project.ninjas.minyala.currency.bot.bot;

import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public record BotResponse(
        SendMessage message,
        BotState nextState
) {}
