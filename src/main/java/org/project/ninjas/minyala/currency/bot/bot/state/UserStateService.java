package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.START;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserStateService {
    private final Map<Long, BotState> userStates;

    public UserStateService() {
        this.userStates = new ConcurrentHashMap<>();
    }

    public BotState getUserState(Long userId) {
        return userStates.getOrDefault(userId, START);
    }

    public void setUserState(Long userId, BotState userState) {
        userStates.put(userId, userState);
    }
}
