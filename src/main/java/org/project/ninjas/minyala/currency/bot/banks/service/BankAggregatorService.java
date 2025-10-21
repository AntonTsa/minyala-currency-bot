package org.project.ninjas.minyala.currency.bot.banks.service;

import java.util.List;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;

/**
 * Provides unified access to all supported bank APIs.
 * This service aggregates data from Monobank, PrivatBank and NBU.
 */
public interface BankAggregatorService {

    /**
     * Retrieves combined rates from all configured bank services.
     *
     * @return list of {@link CurrencyRate} from all banks
     */
    List<CurrencyRate> getAllRates();

    /**
     * Retrieves rates filtered by currency code.
     *
     * @param currency the currency code (e.g., "USD", "EUR")
     * @return list of matching {@link CurrencyRate}
     */
    List<CurrencyRate> getRatesByCurrency(String currency);
}
