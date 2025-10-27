package org.project.ninjas.minyala.currency.bot.bot.service;

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
 * Service to get currency information based on user settings.
 */
public class InfoService {

    /**
     * Get currency information based on user settings.
     *
     * @param userSettings - user's settings
     * @return formatted currency information
     */
    public String getCurrencyInfo(UserSettings userSettings) {
        List<BankRateService> selectedBanks = new ArrayList<>();

        switch (userSettings.getBank().getDisplayName()) {
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

        if (userSettings.getCurrencies().isEmpty()) {
            return "Не обрано жодної валюти для відображення.";
        }
        for (String currency : userSettings.getCurrencies()) {
            boolean currencyFoundRates = false;
            for (CurrencyRate rate : rates) {
                if (rate.getCurrency().equals(currency)) {
                    currencyFoundRates = true;
                    foundCurrency = true;

                    String buy;
                    String sell;
                    if (userSettings.getBank().getDisplayName().equals("НБУ")) {
                        String officialRate = CurrencyFormatter.format(rate.getBuy(), digits);
                        text.append(
                                String.format("%s/UAH - Купівля та продаж: %s\n", rate.getCurrency(), officialRate));
                    } else {
                        buy = rate.getBuy() > 0 ? CurrencyFormatter.format(rate.getBuy(), digits) : "-";
                        sell = rate.getSell() > 0 ? CurrencyFormatter.format(rate.getSell(), digits) : "-";

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

    /*
     * === 🔧 INSTRUCTION FOR TEAM (REMOVE AFTER UPDATING) ===
     *
     * To align with the new singleton-based services and caching system:
     *
     * Replace the section below:
     *
     *   switch (userSettings.getBank().getDisplayName()) {
     *       case "Приватбанк" -> selectedBanks.add(new PrivatBankService());
     *       case "Монобанк" -> selectedBanks.add(new MonobankService());
     *       case "НБУ" -> selectedBanks.add(new NbuService());
     *       default -> selectedBanks.add(new PrivatBankService());
     *   }
     *
     *   BankAggregatorService aggregatorService = new BankAggregatorServiceImpl(selectedBanks);
     *
     * WITH:
     *
     *   switch (userSettings.getBank().getDisplayName()) {
     *       case "Приватбанк" -> selectedBanks.add(PrivatBankService.getInstance());
     *       case "Монобанк" -> selectedBanks.add(MonobankService.getInstance());
     *       case "НБУ" -> selectedBanks.add(NbuService.getInstance());
     *       default -> selectedBanks.add(PrivatBankService.getInstance());
     *   }
     *
     *   BankAggregatorService aggregatorService = BankAggregatorServiceImpl.getInstance();
     *
     * This enables caching and shared singletons across all service calls.
     * After replacing, delete this comment block.
     * ===========================================================
     */
}
