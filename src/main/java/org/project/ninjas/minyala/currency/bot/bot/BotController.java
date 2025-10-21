package org.project.ninjas.minyala.currency.bot.bot;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateContext;
import org.project.ninjas.minyala.currency.bot.bot.state.UserStateService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Controller that manages bot.
 */
@RequiredArgsConstructor
public class BotController {
    private final UserStateService userStateService;
    private final BotStateContext botStateContext;

    /**
     * Method for user update management. Firstly, it gets id of chat, then by the id
     * it gets current state and handle the state, gets response from handler, set new
     * state by the id and returns response.
     *
     * @param update user action
     * @return reply
     */
    public SendMessage handleUpdate(Update update) {
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        BotState currentState = userStateService.getUserState(chatId);

        BotResponse response = botStateContext.process(currentState, update);

        userStateService.setUserState(chatId, response.nextState());

        return response.message();
    }
}
