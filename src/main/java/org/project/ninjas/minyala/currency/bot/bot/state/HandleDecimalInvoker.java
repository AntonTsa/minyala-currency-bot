package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_DECIMAL_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.*;

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
                        .text(ONE)
                        .replyMarkup(decimalReplyMarkupWithChoose(ONE))
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case TWO:
                userSettings.setDecimalPlaces(2);
                settingsService.saveUserSettings(userSettings);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(TWO)
                        .replyMarkup(decimalReplyMarkupWithChoose(TWO))
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case THREE:
                userSettings.setDecimalPlaces(3);
                settingsService.saveUserSettings(userSettings);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(THREE)
                        .replyMarkup(decimalReplyMarkupWithChoose(THREE))
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case BACK:
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(SETTINGSTEXT)
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_SETTINGS);

            case BACKALL:
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(MAINMENUTEXT)
                        .replyMarkup(mainMenuReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_MAIN_MENU);

            default:
                SendMessage.builder()
                        .chatId(chatId)
                        .text(EXEPTIONTEXT)
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, this.getInvokedState());
        }
    }
}
