package org.project.ninjas.minyala.currency.bot.bot.service;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleBankInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleCurrencyChoiceInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleDecimalInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleMainMenuInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleNotifyInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleSettingsInvoker;
import org.project.ninjas.minyala.currency.bot.bot.state.HandleStartInvoker;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;

/**
 * Test class for {@link InvokersService}.
 */
public class InvokersServiceTest {
    private InvokersService invokersService;

    /** Setup before each test. */
    @BeforeEach
    public void setup() {
        SettingsService settings = mock(SettingsService.class);
        InfoService info = mock(InfoService.class);
        invokersService = new InvokersService(settings, info);
    }

    @Test
    void processReturnsCorrectInvokerForEachConfiguredState() {
        // GIVEN
        Map<BotState, Class<? extends BotStateInvoker>> expected = new EnumMap<>(BotState.class);
        expected.put(BotState.HANDLE_START, HandleStartInvoker.class);
        expected.put(BotState.HANDLE_MAIN_MENU, HandleMainMenuInvoker.class);
        expected.put(BotState.HANDLE_SETTINGS, HandleSettingsInvoker.class);
        expected.put(BotState.HANDLE_DECIMAL_CHOICE, HandleDecimalInvoker.class);
        expected.put(BotState.BANK_CHOICE, HandleBankInvoker.class);
        expected.put(BotState.CURRENCY_CHOICE, HandleCurrencyChoiceInvoker.class);
        expected.put(BotState.NOTIFY_CHOICE, HandleNotifyInvoker.class);

        for (Map.Entry<BotState, Class<? extends BotStateInvoker>> e : expected.entrySet()) {
            BotState state = e.getKey();
            Class<? extends BotStateInvoker> expectedClass = e.getValue();
            // WHEN
            BotStateInvoker invoker = invokersService.process(state);
            // THEN
            assertNotNull(invoker, "Invoker should not be null for state: " + state);
            assertInstanceOf(expectedClass, invoker, "Unexpected invoker type for state: " + state);
        }
    }

    @Test
    void processThrowsWhenNoInvokerFound() {
        // GIVEN

        // WHEN
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> invokersService.process(null));
        // THEN
        assertTrue(ex.getMessage().contains("No invoker found for state"),
                "Exception message should mention missing invoker");
    }
}
