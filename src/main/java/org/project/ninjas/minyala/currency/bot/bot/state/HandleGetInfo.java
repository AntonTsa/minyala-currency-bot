package org.project.ninjas.minyala.currency.bot.bot.state;

import java.util.ArrayList;
import java.util.List;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.service.BankAggregatorService;
import org.project.ninjas.minyala.currency.bot.banks.service.BankRateService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.BankAggregatorServiceImpl;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.MonobankService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.NbuService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.PrivatBankService;
import org.project.ninjas.minyala.currency.bot.banks.util.CurrencyFormatter;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;

/**
 * Handles the retrieval and formatting of currency information based on user settings.
 */
public class HandleGetInfo {

    /**
     * Retrieves and formats currency information based on the provided user settings.
     *
     * @param userSettings the user's settings including bank, currencies, and decimal places
     * @return formatted string with currency information
     */
    public static String getCurrencyInfo(UserSettings userSettings) {
        List<BankRateService> selectedBanks = new ArrayList<>();

        switch (userSettings.getBank()) {
            case "Приватбанк" -> selectedBanks.add(new PrivatBankService());
            case "Монобанк" -> selectedBanks.add(new MonobankService());
            case "НБУ" -> selectedBanks.add(new NbuService());
            default -> selectedBanks.add(new PrivatBankService());
        }

        BankAggregatorService aggregatorService = new BankAggregatorServiceImpl(selectedBanks);
        List<CurrencyRate> rates = aggregatorService.getAllRates();

        int digits = userSettings.getDecimalPlaces();
        StringBuilder text = new StringBuilder("Курс у " + userSettings.getBank() + ":\n");

        boolean foundCurrency = false;

        for (String currency : userSettings.getCurrencies()) {
            boolean currencyFoundRates = false;
            for (CurrencyRate rate : rates) {
                if (rate.getCurrency().equals(currency)) {
                    currencyFoundRates = true;
                    foundCurrency = true;

                    String buy;
                    String sell;
                    if (userSettings.getBank().equals("НБУ")) {
                        String officialRate = CurrencyFormatter.format(rate.getBuy(), digits);
                        text.append(
                                String.format("%s/UAH - Купівля та продаж: %s\n",
                                        rate.getCurrency(), officialRate));
                    } else {
                        buy = rate.getBuy() > 0
                                ? CurrencyFormatter.format(rate.getBuy(), digits)
                                : "-";
                        sell = rate.getSell() > 0
                                ? CurrencyFormatter.format(rate.getSell(), digits)
                                : "-";

                        text.append(String.format("%s/UAH - Купівля: %s; Продаж: %s\n",
                                rate.getCurrency(),
                                buy,
                                sell));
                    }

                }
            }
            if (!currencyFoundRates) {
                text.append(String.format("%s - інформація недоступна\n", currency));
            }
        }
        if (!foundCurrency) {
            return "Обрані валюти не знайдено у вибраному банку.";
        }

        return text.toString();
    }
}
