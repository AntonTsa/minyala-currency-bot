package org.project.ninjas.minyala.currency.bot.bot.state;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * BotStateContext saves connection of each bot state with its handler.
 */
public class BotStateContext {
    private final Map<BotState, BotStateHandler> handlers = new EnumMap<>(BotState.class);

    /**
     * The constructor takes list of state handlers and put it in map, where keys are states.
     * they're handling
     *
     * @param handlerList - list of state
     */
    public BotStateContext(List<BotStateHandler> handlerList) {
        handlerList.forEach(handler -> handlers.put(handler.getHandledState(), handler));
    }

    /**
     * The method gets update from user and current state, and send the update to
     * corresponding handler/ After the wor has done, it sends respond back.
     *
     * @param state - current state
     * @param update - user's action
     * @return response from correspondent handler
     */
    public BotResponse process(BotState state, Update update) {
        BotStateHandler handler = handlers.get(state);
        if (handler == null) {
            throw new IllegalStateException("No handler found for state: " + state);
        }
        return handler.handle(update);
    }
}
