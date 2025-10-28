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
        if (userSettings.getBanks() == null || userSettings.getBanks().isEmpty()) {
            return "Не обрано жодного банку для відображення.";
        }
        if (userSettings.getCurrencies() == null || userSettings.getCurrencies().isEmpty()) {
            return "Не обрано жодної валюти для відображення.";
        }

        BankAggregatorService aggregatorService = BankAggregatorServiceImpl.getInstance();
        List<CurrencyRate> allRates = aggregatorService.getAllRates();
        int digits = userSettings.getDecimalPlaces();
        StringBuilder text = new StringBuilder();

        for (String bankDisplayName : userSettings.getBanks()) {
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
                    if (rate.getBankName().equals(internalBankName)
                            && rate.getCurrency().equals(currency)) {
                        currencyFound = true;
                        foundCurrency = true;

                        if ("NBU".equals(internalBankName)) {
                            String officialRate =
                                    CurrencyFormatter.format(rate.getRate(), digits);
                            text.append(String.format(
                                    "%s/UAH - Офіційний курс: %s%n",
                                    rate.getCurrency(), officialRate));
                        } else {
                            String buy = rate.getBuy() > 0
                                    ? CurrencyFormatter.format(rate.getBuy(), digits)
                                    : "-";
                            String sell = rate.getSell() > 0
                                    ? CurrencyFormatter.format(rate.getSell(), digits)
                                    : "-";
                            text.append(String.format(
                                    "%s/UAH - Купівля: %s; Продаж: %s%n",
                                    rate.getCurrency(), buy, sell));
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
