package org.project.ninjas.minyala.currency.bot.bot.service;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.*;

import java.util.EnumMap;
import java.util.Map;
import org.project.ninjas.minyala.currency.bot.bot.state.*;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;

/**
 * BotStateContext saves connection of each bot state with its handler.
 */
public class InvokersService {
    private final Map<BotState, BotStateInvoker> invokers = new EnumMap<>(BotState.class);

    /**
     * The constructor creates default handlers and put it in map.
     *
     * @param settingsService - settings service
     */
    public InvokersService(SettingsService settingsService) {
        invokers.put(HANDLE_START, new HandleStartInvoker(settingsService));
        invokers.put(HANDLE_MAIN_MENU, new HandleMainMenuInvoker(settingsService));
        invokers.put(HANDLE_SETTINGS, new HandleSettingsInvoker(settingsService));
        invokers.put(HANDLE_DECIMAL_CHOICE, new HandleDecimalInvoker(settingsService));
        invokers.put(BANK_CHOICE, new HandleBankInvoker(settingsService));
        invokers.put(CURRENCY_CHOICE, new HandleCurrencyChoiceInvoker(settingsService));
        invokers.put(NOTIFY_CHOICE, new HandleNotifyInvoker(settingsService));
    }

    /**
     * The method gets update from user and current state, and send the update to
     * corresponding handler/ After the wor has done, it sends respond back.
     *
     * @param state - current state
     * @return response from correspondent handler
     */
    public BotStateInvoker process(BotState state) {
        BotStateInvoker invoker = invokers.get(state);
        if (invoker == null) {
            throw new IllegalStateException("No invoker found for state: " + state);
        }
        return invoker;
    }

}
