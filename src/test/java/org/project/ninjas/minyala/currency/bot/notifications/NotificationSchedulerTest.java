package org.project.ninjas.minyala.currency.bot.notifications;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.ninjas.minyala.currency.bot.bot.CurrencyBot;
import org.project.ninjas.minyala.currency.bot.bot.service.InfoService;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Unit tests for NotificationScheduler.
 */
class NotificationSchedulerTest {

    @Mock
    private SettingsService settingsService;

    @Mock
    private InfoService infoService;

    @Mock
    private CurrencyBot bot;

    private NotificationScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduler = new NotificationScheduler(settingsService, infoService, bot);
    }

    @Test
    void testCheckAndNotifyUsers_sendsNotificationAsync() throws TelegramApiException {
        // Мок користувача на поточну годину
        UserSettings user = mock(UserSettings.class);
        when(user.getNotifyTime())
                .thenReturn(LocalTime.now().withMinute(0).withSecond(0).toString().substring(0, 5));
        when(user.getUserId()).thenReturn(123L);

        when(settingsService.getAllUserSettings()).thenReturn(List.of(user));
        when(infoService.getCurrencyInfo(user)).thenReturn("Currency info");

        // Викликаємо метод
        scheduler.checkAndNotifyUsers();

        // Використовуємо Awaitility для очікування асинхронного виконання
        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
            verify(bot, times(1)).execute(captor.capture());

            SendMessage sentMessage = captor.getValue();
            assert sentMessage.getChatId().equals("123");
            assert sentMessage.getText().equals("Currency info");
        });
    }

    @Test
    void testCheckAndNotifyUsers_doesNotSendIfHourMismatch() throws TelegramApiException {
        UserSettings user = mock(UserSettings.class);
        // Set notifyTime one hour ahead
        int nextHour = (LocalTime.now().getHour() + 1) % 24;
        when(user.getNotifyTime()).thenReturn(String.format("%02d:00", nextHour));
        when(user.getUserId()).thenReturn(123L);

        when(settingsService.getAllUserSettings()).thenReturn(List.of(user));

        scheduler.checkAndNotifyUsers();

        // Bot should not be called
        verify(bot, never()).execute((SendMessage) any());
    }

    @Test
    void testSendNotification_handlesException() throws TelegramApiException {
        Long chatId = 123L;
        String message = "Test message";

        doThrow(new TelegramApiException("Failed")).when(bot).execute((SendDocument) any());

        scheduler.sendNotification(chatId, message);

        // Verify that bot.execute was called and exception handled (logged)
        verify(bot, times(1)).execute((SendMessage) any());
    }

    @Test
    void testComputeInitialDelayMinutes_returnsCorrectDelay() {
        long delay = scheduler.computeInitialDelayMinutes();
        int currentMinute = LocalTime.now().getMinute();
        assert delay == 60 - currentMinute;
    }
}
