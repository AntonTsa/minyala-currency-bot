package org.project.ninjas.minyala.currency.bot.bot;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.service.InvokersService;
import org.project.ninjas.minyala.currency.bot.bot.service.UserStateService;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateInvoker;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Controller that manages bot.
 */
@RequiredArgsConstructor
public class BotController {
    private final UserStateService userStateService;
    private final InvokersService invokersService;

    /**
     * Method for user update management. Firstly, it gets id of chat, then by the id
     * it gets current state and handle the state, gets response from handler, set new
     * state by the id and returns response.
     *
     * @param update user action
     * @return reply
     */
    public SendMessage handleUpdate(Update update) {
        Long userId;

        if (update.hasMessage()) {
            userId = update.getMessage().getFrom().getId();
        } else {
            userId = update.getCallbackQuery().getFrom().getId();
        }

        BotState currentState = userStateService.getUserState(update);

        BotStateInvoker invoker = invokersService.process(currentState);

        BotResponse response = invoker.invoke(update);

        userStateService.setUserState(userId, response.nextState());

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
