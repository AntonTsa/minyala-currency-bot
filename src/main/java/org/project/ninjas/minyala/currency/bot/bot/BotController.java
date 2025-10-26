package org.project.ninjas.minyala.currency.bot.bot;

import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateContext;
import org.project.ninjas.minyala.currency.bot.bot.state.UserStateService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotController {
    private final UserStateService userStateService;
    private final BotStateContext botStateContext;

    public BotController(UserStateService userStateService,
                         BotStateContext botStateContext) {
        this.userStateService = userStateService;
        this.botStateContext = botStateContext;
    }

    public SendMessage handleUpdate(Update update) {
        Long chatId = update.getMessage().getChatId();
        BotState currentState = userStateService.getUserState(chatId);

        BotResponse response = botStateContext.process(currentState, update);

        // зберігаємо новий стан користувача
        userStateService.setUserState(chatId, response.nextState());

        // відправляємо повідомлення
        return response.message();
    }
}


public class BotController {

    private final Map<Long, UserSettings> users;

    public BotController(Map<Long, UserSettings> users) {
        this.users = users;
    }

    public SendMessage handleUpdate(Update update) {
        Long chatId = update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();

        UserSettings user = users.get(chatId);

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (text.equalsIgnoreCase("/start")) {
                return sendMessageWithKeyboard(chatId,
                        "Вітаю! Оберіть час сповіщень або вимкніть їх:", createKeyboard());
            }
        }

        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            switch (data) {
                case "off" -> user.setNotificationsEnabled(false);
                case "on" -> user.setNotificationsEnabled(true);
                default -> {
                    if (data.matches("\\d{1,2}:00")) {
                        user.setNotifyTime(data);
                        user.setNotificationsEnabled(true);
                    }
                }
            }
            return new SendMessage(chatId.toString(), "Налаштування оновлено ✅");
        }

        return new SendMessage(chatId.toString(), "Невідома команда. Спробуйте /start");
    }

    private SendMessage sendMessageWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        return message;
    }

    private InlineKeyboardMarkup createKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 9; i <= 18; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(i + ":00");
            button.setCallbackData(i + ":00");
            rows.add(List.of(button));
        }

        InlineKeyboardButton onButton = new InlineKeyboardButton();
        onButton.setText("Увімкнути");
        onButton.setCallbackData("on");

        InlineKeyboardButton offButton = new InlineKeyboardButton();
        offButton.setText("Вимкнути");
        offButton.setCallbackData("off");

        rows.add(List.of(onButton, offButton));
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
