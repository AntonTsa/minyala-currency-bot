package org.project.ninjas.minyala.currency.bot.banks.service;

import java.util.List;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;

/**
 * Defines the contract for fetching exchange rates from a specific bank API.
 */
public interface BankRateService {

  /**
   * Returns the name of the bank (e.g., "Monobank", "PrivatBank", "NBU").
   *
   * @return the name of the bank
   */
  String getBankName();

  /**
   * Retrieves all available currency rates from the bank API.
   *
   * @return list of {@link CurrencyRate} objects
   * @throws Exception if an error occurs during API call or parsing
   */
  List<CurrencyRate> getRates() throws Exception;
}
