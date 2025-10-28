package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BACK_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_BANK_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_CURRENCY_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_DECIMAL_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.DATA_NOTIFY_SETTINGS_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_BACK_MAIN_BTN;
import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.TEXT_EXCEPTION;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.bankReplyMarkupWithChoose;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.decimalReplyMarkupWithChoose;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.mainMenuReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.notifyReplyMarkup;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.settingsReplyMarkup;

import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *  Setting menu button handler.
 */
@RequiredArgsConstructor
public class HandleSettingsInvoker implements BotStateInvoker {
    private final SettingsService settingsService;

    @Override
    public BotState getInvokedState() {
        return BotState.HANDLE_SETTINGS;
    }

    @Override
    public BotResponse invoke(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        return switch (update.getCallbackQuery().getData()) {
            case DATA_DECIMAL_SETTINGS_BTN -> handleDecimalButton(chatId);
            case DATA_BANK_SETTINGS_BTN -> handleBankButton(chatId);
            case DATA_CURRENCY_SETTINGS_BTN -> handleCurrencyButton(chatId);
            case DATA_NOTIFY_SETTINGS_BTN -> handleNotifyButton(chatId);
            case DATA_BACK_BTN -> handleBackButton(chatId);
            default -> handleExceptionalCases(chatId);
        };
    }

    /**
     * Create a reply to action pressing decimal button.
     *
     * @param chatId user's chat id
     * @return correspondent bot response
     */
    private BotResponse handleDecimalButton(long chatId) {
        return new BotResponse(
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Оберіть кількість знаків після коми")
                        .replyMarkup(
                                decimalReplyMarkupWithChoose(String
                                        .valueOf(settingsService
                                        .getUsersSettings(chatId)
                                        .getDecimalPlaces()
                                        )
                                )
                        )
                        .build(),
                BotState.HANDLE_DECIMAL_CHOICE
        );
    }

    /**
     * Create a reply to action pressing bank button.
     *
     * @param chatId user's chat id
     * @return correspondent bot response
     */
    private BotResponse handleBankButton(long chatId) {
        return new BotResponse(
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Оберіть банк")
                        .replyMarkup(bankReplyMarkupWithChoose(settingsService
                                                .getUsersSettings(chatId)
                                                .getBanks()
                                )
                        )
                        .build(),
                BotState.BANK_CHOICE
        );
    }

    /**
     * Create a reply to action pressing currency button.
     *
     * @param chatId user's chat id
     * @return correspondent bot response
     */
    private BotResponse handleCurrencyButton(long chatId) {
        // Delegate to the existing invoker logic
        HandleCurrencyChoiceInvoker currencyHandler = new HandleCurrencyChoiceInvoker(settingsService);
        return currencyHandler.invokeFromParent(chatId);
    }

    /**
     * Create a reply to action pressing notify button.
     *
     * @param chatId user's chat id
     * @return correspondent bot response
     */
    private BotResponse handleNotifyButton(long chatId) {
        return new BotResponse(
                SendMessage.builder()
                        .chatId(chatId)
                        .text("Оберіть час сповіщень")
                        .replyMarkup(notifyReplyMarkup(Integer
                                        .parseInt(settingsService
                                                .getUsersSettings(chatId)
                                                .getNotifyTime()
                                                .split(":")[0])
                                )
                        )
                        .build(),
                BotState.NOTIFY_CHOICE
        );
    }

    /**
     * Create a reply to action back to previous menu.
     *
     * @param chatId user's chat id
     * @return correspondent bot response
     */
    private BotResponse handleBackButton(long chatId) {
        return new BotResponse(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(TEXT_BACK_MAIN_BTN)
                        .replyMarkup(mainMenuReplyMarkup())
                        .build(),
                BotState.HANDLE_MAIN_MENU
        );
    }

    /**
     * Create a reply to action not defined by bot logic.
     *
     * @param chatId user's chat id
     * @return correspondent bot response
     */
    private BotResponse handleExceptionalCases(long chatId) {
        return new BotResponse(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(TEXT_EXCEPTION)
                        .replyMarkup(settingsReplyMarkup())
                        .build(),
                this.getInvokedState()
        );
    }
}
