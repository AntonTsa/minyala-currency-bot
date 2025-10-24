/*package org.project.ninjas.minyala.currency.bot.current_info;

import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.MAIN_MENU;*/

/**Реалізація отримання інформації**/
/*  public  class CurrentChecking implements BotStateHandler {


  @Override
    public BotState getHandledState() {
        return MAIN_MENU;
    }

    @Override
    public BotResponse handle(Update update) {
        long chatId = 0;

        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else {
            chatId = 0;
        }

        SendMessage message;

        if (update.hasCallbackQuery() &&
                "CURRENT_INFO".equals(update.getCallbackQuery().getData())) {

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

        return new BotResponse(message, MAIN_MENU);
    }
}*/

