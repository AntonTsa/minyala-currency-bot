package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.START;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
     * If there is no such id in dtorage, it returns default
     * state, which is START
     *
     * @param userId - user chat id
     * @return - current state for the user
     */
    public BotState getUserState(Long userId) {
        return userStates.getOrDefault(userId, START);
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
