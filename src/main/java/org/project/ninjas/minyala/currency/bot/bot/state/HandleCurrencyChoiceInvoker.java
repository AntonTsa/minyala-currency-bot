package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.CURRENCY_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_SETTINGS;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.CHECKMARK;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_MAIN_MENU_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_BACK_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_BACK_MAIN_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_SETTINGS_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.btn;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.mainMenuReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.settingsReplyMarkup;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.util.Currency;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Handles the {@link BotState#CURRENCY_CHOICE} state.
 * Allows users to select which currencies to track
 * and navigate between menus.
 */
@RequiredArgsConstructor
public class HandleCurrencyChoiceInvoker implements BotStateInvoker {

    /** List of available currencies for display and selection. */
    private static final List<String> AVAILABLE_CURRENCIES =
            Arrays.stream(Currency.values())
                    .map(Currency::name)
                    .toList();

    /** Simple instruction to final user about this menu. */
    private static final String INSTRUCTION_TEXT_MSG = "Оберіть валюту:";

    /** Service for managing user settings. */
    private final SettingsService settingsService;

    @Override
    public BotState getInvokedState() {
        return CURRENCY_CHOICE;
    }

    /**
     * Processes callback updates received from the user.
     * <ul>
     *   <li>When a currency ("USD", "EUR", "GBP") is pressed — toggles its selection.</li>
     *   <li>When the back button is pressed — returns to the settings menu.</li>
     *   <li>Otherwise — refreshes the currency menu.</li>
     * </ul>
     *
     * @param update Telegram update containing callback data
     * @return {@link BotResponse} with a message and the next bot state
     */
    @Override
    public BotResponse invoke(Update update) {
        String chosenButtonData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        UserSettings userSettings = settingsService.getUsersSettings(chatId);
        List<Currency> selectedCurrencies = userSettings.getCurrencies();
        SendMessage message;
        BotState nextState;

        if (AVAILABLE_CURRENCIES.contains(chosenButtonData)) {
            // Toggle currency selection: remove if selected, add if not
            if (!selectedCurrencies.remove(Currency.valueOf(chosenButtonData))) {
                selectedCurrencies.add(Currency.valueOf(chosenButtonData));
            }

            // Save updated settings
            userSettings.setCurrencies(selectedCurrencies);
            settingsService.saveUserSettings(userSettings);

            message = buildCurrencyMenu(chatId, selectedCurrencies
            );
            nextState = CURRENCY_CHOICE;

        } else if (DATA_BACK_BTN.equals(chosenButtonData)) {
            // Return to settings menu
            message = SendMessage.builder()
                    .chatId(chatId)
                    .text(TEXT_SETTINGS_MENU)
                    .replyMarkup(settingsReplyMarkup())
                    .build();
            nextState = HANDLE_SETTINGS;

        } else if (DATA_BACK_MAIN_MENU_BTN.equals(chosenButtonData)) {
            // Return to main menu
            message = SendMessage.builder()
                    .chatId(chatId)
                    .text(TEXT_MAIN_MENU)
                    .replyMarkup(mainMenuReplyMarkup())
                    .build();
            nextState = HANDLE_MAIN_MENU;

        } else {
            // Default: refresh the current menu
            message = buildCurrencyMenu(chatId, selectedCurrencies
            );
            nextState = CURRENCY_CHOICE;
        }

        return new BotResponse(message, nextState);
    }

    /**
     * Invoked when transitioning from a parent menu.
     * Displays current user currency selections.
     *
     * @param chatId unique chat identifier
     * @return {@link BotResponse} with the current currency settings
     */
    public BotResponse invokeFromParent(Long chatId) {
        UserSettings userSettings = settingsService.getUsersSettings(chatId);
        List<Currency> selectedCurrencies = userSettings.getCurrencies();

        SendMessage message = buildCurrencyMenu(chatId, selectedCurrencies);
        return new BotResponse(message, CURRENCY_CHOICE);
    }

    /**
     * Builds the currency selection menu with checkmarks (✅) for selected currencies.
     *
     * @param chatId     user chat identifier
     * @param currencies list of selected currencies
     * @return {@link SendMessage} with an {@link InlineKeyboardMarkup}
     */
    private SendMessage buildCurrencyMenu(Long chatId, List<Currency> currencies) {
        // Build currency buttons dynamically
        List<InlineKeyboardButton> currencyButtons = AVAILABLE_CURRENCIES.stream()
                .map(code -> {
                    String label = currencies.contains(Currency.valueOf(code)) ? CHECKMARK + code : code;
                    return btn(label, code);
                })
                .toList();

        // Build full keyboard layout
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                List.of(
                        currencyButtons,
                        List.of(btn(TEXT_BACK_BTN, DATA_BACK_BTN)),
                        List.of(btn(TEXT_BACK_MAIN_BTN, DATA_BACK_MAIN_MENU_BTN))
                )
        );

        return SendMessage.builder()
                .chatId(chatId)
                .text(HandleCurrencyChoiceInvoker.INSTRUCTION_TEXT_MSG)
                .replyMarkup(markup)
                .build();
    }
}
