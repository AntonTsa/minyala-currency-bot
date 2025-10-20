package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.SETTINGS;

import java.util.List;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Handler for Main Menu buttons
 */
public class MainMenuStateHandler implements BotStateHandler {

    @Override
    public BotState getHandledState() {
        return MAIN_MENU;
    }

    @Override
    public BotResponse handle(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        return switch (update.getCallbackQuery().getData()) {
            case "SETTINGS" -> getSettingsMenu(chatId);
            case "CURRENT_INFO" -> getInfo(chatId);
            default -> getExceptionReply(chatId);
        };
    }

    private BotResponse getExceptionReply(Long chatId) {

        return new BotResponse(
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Немає такої команди")
                        .replyMarkup(getMainMenuReplyMarkup())
                        .build(),
                this.getHandledState()
        );
    }

    private BotResponse getInfo(long chatId) {
        return new BotResponse(
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Немає даних")
                        .replyMarkup(getMainMenuReplyMarkup())
                        .build(),
                this.getHandledState()
        );
    }

    private BotResponse getSettingsMenu(long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Налаштування")
                .replyMarkup(getSettingsReplyMarkup())
                .build();
        return new BotResponse(message, SETTINGS);
    }

    private ReplyKeyboard getMainMenuReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(List.of(
                        InlineKeyboardButton.builder()
                                .text("Отримати інформацію")
                                .callbackData("CURRENT_INFO")
                                .build()),
                        List.of(
                        InlineKeyboardButton.builder()
                                .text("Змінити налаштування")
                                .callbackData("SETTINGS")
                                .build()
                ))
        );
    }

    private ReplyKeyboard getSettingsReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(List.of(
                        InlineKeyboardButton.builder()
                                .text("Кількість знаків після коми")
                                .callbackData("DECIMAL_CHOICE")
                                .build()),
                        List.of(
                        InlineKeyboardButton.builder()
                                .text("Банк")
                                .callbackData("BANK_CHOICE")
                                .build()),
                        List.of(
                        InlineKeyboardButton.builder()
                                .text("Валюти")
                                .callbackData("CURRENCY_CHOICE")
                                .build()),
                        List.of(
                        InlineKeyboardButton.builder()
                                .text("Час оповіщення")
                                .callbackData("NOTIFY_CHOICE")
                                .build()
                ))
        );
    }
}
