package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_SETTINGS;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_GET_INFO_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_SETTINGS_MENU_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_EXCEPTION;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_SETTINGS_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.mainMenuReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.settingsReplyMarkup;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.service.InfoService;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Handler for Main Menu buttons.
 */

@RequiredArgsConstructor
public class HandleMainMenuInvoker implements BotStateInvoker {
    private final SettingsService settingsService;
    private final InfoService infoService;

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
            case DATA_SETTINGS_MENU_BTN -> handleSettingsButton(chatId);
            case DATA_GET_INFO_BTN -> handleCurrentInfoButton(chatId);
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
                        .text(TEXT_EXCEPTION)
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
    private BotResponse handleCurrentInfoButton(long chatId) {
        UserSettings userSettings = settingsService.getUsersSettings(chatId);

        if (userSettings == null) {
            settingsService.createUserSettings(chatId);
            userSettings = settingsService.getUsersSettings(chatId);
        }
        String text = infoService.getCurrencyInfo(userSettings);

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
                .text(TEXT_SETTINGS_MENU)
                .replyMarkup(settingsReplyMarkup())
                .build();
        return new BotResponse(message, HANDLE_SETTINGS);
    }
}
