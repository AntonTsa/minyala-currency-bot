package org.project.ninjas.minyala.currency.bot.bot.state;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.bot.util.Utils;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_DECIMAL_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.*;

@RequiredArgsConstructor
public class HandleDecimalInvoker implements BotStateInvoker {
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
            case "1":
                userSettings.setDecimalPlaces(1);
                settingsService.saveUserSettings(userSettings);
                System.out.println("userSettings = " + userSettings);
                System.out.println("settingsService = " + settingsService);

                InlineKeyboardMarkup markup1 = new InlineKeyboardMarkup(List.of(
                        List.of(
                                Utils.btn("✅1", "1"),
                                Utils.btn("  2", "2"),
                                Utils.btn("  3", "3")),
                        List.of(Utils.btn("НАЗАД", "BACK")),
                        List.of(Utils.btn("ГОЛОВНЕ МЕНЮ", "BACKALL"))
                ));

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("1")
                        .replyMarkup(markup1)
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case "2":
                userSettings.setDecimalPlaces(2);
                settingsService.saveUserSettings(userSettings);

                InlineKeyboardMarkup markup2 = new InlineKeyboardMarkup(List.of(
                        List.of(
                                Utils.btn("  1", "1"),
                                Utils.btn("✅2", "2"),
                                Utils.btn("  3", "3")),
                        List.of(Utils.btn("НАЗАД", "BACK")),
                        List.of(Utils.btn("ГОЛОВНЕ МЕНЮ", "BACKALL"))
                ));
                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("2")
                        .replyMarkup(markup2)
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case "3":
                userSettings.setDecimalPlaces(1);
                settingsService.saveUserSettings(userSettings);

                InlineKeyboardMarkup markup3 = new InlineKeyboardMarkup(List.of(
                        List.of(
                                Utils.btn("  1", "1"),
                                Utils.btn("  2", "2"),
                                Utils.btn("✅3", "3")),
                        List.of(Utils.btn("НАЗАД", "BACK")),
                        List.of(Utils.btn("ГОЛОВНЕ МЕНЮ", "BACKALL"))
                ));
                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("3")
                        .replyMarkup(markup3)
                        .build();

                return new BotResponse(msg, BotState.HANDLE_DECIMAL_CHOICE);

            case "BACK":
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text("Налаштування")
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_SETTINGS);

            case "BACKALL":
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
