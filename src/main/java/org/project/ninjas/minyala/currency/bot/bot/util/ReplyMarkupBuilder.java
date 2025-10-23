package org.project.ninjas.minyala.currency.bot.bot.util;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Utility class for building keyboard layouts.
 */
public class ReplyMarkupBuilder {
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
                List.of(List.of(
                                InlineKeyboardButton.builder()
                                        .text("Кількість знаків після коми")
                                        .callbackData("DECIMAL_CHOICE")
                                        .build()),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("Банк")
                                        .callbackData("BANK_CHOICE")
                                        .build()),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("Валюти")
                                        .callbackData("CURRENCY_CHOICE")
                                        .build()),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("Час оповіщення")
                                        .callbackData("NOTIFY_CHOICE")
                                        .build()),
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("Назад")
                                        .callbackData("BACK")
                                        .build())
                )
        );
    }

    public static InlineKeyboardMarkup decimalReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(
                                Utils.btn("  1", "1"),
                                Utils.btn("✅2", "2"),
                                Utils.btn("  3", "3")),
                        List.of(Utils.btn("НАЗАД", "BACK")),
                        List.of(Utils.btn("ГОЛОВНЕ МЕНЮ", "BACKALL"))

                ));
    }

    public static InlineKeyboardMarkup bankReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(Utils.btn("НАЗАД", "BACK")),
                        List.of(Utils.btn("ГОЛОВНЕ МЕНЮ", "BACKALL"))
                ));
    }

    public static InlineKeyboardMarkup currencyReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(Utils.btn("НАЗАД", "BACK")),
                        List.of(Utils.btn("ГОЛОВНЕ МЕНЮ", "BACKALL"))
                ));
    }

    public static InlineKeyboardMarkup notifyReplyMarkup() {
        return new InlineKeyboardMarkup(
                List.of(
                        List.of(Utils.btn("НАЗАД", "BACK")),
                        List.of(Utils.btn("ГОЛОВНЕ МЕНЮ", "BACKALL"))
                ));
    }
}
