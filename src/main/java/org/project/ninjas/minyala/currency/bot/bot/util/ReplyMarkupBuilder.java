package org.project.ninjas.minyala.currency.bot.bot.util;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Utility class for building keyboard layouts.
 */
@RequiredArgsConstructor
public class ReplyMarkupBuilder {
   private final SettingsService set;
    /**
     *  Button BACK.
     */
    public static final String BACK = "BACK";
    /**
     *  Button BACK to main menu.
     */
    public static final String BACKALL = "BACKALL";

    private ReplyMarkupBuilder() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Builds the main menu reply markup.
     *
     * @return the main menu inline keyboard markup
     */
    public static InlineKeyboardMarkup mainMenuReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(List.of(
                                InlineKeyboardButton.builder()
                                        .text("Отримати інформацію")
                                        .callbackData("CURRENT_INFO_BTN")
                                        .build()),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("Змінити налаштування")
                                        .callbackData("SETTINGS_BTN")
                                        .build()
                        ))
        );
    }

    /**
     * Builds the settings menu reply markup.
     *
     * @return the settings inline keyboard markup
     */
    public static InlineKeyboardMarkup settingsReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(btn("Кількість знаків після коми", "DECIMAL_CHOICE")),
                        List.of(btn("Банк", "BANK_CHOICE")),
                        List.of(btn("Валюти", "CURRENCY_CHOICE")),
                        List.of(btn("Час оповіщення", "NOTIFY_CHOICE")),
                        List.of(btn("Назад", BACK))
                )
        );
    }

    /**
     * Builds the decimal menu reply markup.
     *
     * @return the decimal inline keyboard markup
     */
    public static InlineKeyboardMarkup decimalReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(btn("  1", "1"),
                                btn("✅2", "2"),
                                btn("  3", "3")),
                        List.of(btn("НАЗАД", BACK)),
                        List.of(btn("ГОЛОВНЕ МЕНЮ", BACKALL))
                ));
    }

    /**
     * Builds the decimal menu With Choose reply markup.
     *
     * @param btn1 first button
     * @param btn2 second button
     * @param btn3 third button
     * @return the decimal inline keyboard markup
     */
    public static InlineKeyboardMarkup decimalReplyMarkupWithChoose(
            String btn1,
            String btn2,
            String btn3) {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(btn(btn1, "1"),
                                btn(btn2, "2"),
                                btn(btn3, "3")),
                        List.of(btn("НАЗАД", BACK)),
                        List.of(btn("ГОЛОВНЕ МЕНЮ", BACKALL))
                ));
    }

    /**
     * Builds the bank menu reply markup.
     *
     * @return the bank inline keyboard markup
     */
    public static InlineKeyboardMarkup bankReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(btn("НАЗАД", BACK)),
                        List.of(btn("ГОЛОВНЕ МЕНЮ", BACKALL))
                ));
    }

    /**
     * Builds the currency menu reply markup.
     *
     * @return the currency inline keyboard markup
     */
    public static InlineKeyboardMarkup notifyReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(btn("НАЗАД", BACK)),
                        List.of(btn("ГОЛОВНЕ МЕНЮ", BACKALL))
                ));
    }

    /**
     * Maker buttons by text and data.
     *
     * @param text - text on the button
     * @param data - button's data
     *
     * @return done button.
     */
    public static InlineKeyboardButton btn(String text, String data) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(data);
        return button;
    }

    /**
     * Make buttons with ✅ .
     *
     * @param text - text on the button
     *
     * @return new text button.
     */
    public static String btnWithChoose(String text) {
        return "✅ " + text;
    }
}
