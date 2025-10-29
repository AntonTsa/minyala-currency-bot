package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.CURRENCY_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_SETTINGS;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_MAIN_MENU_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_SETTINGS_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.Currency.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.util.Currency;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class HandleCurrencyChoiceInvokerTest {
    private SettingsService settingsServiceMock;
    private HandleCurrencyChoiceInvoker invoker;

    @BeforeEach
    void setUp() {
        settingsServiceMock = mock(SettingsService.class);
        invoker = new HandleCurrencyChoiceInvoker(settingsServiceMock);
    }

    private Update mockUpdate(String data, long chatId) {
        Update updateMock = mock(Update.class);
        CallbackQuery callbackQueryMock = mock(CallbackQuery.class);
        Message messageMock = mock(Message.class);

        given(updateMock.getCallbackQuery()).willReturn(callbackQueryMock);
        given(callbackQueryMock.getData()).willReturn(data);
        given(callbackQueryMock.getMessage()).willReturn(messageMock);
        given(messageMock.getChatId()).willReturn(chatId);

        return updateMock;
    }

    private UserSettings mockUserSettings(List<Currency> currencies) {
        UserSettings settings = mock(UserSettings.class);
        given(settings.getCurrencies()).willReturn(currencies);
        return settings;
    }

    @Test
    void getInvokedState_shouldReturnCurrencyChoice() {
        // GIVEN
        // WHEN
        BotState actual = invoker.getInvokedState();
        // THEN
        assertEquals(CURRENCY_CHOICE, actual);
    }

    @Test
    void invoke_shouldToggleCurrencySelection_addNew() {
        // GIVEN
        long chatId = 1L;
        Update updateMock = mockUpdate("USD", chatId);
        List<Currency> selected = new ArrayList<>();
        UserSettings settingsMock = mockUserSettings(selected);

        given(settingsServiceMock.getUsersSettings(chatId)).willReturn(settingsMock);
        // WHEN
        BotResponse response = invoker.invoke(updateMock);
        SendMessage msg = response.message();
        // THEN
        assertEquals(CURRENCY_CHOICE, response.nextState());
        assertTrue(msg.getText().contains("Оберіть валюту"));
        verify(settingsServiceMock).saveUserSettings(settingsMock);
        assertTrue(selected.contains("USD"));
    }

    @Test
    void invoke_shouldToggleCurrencySelection_removeExisting() {
        long chatId = 2L;
        Update update = mockUpdate("EUR", chatId);
        List<Currency> selected = new ArrayList<>(List.of(EUR));

        UserSettings settings = mockUserSettings(selected);
        given(settingsServiceMock.getUsersSettings(chatId)).willReturn(settings);

        BotResponse response = invoker.invoke(update);

        assertEquals(CURRENCY_CHOICE, response.nextState());
        verify(settingsServiceMock).saveUserSettings(settings);
        assertFalse(selected.contains(EUR));
    }

    @Test
    void invoke_shouldReturnToSettingsMenu() {
        long chatId = 3L;
        Update update = mockUpdate(DATA_BACK_BTN, chatId);
        UserSettings settings = mockUserSettings(new ArrayList<>());

        given(settingsServiceMock.getUsersSettings(chatId)).willReturn(settings);

        BotResponse response = invoker.invoke(update);

        assertEquals(HANDLE_SETTINGS, response.nextState());
        SendMessage msg = response.message();
        assertEquals(TEXT_SETTINGS_MENU, msg.getText());
        assertNotNull(msg.getReplyMarkup());
        verify(settingsServiceMock, never()).saveUserSettings(any());
    }

    @Test
    void invoke_shouldReturnToMainMenu() {
        long chatId = 4L;
        Update update = mockUpdate(DATA_BACK_MAIN_MENU_BTN, chatId);
        UserSettings settings = mockUserSettings(new ArrayList<>());

        given(settingsServiceMock.getUsersSettings(chatId)).willReturn(settings);

        BotResponse response = invoker.invoke(update);

        assertEquals(HANDLE_MAIN_MENU, response.nextState());
        SendMessage msg = response.message();
        assertEquals(TEXT_MAIN_MENU, msg.getText());
        verify(settingsServiceMock, never()).saveUserSettings(any());
    }

    @Test
    void invoke_shouldRefreshMenuOnUnknownData() {
        long chatId = 5L;
        Update update = mockUpdate("UNKNOWN", chatId);
        UserSettings settings = mockUserSettings(new ArrayList<>());

        given(settingsServiceMock.getUsersSettings(chatId)).willReturn(settings);

        BotResponse response = invoker.invoke(update);

        assertEquals(CURRENCY_CHOICE, response.nextState());
        SendMessage msg = response.message();
        assertTrue(msg.getText().contains("Оберіть валюту"));
        verify(settingsServiceMock, never()).saveUserSettings(any());
    }

    @Test
    void invokeFromParent_shouldReturnCurrencyMenu() {
        long chatId = 6L;
        List<Currency> currencies = List.of(USD);
        UserSettings settings = mockUserSettings(currencies);
        given(settingsServiceMock.getUsersSettings(chatId)).willReturn(settings);

        BotResponse response = invoker.invokeFromParent(chatId);

        assertEquals(CURRENCY_CHOICE, response.nextState());
        SendMessage msg = response.message();
        assertTrue(msg.getText().contains("Оберіть валюту"));
        assertNotNull(msg.getReplyMarkup());
    }

    @Test
    void buildCurrencyMenu_shouldContainCheckmarksForSelectedCurrencies() throws Exception {
        long chatId = 7L;
        List<Currency> selected = List.of(GBP);
        UserSettings settings = mockUserSettings(selected);
        given(settingsServiceMock.getUsersSettings(chatId)).willReturn(settings);

        BotResponse response = invoker.invokeFromParent(chatId);
        SendMessage msg = response.message();

        String markupJson = msg.getReplyMarkup().toString();
        assertTrue(markupJson.contains("GBP"));
        assertTrue(markupJson.contains("USD"));
        assertTrue(markupJson.contains("EUR"));
    }
}
