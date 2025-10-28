package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_MAIN_MENU_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_OFF_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_NOTIFY_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_SETTINGS_MENU;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Test class for {@link HandleNotifyInvoker}.
 */
class HandleNotifyInvokerTest {
    private SettingsService settingsService;
    private HandleNotifyInvoker invoker;

    @BeforeEach
    void setUp() {
        settingsService = mock(SettingsService.class);
        invoker = new HandleNotifyInvoker(settingsService);
    }

    private Update mockUpdate(Long chatId, String data) {
        Update update = mock(Update.class);
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        Message message = mock(Message.class);

        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(callbackQuery.getData()).thenReturn(data);
        when(message.getChatId()).thenReturn(chatId);
        return update;
    }

    @Test
    void getInvokedState_ShouldReturnNotifyChoice() {
        assertEquals(BotState.NOTIFY_CHOICE, invoker.getInvokedState());
    }

    @Test
    void invoke_ShouldHandleSpecificHour() {
        // given
        Update update = mockUpdate(123L, "09:00");
        UserSettings settings = new UserSettings(123L);
        when(settingsService.getUsersSettings(123L)).thenReturn(settings);

        // when
        BotResponse response = invoker.invoke(update);

        // then
        verify(settingsService).saveUserSettings(settings);
        assertEquals("09:00", settings.getNotifyTime());
        assertEquals(BotState.NOTIFY_CHOICE, response.nextState());
        assertTrue(response.message().getText().contains(TEXT_NOTIFY_SETTINGS_BTN));
    }

    @Test
    void invoke_ShouldHandleDataOffButton() {
        Update update = mockUpdate(456L, DATA_OFF_BTN);
        UserSettings settings = new UserSettings(456L);
        when(settingsService.getUsersSettings(456L)).thenReturn(settings);

        BotResponse response = invoker.invoke(update);

        verify(settingsService).saveUserSettings(settings);
        assertEquals("0", settings.getNotifyTime());
        assertEquals(BotState.NOTIFY_CHOICE, response.nextState());
    }

    @Test
    void invoke_ShouldHandleBackButton() {
        Update update = mockUpdate(789L, DATA_BACK_BTN);
        UserSettings settings = new UserSettings(789L);
        when(settingsService.getUsersSettings(789L)).thenReturn(settings);

        BotResponse response = invoker.invoke(update);

        assertEquals(BotState.HANDLE_SETTINGS, response.nextState());
        SendMessage msg = response.message();
        assertTrue(msg.getText().contains(TEXT_SETTINGS_MENU));
    }

    @Test
    void invoke_ShouldHandleBackMainMenuButton() {
        Update update = mockUpdate(999L, DATA_BACK_MAIN_MENU_BTN);
        UserSettings settings = new UserSettings(999L);
        when(settingsService.getUsersSettings(999L)).thenReturn(settings);

        BotResponse response = invoker.invoke(update);

        assertEquals(BotState.HANDLE_MAIN_MENU, response.nextState());
        SendMessage msg = response.message();
        assertTrue(msg.getText().contains(TEXT_MAIN_MENU));
    }

    @Test
    void invoke_ShouldHandleDefaultCase() {
        Update update = mockUpdate(111L, "UNKNOWN_BTN");
        UserSettings settings = new UserSettings(111L);
        when(settingsService.getUsersSettings(111L)).thenReturn(settings);

        BotResponse response = invoker.invoke(update);

        assertEquals(BotState.NOTIFY_CHOICE, response.nextState());
        SendMessage msg = response.message();
        assertNotNull(msg);
    }
}
