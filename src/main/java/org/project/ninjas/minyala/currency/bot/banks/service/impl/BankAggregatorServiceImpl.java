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
 * Implemented as a singleton to ensure a single shared cache
 * across all bank service instances.
 */
public class BankAggregatorServiceImpl implements BankAggregatorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankAggregatorServiceImpl.class);

    /** Singleton instance. */
    private static final BankAggregatorServiceImpl INSTANCE = new BankAggregatorServiceImpl();

    private final List<BankRateService> bankServices = new ArrayList<>();

    /** Private constructor initializes single instances of all bank services. */
    private BankAggregatorServiceImpl() {
        bankServices.add(MonobankService.getInstance());
        bankServices.add(PrivatBankService.getInstance());
        bankServices.add(NbuService.getInstance());
    }

    /**
     * Provides global singleton instance of this aggregator.
     *
     * @return instance of BankAggregatorServiceImpl
     */
    public static BankAggregatorServiceImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public List<CurrencyRate> getAllRates() {
        List<CurrencyRate> combined = new ArrayList<>();
        for (BankRateService service : bankServices) {
            try {
                combined.addAll(service.getRates());
            } catch (Exception e) {
                LOGGER.error("Failed to fetch rates from {}: {}", service.getBankName(), e.getMessage());
            }
        }
        return combined;
    }
}
