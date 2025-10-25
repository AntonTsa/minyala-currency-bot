package org.project.ninjas.minyala.currency.bot.bot.state;

import static java.awt.SystemColor.text;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_SETTINGS;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.mainMenuReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.settingsReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.mainMenuReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_SETTINGS;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.service.BankAggregatorService;
import org.project.ninjas.minyala.currency.bot.banks.service.BankRateService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.BankAggregatorServiceImpl;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.MonobankService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.NbuService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.PrivatBankService;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for Main Menu buttons.
 */

@RequiredArgsConstructor
public class HandleMainMenuInvoker implements BotStateInvoker {
    private final SettingsService settingsService;

    @Override
    public BotState getInvokedState() {
        return HANDLE_MAIN_MENU;
    }

    @Override
    public BotResponse invoke(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (settingsService.getUsersSettings(chatId) == null) {
            settingsService.createUserSettings(chatId);
        }
        return switch (update.getCallbackQuery().getData()) {
            case "SETTINGS_BTN" -> handleSettingsButton(chatId);
            case "CURRENT_INFO_BTN" -> handleCurrentInfoButton(chatId);

            default -> handleExceptionalCases(chatId);
        };
    }

    /**
     * Create a reply to action not defined by bot logic.
     *
     * @param chatId user's chat id
     * @return correspondent bot response
     */
    private BotResponse handleExceptionalCases(Long chatId) {
        return new BotResponse(
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Немає такої команди")
                        .replyMarkup(mainMenuReplyMarkup())
                        .build(),
                this.getInvokedState()
        );
    }

    /**
     * Create a reply if button "Отримати інформацію" pressed.
     *
     * @param chatId user's chat id
     * @return correspondent bot response
     */
    private BotResponse  handleCurrentInfoButton(long chatId) {
        UserSettings userSettings = settingsService.getUsersSettings(chatId);

        if (userSettings == null) {
            settingsService.createUserSettings(chatId);
            userSettings = settingsService.getUsersSettings(chatId);
        }
        String text = HandleGetInfo.getCurrencyInfo(userSettings);

        return new BotResponse(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(text)
                        .replyMarkup(mainMenuReplyMarkup())
                        .build(),
                this.getInvokedState()
        );
    }

    /**
     * Create a reply if button "Змінити налаштування" pressed.
     *
     * @param chatId user's chat id
     * @return correspondent bot response
     */
    private BotResponse handleSettingsButton(long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Налаштування")
                .replyMarkup(settingsReplyMarkup())
                .build();
        return new BotResponse(message, HANDLE_SETTINGS);
    }
}
