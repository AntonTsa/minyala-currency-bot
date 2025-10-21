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
        settings.put(userSettings.getChatId(), userSettings);
    }

    /**
     * Save default user's settings by his chatId.
     *
     * @param chatId - user's chat id
     */
    public void createUserSettings(Long chatId) {
        UserSettings userSettings = new UserSettings(chatId);
        settings.put(userSettings.getChatId(), userSettings);
    }

    /**
     * Returns existing settings or creates defaults for the given chat id.
     *
     * @param chatId the user's chat id.
     * @return existing or newly created settings.
     */
    public UserSettings getOrCreate(Long chatId) {
        return settings.computeIfAbsent(chatId, UserSettings::new);
    }

}
