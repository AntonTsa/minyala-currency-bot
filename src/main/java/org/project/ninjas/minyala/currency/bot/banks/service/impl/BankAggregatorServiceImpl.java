package org.project.ninjas.minyala.currency.bot.banks.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.service.BankAggregatorService;
import org.project.ninjas.minyala.currency.bot.banks.service.BankRateService;

/**
 * Aggregates exchange rates from all supported banks:
 * Monobank, PrivatBank and NBU.
 * Combines their data into a unified list of {@link CurrencyRate}
 * objects for further processing or display in the Telegram bot.
 * </p>
 */
public class BankAggregatorServiceImpl implements BankAggregatorService {

  private final List<BankRateService> bankServices;

  /**
   * Creates a new aggregator with default bank implementations.
   */
  public BankAggregatorServiceImpl() {
    this.bankServices = new ArrayList<>();
    this.bankServices.add(new MonobankService());
    this.bankServices.add(new PrivatBankService());
    this.bankServices.add(new NbuService());
  }

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
        System.err.println("⚠️ Failed to fetch rates from " + service.getBankName()
            + ": " + e.getMessage());
      }
    }
    return combined;
  }

  @Override
  public List<CurrencyRate> getRatesByCurrency(String currency) {
    if (currency == null || currency.isBlank()) {
      return Collections.emptyList();
    }

    String normalized = currency.trim().toUpperCase();
    return getAllRates().stream()
        .filter(rate -> normalized.equalsIgnoreCase(rate.getCurrency()))
        .collect(Collectors.toList());
  }
}
