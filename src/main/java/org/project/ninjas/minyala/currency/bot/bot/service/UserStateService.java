package org.project.ninjas.minyala.currency.bot.bot.service;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_START;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Service for saving current state for each user.
 */
public class UserStateService {
    private final Map<Long, BotState> userStates;

    /**
     * Constructor, initializing user states.
     */
    public UserStateService() {
        this.userStates = new ConcurrentHashMap<>();
    }

    /**
     * Gives from map a corresponding state by user id.
     * If there is no such id in storage, it returns default
     * state, which is START
     *
     * @param update - update from user
     * @return - current state for the user
     */
    public BotState getUserState(Update update) {
        Long userId = update.hasMessage()
                ? update.getMessage().getFrom().getId()
                : update.getCallbackQuery().getFrom().getId();

        BotState currentState = userStates.get(userId);

        if (currentState != null) {
            return currentState;
        } else {
            if (update.hasMessage()
                    && update.getMessage().hasText()
                    && update.getMessage().getText().equals("/start")) {
                return HANDLE_START;
            } else {
                throw new IllegalStateException("No state found for user: " + userId);
            }
        }
    }

    /**
     * Adds a state to the user id.
     *
     * @param userId - user chat id
     * @param userState - current state
     */
    public void setUserState(Long userId, BotState userState) {
        userStates.put(userId, userState);
    }
}
