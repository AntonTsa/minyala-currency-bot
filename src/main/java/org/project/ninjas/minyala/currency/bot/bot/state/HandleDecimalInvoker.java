package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_DECIMAL_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.btnWithChoose;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.decimalReplyMarkupWithChoose;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.mainMenuReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.settingsReplyMarkup;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

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
        Long chatId = update.getCallbackQuery().getFrom().getId();
        UserSettings userSettings = settingsService.getUsersSettings(chatId);
        String data = update.getCallbackQuery().getData();
        SendMessage msg = new SendMessage();

        switch (data) {
            case ONE:
                userSettings.setDecimalPlaces(1);
                settingsService.saveUserSettings(userSettings);

                InlineKeyboardMarkup markup1 = decimalReplyMarkupWithChoose(
                        btnWithChoose(ONE), TWO, THREE);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(ONE)
                        .replyMarkup(markup1)
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case TWO:
                userSettings.setDecimalPlaces(2);
                settingsService.saveUserSettings(userSettings);

                InlineKeyboardMarkup markup2 = decimalReplyMarkupWithChoose(
                        ONE, btnWithChoose(TWO), THREE);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(TWO)
                        .replyMarkup(markup2)
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case THREE:
                userSettings.setDecimalPlaces(3);
                settingsService.saveUserSettings(userSettings);

                InlineKeyboardMarkup markup3 = decimalReplyMarkupWithChoose(
                        ONE, TWO, btnWithChoose(THREE));

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(THREE)
                        .replyMarkup(markup3)
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case ReplyMarkupBuilder.BACK:
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text("Налаштування")
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_SETTINGS);

            case ReplyMarkupBuilder.BACKALL:
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text("Головне меню")
                        .replyMarkup(mainMenuReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_MAIN_MENU);

            default:
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Немає такої команди")
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, this.getInvokedState());
        }
    }
}
