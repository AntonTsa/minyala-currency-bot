package org.project.ninjas.minyala.currency.bot.bot.state;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.Bank;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Handler that manages the BANK_CHOICE state and persists a selected bank.
 */
@RequiredArgsConstructor
public class BankSelectionStateHandler implements BotStateHandler {

    private final SettingsService settingsService;

    /**
     * Returns the state that this handler processes.
     *
     * @return the handled bot state.
     */
    @Override
    public BotState getHandledState() {
        return BotState.BANK_CHOICE;
    }

    /**
     * Handles bank selection flow. If a bank button is pressed, saves it and returns to SETTINGS.
     * If no selection yet, renders the bank menu with a visual check mark near the current bank.
     *
     * @param update the Telegram update object.
     * @return the bot response with the next state.
     */
    @Override
    public BotResponse handle(Update update) {
        long chatId = update.hasMessage()
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getMessage().getChatId();

        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            if (data != null && data.startsWith("bank_")) {
                Bank selected = switch (data) {
                    case "bank_monobank" -> Bank.MONOBANK;
                    case "bank_privatbank" -> Bank.PRIVATBANK;
                    case "bank_nbu" -> Bank.NBU;
                    default -> Bank.PRIVATBANK;
                };

                UserSettings settings = settingsService.getOrCreate(chatId);
                settings.setBank(selected);
                settingsService.saveUserSettings(settings);

                SendMessage confirmation = SendMessage.builder()
                        .chatId(chatId)
                        .text("✅ Обрано банк: " + selected.getLabel())
                        .replyMarkup(buildSettingsMenuMarkup())
                        .build();
                return new BotResponse(confirmation, BotState.SETTINGS);
            }
        }

        SendMessage menu = buildBankMenuMessage(chatId);
        return new BotResponse(menu, BotState.BANK_CHOICE);
    }

    /**
     * Builds the bank selection menu with a check mark near the currently selected bank.
     *
     * @param chatId the user's chat id.
     * @return the SendMessage that contains the inline keyboard.
     */
    public SendMessage buildBankMenuMessage(long chatId) {
        Bank current = settingsService.getOrCreate(chatId).getBank();

        String monoText = labelWithCheck(current, Bank.MONOBANK);
        String privatText = labelWithCheck(current, Bank.PRIVATBANK);
        String nbuText = labelWithCheck(current, Bank.NBU);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text(monoText)
                                .callbackData("bank_monobank")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text(privatText)
                                .callbackData("bank_privatbank")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text(nbuText)
                                .callbackData("bank_nbu")
                                .build())
                )
        );

        return SendMessage.builder()
                .chatId(chatId)
                .text("Оберіть банк:")
                .replyMarkup(markup)
                .build();
    }

    /**
     * Returns the label of a bank prefixed with a visual check if it is selected.
     *
     * @param current the currently selected bank.
     * @param candidate the candidate bank to render.
     * @return the decorated label.
     */
    private String labelWithCheck(Bank current, Bank candidate) {
        return (current == candidate ? "✅ " : "◻️ ") + candidate.getLabel();
    }

    /**
     * Builds the settings menu markup shown after a selection is saved.
     *
     * @return the inline keyboard markup for the settings menu.
     */
    private InlineKeyboardMarkup buildSettingsMenuMarkup() {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text("Кількість знаків після коми")
                                .callbackData("DECIMAL_CHOICE")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text("Банк")
                                .callbackData("BANK_CHOICE")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text("Валюти")
                                .callbackData("CURRENCY_CHOICE")
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text("Час оповіщення")
                                .callbackData("NOTIFY_CHOICE")
                                .build())
                )
        );
    }
}
