package org.project.ninjas.minyala.currency.bot.bot.state;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Handler that manages the SETTINGS state.
 */
@RequiredArgsConstructor
public class SettingsStateHandler implements BotStateHandler {

    private final SettingsService settingsService; // ⬅️ той самий інстанс

    @Override
    public BotState getHandledState() {
        return BotState.SETTINGS;
    }

    @Override
    public BotResponse handle(Update update) {
        long chatId = update.hasMessage()
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getMessage().getChatId();

        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            if ("BANK_CHOICE".equals(data)) {
                // ⬇️ ВЛАСНЕ ВИПРАВЛЕННЯ
                BankSelectionStateHandler bankHandler =
                        new BankSelectionStateHandler(settingsService);
                SendMessage msg = bankHandler.buildBankMenuMessage(chatId);
                return new BotResponse(msg, BotState.BANK_CHOICE);
            }
        }

        SendMessage msg = buildSettingsMenuMessage(chatId);
        return new BotResponse(msg, BotState.SETTINGS);
    }

    private SendMessage buildSettingsMenuMessage(long chatId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
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
        return SendMessage.builder()
                .chatId(chatId)
                .text("Налаштування")
                .replyMarkup(markup)
                .build();
    }
}
