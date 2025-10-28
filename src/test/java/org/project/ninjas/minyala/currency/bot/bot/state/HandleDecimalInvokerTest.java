package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_DECIMAL_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_MAIN_MENU_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_DECIMAL_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_SETTINGS_MENU;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Test class for {@link HandleDecimalInvoker}.
 */
public class HandleDecimalInvokerTest {

    private SettingsService settingsService;
    private HandleDecimalInvoker invoker;
    private UserSettings userSettings;
    private Update update;
    private CallbackQuery callbackQuery;

    @BeforeEach
    void setUp() {
        settingsService = mock(SettingsService.class);
        invoker = new HandleDecimalInvoker(settingsService);
        userSettings = new UserSettings(123L);
        update = mock(Update.class);
        callbackQuery = mock(CallbackQuery.class);
        Message message = mock(Message.class);

        given(update.getCallbackQuery()).willReturn(callbackQuery);
        given(callbackQuery.getMessage()).willReturn(message);
        given(message.getChatId()).willReturn(123L);
        given(settingsService.getUsersSettings(123L)).willReturn(userSettings);
    }

    @Test
    void testGetInvokedState() {
        assertEquals(HANDLE_DECIMAL_CHOICE, invoker.getInvokedState());
    }

    @Test
    void testInvoke_WhenOneSelected() {
        given(callbackQuery.getData()).willReturn(HandleDecimalInvoker.ONE);

        BotResponse response = invoker.invoke(update);

        verify(settingsService).saveUserSettings(userSettings);
        assertEquals(1, userSettings.getDecimalPlaces());
        assertEquals(HANDLE_DECIMAL_CHOICE, response.nextState());
        assertTrue(response.message().getText().contains(TEXT_DECIMAL_SETTINGS_BTN));
    }

    @Test
    void testInvoke_WhenTwoSelected() {
        given(callbackQuery.getData()).willReturn(HandleDecimalInvoker.TWO);

        BotResponse response = invoker.invoke(update);

        verify(settingsService).saveUserSettings(userSettings);
        assertEquals(2, userSettings.getDecimalPlaces());
        assertEquals(HANDLE_DECIMAL_CHOICE, response.nextState());
        assertEquals(TEXT_DECIMAL_SETTINGS_BTN, response.message().getText());
    }

    @Test
    void testInvoke_WhenThreeSelected() {
        given(callbackQuery.getData()).willReturn(HandleDecimalInvoker.THREE);

        BotResponse response = invoker.invoke(update);

        verify(settingsService).saveUserSettings(userSettings);
        assertEquals(3, userSettings.getDecimalPlaces());
        assertEquals(HANDLE_DECIMAL_CHOICE, response.nextState());
        assertEquals(TEXT_DECIMAL_SETTINGS_BTN, response.message().getText());
    }

    @Test
    void testInvoke_WhenDataBackButton() {
        given(callbackQuery.getData()).willReturn(DATA_BACK_BTN);

        BotResponse response = invoker.invoke(update);

        assertEquals(BotState.HANDLE_SETTINGS, response.nextState());
        assertEquals(TEXT_SETTINGS_MENU, response.message().getText());
        assertEquals("123", response.message().getChatId());
    }

    @Test
    void testInvoke_WhenDataBackMainMenuButton() {
        given(callbackQuery.getData()).willReturn(DATA_BACK_MAIN_MENU_BTN);

        BotResponse response = invoker.invoke(update);

        assertEquals(BotState.HANDLE_MAIN_MENU, response.nextState());
        assertEquals(TEXT_MAIN_MENU, response.message().getText());
    }

    @Test
    void testInvoke_WhenDefaultCase() {
        given(callbackQuery.getData()).willReturn("UNKNOWN");

        BotResponse response = invoker.invoke(update);

        assertEquals(HANDLE_DECIMAL_CHOICE, response.nextState());
        assertNotNull(response.message());
    }
}
