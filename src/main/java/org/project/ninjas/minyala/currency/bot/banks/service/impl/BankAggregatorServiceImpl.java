package org.project.ninjas.minyala.currency.bot.banks.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.service.BankAggregatorService;
import org.project.ninjas.minyala.currency.bot.banks.service.BankRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aggregates exchange rates from all supported banks:
 * Monobank, PrivatBank and NBU.
 * Combines their data into a unified list of {@link CurrencyRate}
 * objects for further processing or display in the Telegram bot.
 */
public class BankAggregatorServiceImpl implements BankAggregatorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankAggregatorServiceImpl.class);
    private final List<BankRateService> bankServices;

    /**
     * Creates a new aggregator with custom bank services.
     *
     * @param bankServices list of {@link BankRateService} instances
     */
    public BankAggregatorServiceImpl(List<BankRateService> bankServices) {
        this.bankServices = new ArrayList<>(bankServices);
    }

    @Override
    public List<CurrencyRate> getAllRates() {
        List<CurrencyRate> combined = new ArrayList<>();
        for (BankRateService service : bankServices) {
            try {
                combined.addAll(service.getRates());
            } catch (Exception e) {
                LOGGER.error("Failed to fetch rates from {}: {}",
                        service.getBankName(), e.getMessage());
            }
        }
        return combined;
    }
}
