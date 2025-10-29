package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BANK_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_CURRENCY_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_DECIMAL_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_NOTIFY_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_BACK_MAIN_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_EXCEPTION;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.util.Bank;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Test class for {@link HandleSettingsInvoker}.
 */
class HandleSettingsInvokerTest {

    private SettingsService settingsService;
    private HandleSettingsInvoker invoker;
    private Update update;
    private CallbackQuery callbackQuery;

    @BeforeEach
    void setUp() {
        settingsService = mock(SettingsService.class);
        invoker = new HandleSettingsInvoker(settingsService);
        callbackQuery = mock(CallbackQuery.class);
        update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(123L);
    }

    private void prepareCallback(String data) {
        when(callbackQuery.getData()).thenReturn(data);
    }

    private UserSettings mockUserSettings() {
        UserSettings settings = new UserSettings(123L);
        settings.setDecimalPlaces(2);
        settings.setBanks(List.of(Bank.PRIVAT));
        settings.setNotifyTime("10:00");
        return settings;
    }

    @Test
    void testGetInvokedState() {
        assertEquals(BotState.HANDLE_SETTINGS, invoker.getInvokedState());
    }

    @Test
    void testHandleDecimalButton() {
        prepareCallback(DATA_DECIMAL_SETTINGS_BTN);
        when(settingsService.getUsersSettings(123L)).thenReturn(mockUserSettings());

        BotResponse response = invoker.invoke(update);

        assertEquals(BotState.HANDLE_DECIMAL_CHOICE, response.nextState());
        assertTrue(response.message().getText().contains("Оберіть кількість знаків"));
        assertEquals("123", response.message().getChatId());
        assertNotNull(response.message().getReplyMarkup());
    }

    @Test
    void testHandleBankButton() {
        prepareCallback(DATA_BANK_SETTINGS_BTN);
        when(settingsService.getUsersSettings(123L)).thenReturn(mockUserSettings());

        BotResponse response = invoker.invoke(update);

        assertEquals(BotState.BANK_CHOICE, response.nextState());
        assertTrue(response.message().getText().contains("Оберіть банк"));
        assertEquals("123", response.message().getChatId());
    }

    @Test
    void testHandleCurrencyButton() {
        prepareCallback(DATA_CURRENCY_SETTINGS_BTN);
        when(settingsService.getUsersSettings(123L)).thenReturn(mockUserSettings());

        // Spy on HandleCurrencyChoiceInvoker to verify delegation
        HandleSettingsInvoker spyInvoker = Mockito.spy(invoker);
        BotResponse mockResponse =
                new BotResponse(SendMessage.builder().chatId(123L).text("Currency").build(), BotState.CURRENCY_CHOICE);
        HandleCurrencyChoiceInvoker currencyInvoker = mock(HandleCurrencyChoiceInvoker.class);
        doReturn(mockResponse).when(currencyInvoker).invokeFromParent(123L);

        // Replace direct instantiation
        try (var mocked = Mockito.mockConstruction(HandleCurrencyChoiceInvoker.class,
                (mock, context) -> when(mock.invokeFromParent(123L)).thenReturn(mockResponse))) {

            BotResponse response = spyInvoker.invoke(update);

            assertEquals(BotState.CURRENCY_CHOICE, response.nextState());
            assertEquals("Currency", response.message().getText());
        }
    }

    @Test
    void testHandleNotifyButton() {
        prepareCallback(DATA_NOTIFY_SETTINGS_BTN);
        when(settingsService.getUsersSettings(123L)).thenReturn(mockUserSettings());

        BotResponse response = invoker.invoke(update);

        assertEquals(BotState.NOTIFY_CHOICE, response.nextState());
        assertTrue(response.message().getText().contains("Оберіть час сповіщень"));
        assertEquals("123", response.message().getChatId());
    }

    @Test
    void testHandleBackButton() {
        prepareCallback(DATA_BACK_BTN);

        BotResponse response = invoker.invoke(update);

        assertEquals(BotState.HANDLE_MAIN_MENU, response.nextState());
        assertEquals(TEXT_BACK_MAIN_BTN, response.message().getText());
        assertNotNull(response.message().getReplyMarkup());
    }

    @Test
    void testHandleExceptionalCase() {
        prepareCallback("UNKNOWN_DATA");

        BotResponse response = invoker.invoke(update);

        assertEquals(BotState.HANDLE_SETTINGS, response.nextState());
        assertEquals(TEXT_EXCEPTION, response.message().getText());
        assertNotNull(response.message().getReplyMarkup());
    }
}
