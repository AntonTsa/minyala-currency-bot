package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.NOTIFY_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.BACK;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.BACKALL;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.BACKALLTEXT;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.EXEPTIONTEXT;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.mainMenuReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.notifyReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.settingsReplyMarkup;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/***/
@RequiredArgsConstructor
public class HandleNotifyInvoker implements BotStateInvoker {

    private final SettingsService settingsService;

    @Override
    public BotState getInvokedState() {
        return NOTIFY_CHOICE;
    }

    @Override
    public BotResponse invoke(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        UserSettings userSettings = settingsService.getUsersSettings(chatId);
        String data = update.getCallbackQuery().getData();
        SendMessage msg = new SendMessage();

        switch (data) {
            case "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00" -> {

                userSettings.setNotifyTime(data);
                settingsService.saveUserSettings(userSettings);

                msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Час:")
                        .replyMarkup(notifyReplyMarkup(Integer.parseInt(data.split(":")[0])))
                        .build();
                return new BotResponse(msg, NOTIFY_CHOICE);
            }

            case BACK -> {
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(BACKALL)
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_SETTINGS);
            }

            case BACKALL -> {
                msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(BACKALLTEXT)
                        .replyMarkup(mainMenuReplyMarkup())
                        .build();
                return new BotResponse(msg, BotState.HANDLE_MAIN_MENU);
            }

            default -> {
                SendMessage.builder()
                        .chatId(chatId)
                        .text(EXEPTIONTEXT)
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                return new BotResponse(msg, this.getInvokedState());
            }
        }
    }
}
