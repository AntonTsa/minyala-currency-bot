package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_START;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.mainMenuReplyMarkup;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * First step implementation.
 */
@RequiredArgsConstructor
public class HandleStartInvoker implements BotStateInvoker {

    @Override
    public BotState getInvokedState() {
        return HANDLE_START;
    }

    @Override
    public BotResponse invoke(Update update) {
        return new BotResponse(SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Ласкаво просимо! Цей бот допоможе відслідкувати актуальний курс валют")
                .replyMarkup(mainMenuReplyMarkup())
                .build(),
        HANDLE_MAIN_MENU);
    }

}
