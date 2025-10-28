package org.project.ninjas.minyala.currency.bot.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SettingsService.
 */
class SettingsServiceTest {

    private SettingsService settingsService;

    @BeforeEach
    void setUp() {
        settingsService = new SettingsService();
    }

    @Test
    void testCreateUserSettings() {
        Long userId = 123L;

        settingsService.createUserSettings(userId);

        UserSettings settings = settingsService.getUsersSettings(userId);
        assertNotNull(settings, "Settings should not be null");
        assertEquals(userId, settings.getUserId(), "UserId should match");
    }

    @Test
    void testSaveUserSettings() {
        Long userId = 456L;
        UserSettings userSettings = new UserSettings(userId);

        settingsService.saveUserSettings(userSettings);

        UserSettings savedSettings = settingsService.getUsersSettings(userId);
        assertNotNull(savedSettings);
        assertEquals(userId, savedSettings.getUserId());
    }

    @Test
    void testGetUsersSettings_WhenNotExists() {
        Long userId = 789L;
        UserSettings settings = settingsService.getUsersSettings(userId);

        assertNull(settings, "Settings should be null if user doesn't exist");
    }

    @Test
    void testGetAllUserSettings() {
        UserSettings user1 = new UserSettings(1L);
        UserSettings user2 = new UserSettings(2L);

        settingsService.saveUserSettings(user1);
        settingsService.saveUserSettings(user2);

        List<UserSettings> allSettings = settingsService.getAllUserSettings();

        assertEquals(2, allSettings.size());
        assertTrue(allSettings.contains(user1));
        assertTrue(allSettings.contains(user2));
    }
}
