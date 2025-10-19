package settings;

import java.util.concurrent.ConcurrentHashMap;

public class SettingsService {
    private final ConcurrentHashMap<Long, UserSettings> settings = new ConcurrentHashMap<>();

    public UserSettings getUserSetting(Long chatId) {

        return settings.get(chatId);
    }

    public void saveUserSettings(UserSettings userSettings) {

        settings.put(userSettings.getChatId(), userSettings);
    }

    public void createUserSettings(Long chatId) {

        UserSettings userSettings = new UserSettings(chatId);
        settings.put(userSettings.getChatId(), userSettings);
    }
}
