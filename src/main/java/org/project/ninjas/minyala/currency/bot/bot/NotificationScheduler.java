package org.project.ninjas.minyala.currency.bot.bot;

import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;

public class NotificationScheduler extends Thread {

    private final CurrencyBot bot;

    public NotificationScheduler(CurrencyBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        while (true) {
            try {
                LocalTime now = LocalTime.now().withSecond(0).withNano(0);
                for (UserSettings user : bot.getUsers()) {
                    if (user.isNotificationsEnabled() &&
                        now.equals(LocalTime.parse(user.getNotifyTime()))) {
                        sendNotification(user);
                    }
                }
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendNotification(UserSettings user) {
        SendMessage message = new SendMessage();
        message.setChatId(user.getChatId().toString());
        message.setText("Ваше щоденне повідомлення!");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

