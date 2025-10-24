package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.BANK_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.util.Constants.Banks.*;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.*;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;


/***/
@RequiredArgsConstructor
public class HandleBankInvoker implements BotStateInvoker {

    private final SettingsService settingsService;

    @Override
    public BotState getInvokedState() {
        return BotState.BANK_CHOICE;
    }

    @Override
    public BotResponse invoke(Update update) {
        Long chatId = update.getCallbackQuery().getFrom().getId();
        UserSettings userSettings = settingsService.getUsersSettings(chatId);
        String data = update.getCallbackQuery().getData();
        SendMessage msg = new SendMessage();

        switch (data) {
            case "ПриватБанк":
                userSettings.setBank(PRIVAT.getDisplayName());
                settingsService.saveUserSettings(userSettings);

                InlineKeyboardMarkup markup1 = bankReplyMarkupWithChoose(
                        btnWithChoose(PRIVAT.getDisplayName()), MONO.getDisplayName(), NBU.getDisplayName());

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(PRIVAT.getDisplayName())
                        .replyMarkup(markup1)
                        .build();

                return new BotResponse(msg, BANK_CHOICE);

            case "МоноБанк":
                userSettings.setBank(MONO.getDisplayName());
                settingsService.saveUserSettings(userSettings);

                InlineKeyboardMarkup markup2 = bankReplyMarkupWithChoose(
                        PRIVAT.getDisplayName(), btnWithChoose(MONO.getDisplayName()), NBU.getDisplayName());

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(MONO.getDisplayName())
                        .replyMarkup(markup2)
                        .build();

                return new BotResponse(msg, BANK_CHOICE);

            case "НБУ":
                userSettings.setBank(NBU.getDisplayName());
                settingsService.saveUserSettings(userSettings);

                InlineKeyboardMarkup markup3 = bankReplyMarkupWithChoose(
                        PRIVAT.getDisplayName(), MONO.getDisplayName(), btnWithChoose(NBU.getDisplayName()));

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(NBU.getDisplayName())
                        .replyMarkup(markup3)
                        .build();

                return new BotResponse(msg, BANK_CHOICE);

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
