package org.project.ninjas.minyala.currency.bot.bot;

import java.util.List;

import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * A class that represents a bot itself.
 */
public class CurrencyBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyBot.class);
    /**
     * Bot Username.
     */
    private final String botUsername;
    /**
     * Bot Controller.
     */
    private final BotController botController;

    /**
     * Constructor with params.
     *
     * @param botToken - token, got from.env
     * @param botUsername - username, got from .env
     * @param botController - controller for handling updates
     */
    public CurrencyBot(
            String botToken,
            String botUsername,
            BotController botController
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.botController = botController;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() || update.hasCallbackQuery()) {
            try {
                execute(botController.handleUpdate(update));
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        } else {
            LOGGER.error("Update has neither message nor callback query");
        }
    }
}


@Override
    public void onUpdateReceived(Update update) {
        try {
            // Додаємо користувача у список
            if (update.hasMessage() && update.getMessage().hasText()) {
                Long chatId = update.getMessage().getChatId();
                users.computeIfAbsent(chatId, id -> new UserSettings(id));
            }

            execute(botController.handleUpdate(update));

        } catch (Exception e) {
            LOGGER.error("Помилка при обробці оновлення: " + e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return super.ggetBotToken();
    }

    public List<UserSettings> getUsers() {
        return List.copyOf(users.values());
    }
}