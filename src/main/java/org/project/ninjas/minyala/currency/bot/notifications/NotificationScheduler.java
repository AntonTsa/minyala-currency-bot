package org.project.ninjas.minyala.currency.bot.notifications;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.project.ninjas.minyala.currency.bot.bot.CurrencyBot;
import org.project.ninjas.minyala.currency.bot.bot.service.InfoService;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Scheduler for sending notifications to users at their specified times.
 */
public class NotificationScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationScheduler.class);
    private final SettingsService settingsService;
    private final InfoService infoService;
    private final CurrencyBot bot;

    // Пул для перевірки часу (1 потік)
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Пул для відправки повідомлень (наприклад, до 10 одночасно)
    private final ExecutorService notificationPool = Executors.newFixedThreadPool(10);

    /**
     * Constructor for NotificationScheduler.
     *
     * @param settingsService Settings service
     * @param infoService     Info service
     * @param bot             Currency bot
     */
    public NotificationScheduler(SettingsService settingsService, InfoService infoService, CurrencyBot bot) {
        this.settingsService = settingsService;
        this.infoService = infoService;
        this.bot = bot;
    }

    /**
     * Start the scheduler to check and send notifications.
     */
    public void start() {
        long initialDelay = computeInitialDelayMinutes();

        scheduler.scheduleAtFixedRate(
                this::checkAndNotifyUsers,
                initialDelay,
                60, // повторювати кожну годину
                TimeUnit.MINUTES
        );
    }

    private void checkAndNotifyUsers() {
        try {
            int currentHour = LocalTime.now().getHour();
            List<UserSettings> users = settingsService.getAllUserSettings();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            for (UserSettings user : users) {
                String notifyTime = user.getNotifyTime();
                //if (notifyTime.equals("")) {
                int userHour = LocalTime.parse(user.getNotifyTime(), formatter).getHour();
                if (userHour == currentHour) {
                    notificationPool.submit(() -> sendNotification(
                            user.getUserId(), infoService.getCurrencyInfo(user)));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void sendNotification(Long chatId, String message) {
        try {
            bot.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(message)
                    .build());
            System.out.println("✅ Sent to user: " + chatId);
        } catch (TelegramApiException e) {
            System.err.println("❌ Failed to send to " + chatId + ": " + e.getMessage());
        }
    }

    private long computeInitialDelayMinutes() {
        int minutes = LocalTime.now().getMinute();
        return 60 - minutes; // скільки хвилин до наступної повної години
    }

    /**
     * Shutdown the scheduler and notification pool.
     */
    public void shutdown() {
        scheduler.shutdown();
        notificationPool.shutdown();
    }
}
