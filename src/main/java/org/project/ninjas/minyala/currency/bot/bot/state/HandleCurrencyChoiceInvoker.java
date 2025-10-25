package org.project.ninjas.minyala.currency.bot.bot.state;

import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.CURRENCY_CHOICE;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_MAIN_MENU;
import static org.project.ninjas.minyala.currency.bot.bot.state.BotState.HANDLE_SETTINGS;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.btn;
import static org.project.ninjas.minyala.currency.bot.bot.util.ReplyMarkupBuilder.settingsReplyMarkup;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.ninjas.minyala.currency.bot.bot.BotResponse;
import org.project.ninjas.minyala.currency.bot.settings.SettingsService;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Клас обробника стану {@link BotState#CURRENCY_CHOICE}.
 * Використовується для керування вибором валют користувача та
 * повернення до головного меню налаштувань.
 */
@RequiredArgsConstructor
public class HandleCurrencyChoiceInvoker implements BotStateInvoker {

    /** Сервіс для збереження та отримання налаштувань користувача. */
    private final SettingsService settingsService;

    /**
     * @return стан бота, який обробляється цим інвокером — {@link BotState#CURRENCY_CHOICE}.
     */
    @Override
    public BotState getInvokedState() {
        return CURRENCY_CHOICE;
    }

    /**
     * Основний метод обробки callback-оновлень, отриманих від користувача.
     * <ul>
     *   <li>При натисканні на одну з валют ("USD", "EUR", "GBP") — змінює стан вибору цієї валюти.</li>
     *   <li>При натисканні кнопки "Назад" — повертає користувача до меню налаштувань.</li>
     *   <li>При будь-яких інших даних — просто оновлює меню валют.</li>
     * </ul>
     *
     * @param update об’єкт оновлення Telegram, що містить callback з даними
     * @return сформований об’єкт {@link BotResponse} із повідомленням та новим станом
     */
    @Override
    public BotResponse invoke(Update update) {
        String chosenButtonData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getFrom().getId();
        UserSettings userSettings = settingsService.getUsersSettings(chatId);
        List<String> currencies = userSettings.getCurrencies();
        SendMessage message;
        BotState nextState;

        switch (chosenButtonData) {
            case "USD", "EUR", "GBP" -> {
                // Перемикаємо вибір валюти (вибрана → видалити, не вибрана → додати)
                if (!currencies.remove(chosenButtonData)) {
                    currencies.add(chosenButtonData);
                }
                userSettings.setCurrencies(currencies);
                settingsService.saveUserSettings(userSettings);
                message = buildCurrencyMenu(chatId, currencies,
                        "Оберіть додаткові валюти, які хочете відстежувати:");
                nextState = CURRENCY_CHOICE;
            }

            case "BACK" -> {
                // Повернення до меню налаштувань
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text("⚙️ Налаштування")
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                nextState = HANDLE_SETTINGS;
            }

            case "HANDLE_MAIN_MENU" -> {
                // Повернення до main menu
                message = SendMessage.builder()
                        .chatId(chatId)
                        .text("Головне меню")
                        .replyMarkup(settingsReplyMarkup())
                        .build();
                nextState = HANDLE_MAIN_MENU;
            }

            default -> {
                // Будь-яке інше значення callback — просто оновлюємо меню
                message = buildCurrencyMenu(chatId, currencies,
                        "Оберіть валюти, які хочете відстежувати:");
                nextState = CURRENCY_CHOICE;
            }
        }

        return new BotResponse(message, nextState);
    }

    /**
     * Викликається при переході з батьківського меню.
     * Відображає наявні вибрані валюти користувача без зміни їхнього стану.
     *
     * @param chatId унікальний ідентифікатор чату користувача
     * @return об’єкт {@link BotResponse} із поточними налаштуваннями валют
     */
    public BotResponse invokeFromParent(Long chatId) {
        UserSettings userSettings = settingsService.getUsersSettings(chatId);
        List<String> currencies = userSettings.getCurrencies();

        SendMessage message = buildCurrencyMenu(chatId, currencies,
                "Оберіть валюти, які хочете відстежувати:");
        return new BotResponse(message, CURRENCY_CHOICE);
    }

    // -------------------------------------------------------------------------
    // Допоміжний метод для побудови клавіатури та повідомлення
    // -------------------------------------------------------------------------

    /**
     * Створює меню вибору валют з відповідними позначками (✅) для вже вибраних.
     * Також додає кнопку «⬅ Назад» для повернення в меню налаштувань.
     * @param chatId ідентифікатор користувача
     * @param currencies список вибраних валют
     * @param text текст повідомлення, який буде відображено у верхній частині меню
     * @return об’єкт {@link SendMessage} з клавіатурою {@link InlineKeyboardMarkup}
     */
    private SendMessage buildCurrencyMenu(Long chatId, List<String> currencies, String text) {
        String currencyUsd = currencies.contains("USD") ? "✅ USD" : "USD";
        String currencyEur = currencies.contains("EUR") ? "✅ EUR" : "EUR";
        String currencyGbp = currencies.contains("GBP") ? "✅ GBP" : "GBP";

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                List.of(
                        List.of(
                                btn(currencyUsd,"USD"),
                                btn(currencyEur,"EUR"),
                                btn(currencyGbp,"GBP")
                        ),
                        List.of(
                                btn("⬅ Назад","BACK"),
                                btn("Головне меню","HANDLE_MAIN_MENU")
                        )
                )
        );

        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
    }
}
