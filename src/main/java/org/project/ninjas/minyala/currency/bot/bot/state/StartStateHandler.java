package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.START;

import java.util.List;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * First step implementation
 */
public class StartStateHandler implements BotStateHandler {
    private final SettingsService settingsService;

    public StartStateHandler(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Override
    public BotState getHandledState() {
        return START;
    }

    @Override
    public BotResponse handle(Update update) {
        settingsService.createUserSettings(update.getMessage().getChatId());
        SendMessage message = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Ласкаво просимо! Цей бот допоможе відслідкувати актуальний курс валют")
                .replyMarkup(getReplyMarkup())
                .build();

        return new BotResponse(message, MAIN_MENU);
    }

    private ReplyKeyboard getReplyMarkup() {
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
}
