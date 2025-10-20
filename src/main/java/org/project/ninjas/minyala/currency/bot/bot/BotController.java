package org.project.ninjas.minyala.currency.bot.bot;

import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateContext;
import org.project.ninjas.minyala.currency.bot.bot.state.UserStateService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotController {
    private final UserStateService userStateService;
    private final BotStateContext botStateContext;

    public BotController(UserStateService userStateService,
                         BotStateContext botStateContext) {
        this.userStateService = userStateService;
        this.botStateContext = botStateContext;
    }

    public SendMessage handleUpdate(Update update) {
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        BotState currentState = userStateService.getUserState(chatId);

        BotResponse response = botStateContext.process(currentState, update);

        // зберігаємо новий стан користувача
        userStateService.setUserState(chatId, response.nextState());

        // відправляємо повідомлення
        return response.message();
    }
}
