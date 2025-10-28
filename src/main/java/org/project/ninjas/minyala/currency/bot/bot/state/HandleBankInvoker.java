package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.BANK_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_MAIN_MENU_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_BANK_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_EXCEPTION;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_SETTINGS_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.bankReplyMarkupWithChoose;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.mainMenuReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.settingsReplyMarkup;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.util.Bank;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;



/**
 *  Bank menu button handler.
 */
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
        String data = update.getCallbackQuery().getData();
        SendMessage msg = new SendMessage();

        switch (data) {
            case "PRIVAT" -> {
                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(TEXT_BANK_SETTINGS_BTN)
                        .replyMarkup(bankReplyMarkupWithChoose(
                                saveToUserSettings(Bank.PRIVAT.getDisplayName(), chatId)))
                        .build();
                return new BotResponse(msg, BANK_CHOICE);
            }
            case "MONO" -> {
                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(TEXT_BANK_SETTINGS_BTN)
                        .replyMarkup(bankReplyMarkupWithChoose(
                                saveToUserSettings(Bank.MONO.getDisplayName(), chatId)))
                        .build();
                return new BotResponse(msg, BANK_CHOICE);
            }
            case "NBU" -> {
                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(TEXT_BANK_SETTINGS_BTN)
                        .replyMarkup(bankReplyMarkupWithChoose(
                                saveToUserSettings(Bank.NBU.getDisplayName(), chatId)))
                        .build();
                return new BotResponse(msg, BANK_CHOICE);
            }
            case DATA_BACK_BTN -> {
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(TEXT_SETTINGS_MENU)
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_SETTINGS);
            }
            case DATA_BACK_MAIN_MENU_BTN -> {
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(TEXT_MAIN_MENU)
                        .replyMarkup(mainMenuReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_MAIN_MENU);
            }
            default -> {
                SendMessage.builder()
                        .chatId(chatId)
                        .text(TEXT_EXCEPTION)
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, this.getInvokedState());
            }
        }
    }

    /**
     * Save bank after user's choose to user's settings.
     *
     * @param bank choose bank
     * @param chatId chatId
     * @return the bank's list
     */
    private List<String> saveToUserSettings(String bank, Long chatId) {
        UserSettings userSettings = settingsService.getUsersSettings(chatId);

        List<String> banks = userSettings.getBanks();

        if (banks.contains(bank)) {
            banks.remove(bank);
        } else {
            banks.add(bank);
        }
        if (banks.isEmpty()) {
            banks.add(Bank.PRIVAT.getDisplayName());
        }
        settingsService.saveUserSettings(userSettings);
        return banks;
    }
}
