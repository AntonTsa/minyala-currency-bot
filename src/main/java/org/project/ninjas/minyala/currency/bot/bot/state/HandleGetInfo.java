package org.project.ninjas.minyala.currency.bot.bot.state;

import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.service.BankAggregatorService;
import org.project.ninjas.minyala.currency.bot.banks.service.BankRateService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.BankAggregatorServiceImpl;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.MonobankService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.NbuService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.PrivatBankService;

import org.project.ninjas.minyala.currency.bot.banks.util.CurrencyFormatter;
import org.project.ninjas.minyala.currency.bot.settings.UserSettings;

import java.util.ArrayList;
import java.util.List;

public class HandleGetInfo {


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

        for (String currency : userSettings.getCurrencies()) {
            for (CurrencyRate rate : rates) {
                if (rate.getCurrency().equals(currency)) {
                    String buy = rate.getBuy() > 0 ? CurrencyFormatter.format(rate.getBuy(), digits) : "-";
                    String sell = rate.getSell() > 0 ? CurrencyFormatter.format(rate.getSell(), digits) : "-";

                    text.append(String.format("%s/UAH - Купівля: %s; Продаж: %s\n",
                            rate.getCurrency(),
                            buy,
                            sell));
                }
            }
        }

        return text.toString();
    }
}