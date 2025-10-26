package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.BANK_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.*;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.util.Bank;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


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
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        UserSettings userSettings = settingsService.getUsersSettings(chatId);
        String data = update.getCallbackQuery().getData();
        SendMessage msg = new SendMessage();

        switch (data) {
            case "PRIVAT":
                userSettings.setBank(Bank.PRIVAT);
                settingsService.saveUserSettings(userSettings);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Bank.PRIVAT.getDisplayName())
                        .replyMarkup(bankReplyMarkupWithChoose(Bank.PRIVAT))
                        .build();

                return new BotResponse(msg, BANK_CHOICE);

            case "MONO":
                userSettings.setBank(Bank.MONO);
                settingsService.saveUserSettings(userSettings);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Bank.MONO.getDisplayName())
                        .replyMarkup(bankReplyMarkupWithChoose(Bank.MONO))
                        .build();

                return new BotResponse(msg, BANK_CHOICE);

            case "NBU":
                userSettings.setBank(Bank.NBU);
                settingsService.saveUserSettings(userSettings);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(Bank.NBU.getDisplayName())
                        .replyMarkup(bankReplyMarkupWithChoose(Bank.NBU))
                        .build();

                return new BotResponse(msg, BANK_CHOICE);

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
