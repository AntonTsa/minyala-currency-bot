package org.project.ninjas.minyala.currency.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.project.ninjas.minyala.currency.bot.bot.CurrencyBot;
import org.project.ninjas.minyala.currency.bot.notifications.NotificationScheduler;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Tests for {@link AppLauncher}.
 */
class AppLauncherTest {
    @Test
    void givenMissingBotToken_whenMain_thenDoesNotConstructBotsApiOrScheduler() {
        // Given
        Dotenv dotenv = mock(Dotenv.class);
        when(dotenv.get("BOT_TOKEN")).thenReturn(null);
        when(dotenv.get("BOT_USERNAME")).thenReturn("username");

        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class);
                MockedConstruction<CurrencyBot> currencyConstruction = mockConstruction(CurrencyBot.class);
                MockedConstruction<TelegramBotsApi> botsApiConstruction = mockConstruction(TelegramBotsApi.class);
                MockedConstruction<NotificationScheduler> schedConstruction =
                        mockConstruction(NotificationScheduler.class)) {

            dotenvMock.when(Dotenv::load).thenReturn(dotenv);

            // When
            AppLauncher.main(new String[0]);

            // Then - no constructions should have occurred
            assertEquals(0, currencyConstruction.constructed().size());
            assertEquals(0, botsApiConstruction.constructed().size());
            assertEquals(0, schedConstruction.constructed().size());
        }
    }

    @Test
    void givenMissingBotUsername_whenMain_thenDoesNotConstructBotsApiOrScheduler() {
        // Given
        Dotenv dotenv = mock(Dotenv.class);
        when(dotenv.get("BOT_TOKEN")).thenReturn("token");
        when(dotenv.get("BOT_USERNAME")).thenReturn(null);

        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class);
                MockedConstruction<CurrencyBot> currencyConstruction = mockConstruction(CurrencyBot.class);
                MockedConstruction<TelegramBotsApi> botsApiConstruction = mockConstruction(TelegramBotsApi.class);
                MockedConstruction<NotificationScheduler> schedConstruction =
                        mockConstruction(NotificationScheduler.class)) {

            dotenvMock.when(Dotenv::load).thenReturn(dotenv);

            // When
            AppLauncher.main(new String[0]);

            // Then - no constructions should have occurred
            assertEquals(0, currencyConstruction.constructed().size());
            assertEquals(0, botsApiConstruction.constructed().size());
            assertEquals(0, schedConstruction.constructed().size());
        }
    }

    @Test
    void givenValidEnv_whenMain_thenRegistersBotAndStartsScheduler() {
        // Given
        Dotenv dotenv = mock(Dotenv.class);
        when(dotenv.get("BOT_TOKEN")).thenReturn("token");
        when(dotenv.get("BOT_USERNAME")).thenReturn("username");

        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class);
                MockedConstruction<CurrencyBot> currencyConstruction = mockConstruction(CurrencyBot.class);
                MockedConstruction<TelegramBotsApi> botsApiConstruction = mockConstruction(TelegramBotsApi.class);
                MockedConstruction<NotificationScheduler> schedConstruction =
                        mockConstruction(NotificationScheduler.class,
                            (mock, ctx) -> {
                                // ensure start() is a real mock method we can verify
                                doNothing().when(mock).start();
                            })) {

            dotenvMock.when(Dotenv::load).thenReturn(dotenv);

            // When
            AppLauncher.main(new String[0]);

            // Then - one instance each created
            assertEquals(1, currencyConstruction.constructed().size());
            assertEquals(1, botsApiConstruction.constructed().size());
            assertEquals(1, schedConstruction.constructed().size());

            // verify registerBot called with the created CurrencyBot instance
            TelegramBotsApi botsApiMock = botsApiConstruction.constructed().getFirst();
            CurrencyBot currencyBotMock = currencyConstruction.constructed().getFirst();
            verify(botsApiMock).registerBot(currencyBotMock);

            // verify scheduler.start was invoked
            NotificationScheduler schedulerMock = schedConstruction.constructed().getFirst();
            verify(schedulerMock).start();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void givenTelegramApiThrows_whenMain_thenSchedulerNotConstructed() {
        // Given
        Dotenv dotenv = mock(Dotenv.class);
        when(dotenv.get("BOT_TOKEN")).thenReturn("token");
        when(dotenv.get("BOT_USERNAME")).thenReturn("username");

        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class);
                MockedConstruction<TelegramBotsApi> botsApiConstruction = mockConstruction(TelegramBotsApi.class,
                        (mock, ctx) -> {
                            // cause registerBot to throw
                            doThrow(new TelegramApiException("registration failed")).when(mock).registerBot(any());
                        });
                MockedConstruction<NotificationScheduler> schedConstruction =
                        mockConstruction(NotificationScheduler.class)) {

            dotenvMock.when(Dotenv::load).thenReturn(dotenv);

            // When
            AppLauncher.main(new String[0]);

            // Then - botsApi was constructed, but scheduler should NOT be constructed because registerBot threw
            assertEquals(1, botsApiConstruction.constructed().size());
            assertEquals(0, schedConstruction.constructed().size());
        }
    }
}
