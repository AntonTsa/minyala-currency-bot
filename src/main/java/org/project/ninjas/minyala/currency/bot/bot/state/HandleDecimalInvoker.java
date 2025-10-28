package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_DECIMAL_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_MAIN_MENU_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_DECIMAL_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_EXCEPTION;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_SETTINGS_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.decimalReplyMarkupWithChoose;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.mainMenuReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.settingsReplyMarkup;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *  Decimal menu button handler.
 */
@RequiredArgsConstructor
public class HandleDecimalInvoker implements BotStateInvoker {
    /**
     *  Button 1.
     */
    public static final String ONE = "1";
    /**
     *  Button 2.
     */
    public static final String TWO = "2";
    /**
     *  Button 3.
     */
    public static final String THREE = "3";

    private final SettingsService settingsService;

    @Override
    public BotState getInvokedState() {
        return HANDLE_DECIMAL_CHOICE;
    }

    @Override
    public BotResponse invoke(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        UserSettings userSettings = settingsService.getUsersSettings(chatId);
        String data = update.getCallbackQuery().getData();
        SendMessage msg = new SendMessage();

        switch (data) {
            case ONE:
                userSettings.setDecimalPlaces(1);
                settingsService.saveUserSettings(userSettings);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(TEXT_DECIMAL_SETTINGS_BTN)
                        .replyMarkup(decimalReplyMarkupWithChoose(ONE))
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case TWO:
                userSettings.setDecimalPlaces(2);
                settingsService.saveUserSettings(userSettings);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(TEXT_DECIMAL_SETTINGS_BTN)
                        .replyMarkup(decimalReplyMarkupWithChoose(TWO))
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case THREE:
                userSettings.setDecimalPlaces(3);
                settingsService.saveUserSettings(userSettings);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(TEXT_DECIMAL_SETTINGS_BTN)
                        .replyMarkup(decimalReplyMarkupWithChoose(THREE))
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case DATA_BACK_BTN:
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(TEXT_SETTINGS_MENU)
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_SETTINGS);

            case DATA_BACK_MAIN_MENU_BTN:
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(TEXT_MAIN_MENU)
                        .replyMarkup(mainMenuReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_MAIN_MENU);

            default:
                SendMessage.builder()
                        .chatId(chatId)
                        .text(TEXT_EXCEPTION)
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, this.getInvokedState());
        }
    }
}
