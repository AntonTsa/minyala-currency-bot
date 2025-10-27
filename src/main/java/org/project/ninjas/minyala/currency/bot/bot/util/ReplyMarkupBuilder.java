package org.project.ninjas.minyala.currency.bot.bot.util;

import static org.project.ninjas.minyala.currency.bot.bot.util.ButtonNameLabelConstants.*;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Utility class for building keyboard layouts.
 */
public class ReplyMarkupBuilder {

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
                                        .text(TEXT_GET_INFO_BTN)
                                        .callbackData(DATA_GET_INFO_BTN)
                                        .build()),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(TEXT_SETTINGS_MENU)
                                        .callbackData(DATA_SETTINGS_MENU_BTN)
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
                        List.of(btn(TEXT_DECIMAL_SETTINGS_BTN, DATA_DECIMAL_SETTINGS_BTN)),
                        List.of(btn(TEXT_BANK_SETTINGS_BTN, DATA_BANK_SETTINGS_BTN)),
                        List.of(btn(TEXT_CURRENCY_SETTINGS_BTN, DATA_CURRENCY_SETTINGS_BTN)),
                        List.of(btn(TEXT_NOTIFY_SETTINGS_BTN, DATA_NOTIFY_SETTINGS_BTN)),
                        List.of(btn(TEXT_BACK_BTN, DATA_BACK_BTN))
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
        rows.add(List.of(btn(TEXT_BACK_BTN, DATA_BACK_BTN)));
        rows.add(List.of(btn(TEXT_BACK_MAIN_BTN, DATA_BACK_MAIN_MENU_BTN)));
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
        rows.add(List.of(btn(TEXT_BACK_BTN, DATA_BACK_BTN)));
        rows.add(List.of(btn(TEXT_BACK_MAIN_BTN, DATA_BACK_MAIN_MENU_BTN)));
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
        if (choose == 0) {
            rows.add(List.of(btn(btnWithChoose(TEXT_OFF_BTN), DATA_OFF_BTN)));
        } else {
            rows.add(List.of(btn(TEXT_OFF_BTN, DATA_OFF_BTN)));
        }
        rows.add(List.of(btn(TEXT_BACK_BTN, DATA_BACK_BTN)));
        rows.add(List.of(btn(TEXT_BACK_MAIN_BTN, DATA_BACK_MAIN_MENU_BTN)));

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
        return CHECKMARK + text;
    }
}
