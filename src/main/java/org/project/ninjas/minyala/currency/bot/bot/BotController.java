package org.project.ninjas.minyala.currency.bot.bot;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.service.InvokersService;
import org.project.ninjas.minyala.currency.bot.bot.service.UserStateService;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateInvoker;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Controller that manages bot.
 */
@RequiredArgsConstructor
public class BotController {
    private final UserStateService userStateService;
    private final InvokersService invokersService;

    /**
     * Method for user update management. Firstly, it gets id of chat, then by the id
     * it gets current state and handle the state, gets response from handler, set new
     * state by the id and returns response.
     *
     * @param update user action
     * @return reply
     */
    public SendMessage handleUpdate(Update update) {
        Long userId;

        if (update.hasMessage()) {
            userId = update.getMessage().getFrom().getId();
        } else {
            userId = update.getCallbackQuery().getFrom().getId();
        }

        BotState currentState = userStateService.getUserState(update);

        BotStateInvoker invoker = invokersService.process(currentState);

        BotResponse response = invoker.invoke(update);

        userStateService.setUserState(userId, response.nextState());

        return response.message();
    }

}
