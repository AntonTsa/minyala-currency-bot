package org.project.ninjas.minyala.currency.bot.info;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_MAIN_MENU;

import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateInvoker;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Реалізація отримання інформації.
 **/
public class CurrentChecking implements BotStateInvoker {

    @Override
    public BotState getInvokedState() {
        return HANDLE_MAIN_MENU;
    }

    @Override
    public BotResponse invoke(Update update) {
        long chatId = 0;

        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        }

        SendMessage message;

        if (update.hasCallbackQuery()
                && "CURRENT_INFO".equals(update.getCallbackQuery().getData())) {

            String bankName = "ПриватБанк";
            String currency = "USD/UAH";
            double buy = 27.55;
            double sell = 27.95;

            String text = String.format(
                    "Курс у %s: %s\nПокупка: %.2f\nПродажа: %.2f",
                    bankName, currency, buy, sell
            );

            message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
        } else {
            message = SendMessage.builder()
                    .chatId(chatId)
                    .text(" ")
                    .build();
        }

        return new BotResponse(message, HANDLE_MAIN_MENU);
    }
}

