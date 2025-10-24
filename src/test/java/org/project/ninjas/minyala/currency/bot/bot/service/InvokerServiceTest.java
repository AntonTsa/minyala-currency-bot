package org.project.ninjas.minyala.currency.bot.bot.service;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleCurrencyChoiceInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleDecimalInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleMainMenuInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleSettingsInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleStartInvoker;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;

/**
 * Tests for {@link InvokersService}.
 */

class InvokerServiceTest {
    private InvokersService service;

    @BeforeEach
    void setUp() {
        // Given: a mocked SettingsService and an InvokersService built with it
        SettingsService settingsService = Mockito.mock(SettingsService.class);
        service = new InvokersService(settingsService);
    }

    @Test
    void givenHandleStartState_whenProcess_thenReturnsHandleStartInvoker() {
        // Given: BotState.HANDLE_START
        // When
        BotStateInvoker inv = service.process(BotState.HANDLE_START);
        // Then
        assertInstanceOf(HandleStartInvoker.class, inv);
    }

    @Test
    void givenHandleMainMenuState_whenProcess_thenReturnsHandleMainMenuInvoker() {
        // Given: BotState.HANDLE_MAIN_MENU
        // When
        BotStateInvoker inv = service.process(BotState.HANDLE_MAIN_MENU);
        // Then
        assertInstanceOf(HandleMainMenuInvoker.class, inv);
    }

    @Test
    void givenHandleSettingsState_whenProcess_thenReturnsHandleSettingsInvoker() {
        // Given: BotState.HANDLE_SETTINGS
        // When
        BotStateInvoker inv = service.process(BotState.HANDLE_SETTINGS);
        // Then
        assertInstanceOf(HandleSettingsInvoker.class, inv);
    }

    @Test
    void givenDecimalChoiceState_whenProcess_thenReturnsHandleDecimalInvoker() {
        // Given: BotState.HANDLE_DECIMAL_CHOICE
        // When
        BotStateInvoker inv = service.process(BotState.HANDLE_DECIMAL_CHOICE);
        // Then
        assertInstanceOf(HandleDecimalInvoker.class, inv);
    }

    @Test
    void givenCurrencyChoiceState_whenProcess_thenReturnsHandleCurrencyChoiceInvoker() {
        // Given: BotState.CURRENCY_CHOICE
        // When
        BotStateInvoker inv = service.process(BotState.CURRENCY_CHOICE);
        // Then
        assertInstanceOf(HandleCurrencyChoiceInvoker.class, inv);
    }

    @Test
    void givenNoRegisteredInvoker_whenProcess_thenThrowsIllegalStateException() throws Exception {
        // Given: clear internal invokers map to simulate missing mapping
        Field field = InvokersService.class.getDeclaredField("invokers");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<BotState, BotStateInvoker> map = (Map<BotState, BotStateInvoker>) field.get(service);
        map.clear();

        // When / Then
        assertThrows(IllegalStateException.class, () -> service.process(BotState.HANDLE_START));
    }

}
