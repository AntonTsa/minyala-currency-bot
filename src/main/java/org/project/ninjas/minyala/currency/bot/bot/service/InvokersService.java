package org.project.ninjas.minyala.currency.bot.bot.service;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_START;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleStartInvoker;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * BotStateContext saves connection of each bot state with its handler.
 */
public class InvokersFactory {
    private final Map<BotState, BotStateInvoker> invokers = new EnumMap<>(BotState.class);

    /**
     * The constructor creates default handlers and put it in map.
     *
     * @param settingsService - settings service
     */
    public InvokersFactory(SettingsService settingsService) {
        invokers.put(HANDLE_START, new HandleStartInvoker(settingsService));
        invokers.put(HANDLE_MAIN_MENU, new HandleStartInvoker(settingsService));
        // Placeholder for actual main menu handler
    }

    /**
     * The constructor takes list of state handlers and put it in map, where keys are states.
     * they're handling
     *
     * @param handlerList - list of state
     */
    public InvokersFactory(List<BotStateInvoker> handlerList) {
        handlerList.forEach(handler -> invokers.put(handler.getInvokedState(), handler));
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
        BotStateInvoker invoker = invokers.get(state);
        if (invoker == null) {
            throw new IllegalStateException("No invoker found for state: " + state);
        }
        return invoker.invoke(update);
    }
}
