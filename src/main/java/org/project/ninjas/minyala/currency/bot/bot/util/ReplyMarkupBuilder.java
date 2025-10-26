package org.project.ninjas.minyala.currency.bot.bot.util;

import static org.project.ninjas.minyala.currency.bot.bot.util.Constants.Banks.MONO;
import static org.project.ninjas.minyala.currency.bot.bot.util.Constants.Banks.NBU;
import static org.project.ninjas.minyala.currency.bot.bot.util.Constants.Banks.PRIVAT;
import static org.project.ninjas.minyala.currency.bot.bot.util.Constants.Decimal.ONE;
import static org.project.ninjas.minyala.currency.bot.bot.util.Constants.Decimal.THREE;
import static org.project.ninjas.minyala.currency.bot.bot.util.Constants.Decimal.TWO;

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
     * Builds the decimal menu reply markup.
     *
     * @return the decimal inline keyboard markup
     */
    public static InlineKeyboardMarkup decimalReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(btn(ONE.getDisplayName(), ONE.getDisplayName()),
                                btn(btnWithChoose(TWO.getDisplayName()), TWO.getDisplayName()),
                                btn(THREE.getDisplayName(), THREE.getDisplayName())),
                        List.of(btn(BACKTEXT, BACK)),
                        List.of(btn(BACKALLTEXT, BACKALL))
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
                        List.of(btn(btn1, ONE.getDisplayName()),
                                btn(btn2, TWO.getDisplayName()),
                                btn(btn3, THREE.getDisplayName())),
                        List.of(btn(BACKTEXT, BACK)),
                        List.of(btn(BACKALLTEXT, BACKALL))
                ));
    }

    /**
     * Builds the bank menu reply markup.
     *
     * @return the bank inline keyboard markup
     */
    public static InlineKeyboardMarkup bankReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(List.of(btn(btnWithChoose(PRIVAT.getDisplayName()), PRIVAT.getDisplayName())),
                        List.of(btn(MONO.getDisplayName(), MONO.getDisplayName())),
                        List.of(btn(NBU.getDisplayName(), NBU.getDisplayName())),
                        List.of(btn(BACKTEXT, BACK)),
                        List.of(btn(BACKALLTEXT, BACKALL))
                ));
    }

    /**
     * Builds the bank menu reply markup With Choose.
     *
     * @param btn1 first button
     * @param btn2 second button
     * @param btn3 third button
     * @return the bank inline keyboard markup
     */
    public static InlineKeyboardMarkup bankReplyMarkupWithChoose(
            String btn1,
            String btn2,
            String btn3) {
        return new InlineKeyboardMarkup(
                List.of(List.of(btn(btn1, PRIVAT.getDisplayName())),
                        List.of(btn(btn2, MONO.getDisplayName())),
                        List.of(btn(btn3, NBU.getDisplayName())),
                        List.of(btn(BACKTEXT, BACK)),
                        List.of(btn(BACKALLTEXT, BACKALL))
                ));
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
