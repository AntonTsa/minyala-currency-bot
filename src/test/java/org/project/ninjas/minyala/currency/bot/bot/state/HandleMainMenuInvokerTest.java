package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_GET_INFO_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_SETTINGS_MENU_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_EXCEPTION;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_SETTINGS_MENU;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.service.InfoService;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Test class for {@link HandleMainMenuInvoker}.
 */
class HandleMainMenuInvokerTest {

    private SettingsService settingsService;
    private InfoService infoService;
    private HandleMainMenuInvoker invoker;

    @BeforeEach
    void setUp() {
        settingsService = mock(SettingsService.class);
        infoService = mock(InfoService.class);
        invoker = new HandleMainMenuInvoker(settingsService, infoService);
    }

    private Update createUpdate(long chatId, String data) {
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(chatId);

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.getData()).thenReturn(data);
        when(callbackQuery.getMessage()).thenReturn(message);

        Update update = mock(Update.class);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);

        return update;
    }

    @Test
    void testGetInvokedState() {
        assertEquals(BotState.HANDLE_MAIN_MENU, invoker.getInvokedState());
    }

    @Test
    void testHandleSettingsButton_createsSettingsResponse() {
        long chatId = 123L;
        Update update = createUpdate(chatId, DATA_SETTINGS_MENU_BTN);

        BotResponse response = invoker.invoke(update);

        assertNotNull(response);
        assertEquals(BotState.HANDLE_SETTINGS, response.nextState());
        assertTrue(response.message().getText().contains(TEXT_SETTINGS_MENU));
        assertEquals(chatId, Long.parseLong(response.message().getChatId()));
    }

    @Test
    void testHandleCurrentInfoButton_userSettingsExists() {
        long chatId = 123L;
        Update update = createUpdate(chatId, DATA_GET_INFO_BTN);

        UserSettings mockSettings = mock(UserSettings.class);
        when(settingsService.getUsersSettings(chatId)).thenReturn(mockSettings);
        when(infoService.getCurrencyInfo(mockSettings)).thenReturn("USD = 40");

        BotResponse response = invoker.invoke(update);

        assertNotNull(response);
        assertEquals(BotState.HANDLE_MAIN_MENU, response.nextState());
        assertTrue(response.message().getText().contains("USD"));
        verify(settingsService, never()).createUserSettings(chatId);
    }

    @Test
    void testHandleCurrentInfoButton_userSettingsIsNull_createsNew() {
        long chatId = 321L;
        Update update = createUpdate(chatId, DATA_GET_INFO_BTN);

        when(settingsService.getUsersSettings(chatId))
                .thenReturn(null)
                .thenReturn(new UserSettings(chatId));
        when(infoService.getCurrencyInfo(any())).thenReturn("EUR = 42");

        BotResponse response = invoker.invoke(update);

        verify(settingsService).createUserSettings(chatId);
        verify(infoService).getCurrencyInfo(any(UserSettings.class));

        assertNotNull(response);
        assertEquals(BotState.HANDLE_MAIN_MENU, response.nextState());
        assertTrue(response.message().getText().contains("EUR"));
    }

    @Test
    void testHandleExceptionalCases_returnsDefaultResponse() {
        long chatId = 999L;
        Update update = createUpdate(chatId, "UNKNOWN_BUTTON");

        when(settingsService.getUsersSettings(chatId)).thenReturn(new UserSettings(chatId));

        BotResponse response = invoker.invoke(update);

        assertNotNull(response);
        assertEquals(BotState.HANDLE_MAIN_MENU, response.nextState());
        assertEquals(TEXT_EXCEPTION, response.message().getText());
        assertEquals(chatId, Long.parseLong(response.message().getChatId()));
    }

    @Test
    void testInvoke_createsUserSettingsWhenNullInitially() {
        long chatId = 777L;
        Update update = createUpdate(chatId, DATA_GET_INFO_BTN);

        when(settingsService.getUsersSettings(chatId))
                .thenReturn(null)
                .thenReturn(mock(UserSettings.class));
        when(infoService.getCurrencyInfo(any())).thenReturn("Test Info");

        invoker.invoke(update);

        verify(settingsService).createUserSettings(chatId);
    }
}
