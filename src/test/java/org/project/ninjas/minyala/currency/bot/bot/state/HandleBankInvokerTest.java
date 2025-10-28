package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
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
 * Test class for {@link HandleBankInvoker}.
 */
class HandleBankInvokerTest {

    private SettingsService settingsService;
    private HandleBankInvoker invoker;
    private Update update;
    private CallbackQuery callbackQuery;

    @BeforeEach
    void setUp() {
        settingsService = mock(SettingsService.class);
        invoker = new HandleBankInvoker(settingsService);

        update = mock(Update.class);
        callbackQuery = mock(CallbackQuery.class);
        Message message = mock(Message.class);

        given(update.getCallbackQuery()).willReturn(callbackQuery);
        given(callbackQuery.getMessage()).willReturn(message);
        given(message.getChatId()).willReturn(123L);
    }

    @Test
    void testGetInvokedState() {
        // GIVEN
        BotState expected = BotState.BANK_CHOICE;
        // WHEN
        BotState actual = invoker.getInvokedState();
        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void testInvoke_Privat() {
        // GIVEN
        given(callbackQuery.getData()).willReturn("PRIVAT");

        UserSettings userSettings = new UserSettings(123L);
        userSettings.setBanks(new ArrayList<>());
        given(settingsService.getUsersSettings(123L)).willReturn(userSettings);
        // WHEN
        BotResponse response = invoker.invoke(update);
        SendMessage msg = response.message();
        // THEN
        assertNotNull(response);
        assertEquals(BotState.BANK_CHOICE, response.nextState());
        assertTrue(msg.getText().contains("Банк"));
        verify(settingsService).saveUserSettings(any());
    }

    @Test
    void testInvoke_Mono() {
        // GIVEN
        given(callbackQuery.getData()).willReturn("MONO");

        UserSettings userSettings = new UserSettings(123L);
        userSettings.setBanks(new ArrayList<>());
        given(settingsService.getUsersSettings(123L)).willReturn(userSettings);
        // WHEN
        BotResponse response = invoker.invoke(update);
        // THEN
        assertEquals(BotState.BANK_CHOICE, response.nextState());
        verify(settingsService).saveUserSettings(any());
    }

    @Test
    void testInvoke_Nbu() {
        // GIVEN
        given(callbackQuery.getData()).willReturn("NBU");

        UserSettings userSettings = new UserSettings(123L);
        userSettings.setBanks(new ArrayList<>());
        given(settingsService.getUsersSettings(123L)).willReturn(userSettings);
        // WHEN
        BotResponse response = invoker.invoke(update);
        // THEN
        assertEquals(BotState.BANK_CHOICE, response.nextState());
        verify(settingsService).saveUserSettings(any());
    }

    @Test
    void testInvoke_BackButton() {
        // GIVEN
        given(callbackQuery.getData()).willReturn("BACK");
        // WHEN
        BotResponse response = invoker.invoke(update);
        // THEN
        assertEquals(BotState.HANDLE_SETTINGS, response.nextState());
        assertTrue(response.message().getText().contains("Налаштування"));
    }

    @Test
    void testInvoke_BackMainMenuButton() {
        // GIVEN
        given(callbackQuery.getData()).willReturn("BACKALL");
        // WHEN
        BotResponse response = invoker.invoke(update);
        // THEN
        assertEquals(BotState.HANDLE_MAIN_MENU, response.nextState());
        assertTrue(response.message().getText().contains("Головне меню"));
    }

    @Test
    void testInvoke_DefaultCase() {
        // GIVEN
        given(callbackQuery.getData()).willReturn("UNKNOWN");
        // WHEN
        BotResponse response = invoker.invoke(update);
        // THEN
        assertNotNull(response);
        assertEquals(BotState.BANK_CHOICE, response.nextState());
        assertNotNull(response.message());
    }
}
