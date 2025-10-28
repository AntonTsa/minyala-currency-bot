package org.project.ninjas.minyala.currency.bot.bot.service;

import java.util.List;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.service.BankAggregatorService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.BankAggregatorServiceImpl;
import org.project.ninjas.minyala.currency.bot.banks.util.CurrencyFormatter;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;

/**
 * Service to get currency information based on user settings.
 */
public class InfoService {

    /**
     * Returns formatted currency information for all selected banks and currencies.
     *
     * @param userSettings user's settings
     * @return formatted text with exchange rates
     */
    public String getCurrencyInfo(UserSettings userSettings) {
        if (userSettings.getBank() == null || userSettings.getBank().isEmpty()) {
            return "Не обрано жодного банку для відображення.";
        }
        if (userSettings.getCurrencies() == null || userSettings.getCurrencies().isEmpty()) {
            return "Не обрано жодної валюти для відображення.";
        }

        BankAggregatorService aggregatorService = BankAggregatorServiceImpl.getInstance();
        List<CurrencyRate> allRates = aggregatorService.getAllRates();
        int digits = userSettings.getDecimalPlaces();
        StringBuilder text = new StringBuilder();

        for (String bankDisplayName : userSettings.getBank()) {
            String internalBankName = switch (bankDisplayName) {
                case "ПриватБанк" -> "PrivatBank";
                case "МоноБанк" -> "Monobank";
                case "НБУ" -> "NBU";
                default -> "PrivatBank";
            };

            text.append("Курс у ").append(bankDisplayName).append(":\n");
            boolean foundCurrency = false;

            for (String currency : userSettings.getCurrencies()) {
                boolean currencyFound = false;

                for (CurrencyRate rate : allRates) {
                    // Normalize numeric codes to ISO strings for comparison
                    String normalizedCurrency = switch (rate.getCurrency()) {
                        case "840" -> "USD";
                        case "978" -> "EUR";
                        case "826" -> "GBP";
                        default -> rate.getCurrency();
                    };

                    if (internalBankName.equals(rate.getBankName())
                            && currency.equalsIgnoreCase(normalizedCurrency)) {
                        currencyFound = true;
                        foundCurrency = true;

                        if ("NBU".equals(internalBankName)) {
                            String officialRate =
                                    CurrencyFormatter.format(rate.getRate(), digits);
                            text.append(String.format(
                                    "%s/UAH - Офіційний курс: %s%n",
                                    normalizedCurrency, officialRate));
                        } else {
                            String buy = rate.getBuy() > 0
                                    ? CurrencyFormatter.format(rate.getBuy(), digits)
                                    : "-";
                            String sell = rate.getSell() > 0
                                    ? CurrencyFormatter.format(rate.getSell(), digits)
                                    : "-";
                            text.append(String.format(
                                    "%s/UAH - Купівля: %s; Продаж: %s%n",
                                    normalizedCurrency, buy, sell));
                        }
                    }
                }

                if (!currencyFound) {
                    text.append(String.format("%s - інформація недоступна%n", currency));
                }
            }

            if (!foundCurrency) {
                text.append("Обрані валюти не знайдено у цьому банку.\n");
            }

            text.append("\n");
        }

        return text.toString().trim();
    }
}
