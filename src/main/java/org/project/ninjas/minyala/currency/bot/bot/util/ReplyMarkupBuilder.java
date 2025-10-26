package org.project.ninjas.minyala.currency.bot.bot.util;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Utility class for building keyboard layouts.
 */
public class ReplyMarkupBuilder {
    /**
     *  Button BACK.
     */
    public static final String BACK = "BACK";

    /***/
    public static final String BACKTEXT = "НАЗАД";

    /**
     *  Button BACK to main menu.
     */
    public static final String BACKALL = "BACKALL";

    /***/
    public static final String BACKALLTEXT = "ГОЛОВНЕ МЕНЮ";

    /**
     *  Text for Button "НАСТРОЙКИ" to main menu.
     */
    public static final String SETTINGSTEXT = "⚙️ Налаштування";

    /**
     *  Text for Button "ГОЛОВНЕ МЕНЮ" to main menu.
     */
    public static final String MAINMENUTEXT = "Головне меню";

    /**
     *  Text for ExeptionButton.
     */
    public static final String EXEPTIONTEXT = "Немає такої команди";

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
                        List.of(btn(BACKTEXT, BACK))
                )
        );
    }

    /**
     * Builds the decimal menu With Choose reply markup.
     *
     * @param choose choose decimal
     * @return the decimal inline keyboard markup
     */
    public static InlineKeyboardMarkup decimalReplyMarkupWithChoose(String choose) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int decimal = 1; decimal < 4; decimal++) {
            String dec = String.valueOf(decimal);
            if (dec.contains(choose)) {
                row.add(btn(btnWithChoose(dec), dec));
            } else {
                row.add(btn(dec, dec));
            }
        }
        rows.add(row);
        rows.add(List.of(btn(BACKTEXT, BACK)));
        rows.add(List.of(btn(BACKALLTEXT, BACKALL)));
        return new InlineKeyboardMarkup(rows);
    }

    /**
     * Builds the bank menu reply markup With Choose.
     *
     * @param choose choose bank
     * @return the bank inline keyboard markup
     */
    public static InlineKeyboardMarkup bankReplyMarkupWithChoose(Bank choose) {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Bank bank : Bank.values()) {
            if (bank.equals(choose)) {
                rows.add(List.of(btn(btnWithChoose(bank.getDisplayName()), bank.name())));
            } else {
                rows.add(List.of(btn(bank.getDisplayName(), bank.name())));
            }
        }
        rows.add(List.of(btn(BACKTEXT, BACK)));
        rows.add(List.of(btn(BACKALLTEXT, BACKALL)));
        return new InlineKeyboardMarkup(rows);
    }

    /**
     * Builds the notify menu reply markup With Choose.
     *
     * @param choose - time notify
     * @return the notify inline keyboard markup
     */
    public static InlineKeyboardMarkup notifyReplyMarkup(int choose) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int hour = 9; hour < 19; hour++) {
            String time = String.format("%02d:00",hour);
            if (hour == choose) {
                if (row.size() < 3) {
                    row.add(btn(btnWithChoose(time), time));
                } else {
                    rows.add(row);
                    row = new ArrayList<>();
                    row.add(btn(btnWithChoose(time), time));
                }
            } else if (row.size() < 3) {
                row.add(btn(time, time));
            } else {
                rows.add(row);
                row = new ArrayList<>();
                row.add(btn(time, time));
            }
        }
        rows.add(row);
        rows.add(List.of(btn(BACKTEXT, BACK)));
        rows.add(List.of(btn(BACKALLTEXT, BACKALL)));

        return new InlineKeyboardMarkup(rows);
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
