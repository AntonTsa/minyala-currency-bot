package org.project.ninjas.minyala.currency.bot;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import org.project.ninjas.minyala.currency.bot.bot.BotController;
import org.project.ninjas.minyala.currency.bot.bot.CurrencyBot;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateContext;
import org.project.ninjas.minyala.currency.bot.bot.state.MainMenuStateHandler;
import org.project.ninjas.minyala.currency.bot.bot.state.StartStateHandler;
import org.project.ninjas.minyala.currency.bot.bot.state.UserStateService;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public final class AppLauncher {

    /**
     * Logger for AppLauncher.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AppLauncher.class);

    private AppLauncher() {
        // AppLauncher serves as starter util class
    }

    /**
     * Main method that launches app.
     * @param args - args
     */
    public static void main(final String[] args) {
        Dotenv dotenv = Dotenv.load();

        String botToken = dotenv.get("BOT_TOKEN");
        String botUsername = dotenv.get("BOT_USERNAME");

        if (botToken == null || botUsername == null) {
            LOGGER.error("BOT_TOKEN or BOT_USERNAME missing in .env");
            return;
        }

        SettingsService settingsService = new SettingsService();

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(
                    DefaultBotSession.class
            );
            botsApi.registerBot(
                    new CurrencyBot(
                            botToken,
                            botUsername,
                            new BotController(
                                    new UserStateService(),
                                    new BotStateContext(
                                            List.of(
                                                    new StartStateHandler(
                                                            settingsService
                                                    ),
                                                    new MainMenuStateHandler()
                                            )
                                    )
                            )
                    )
            );
        } catch (TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("Bot successfully loaded");
    }
}
