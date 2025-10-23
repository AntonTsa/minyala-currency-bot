package org.project.ninjas.minyala.currency.bot.settings;

import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

/**
 * Service to store and manage settings of each user.
 */
@RequiredArgsConstructor
public class SettingsService {
    private final ConcurrentHashMap<Long, UserSettings> settings = new ConcurrentHashMap<>();

    /**
     * Save custom user's settings.
     *
     * @param userSettings - custom settings
     */
    public void saveUserSettings(UserSettings userSettings) {
        settings.put(userSettings.getUserId(), userSettings);
    }

    /**
     * Save default user's settings by his chatId.
     *
     * @param userId - user's chat id
     */
    public void createUserSettings(Long userId) {
        UserSettings userSettings = new UserSettings(userId);
        settings.put(userSettings.getUserId(), userSettings);
    }

    public UserSettings getUserSettings(Long userId){
        return settings.get(userId);
    }

    public ConcurrentHashMap<Long, UserSettings> getAllUserSettings(Long userId){
        return settings;
    }
}
