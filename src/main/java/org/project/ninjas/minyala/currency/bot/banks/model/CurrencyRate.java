package org.project.ninjas.minyala.currency.bot.banks.model;

import java.time.LocalDate;

/**
 * Represents a unified currency rate from any supported bank.
 * Used by Monobank, PrivatBank and NBU implementations.
 * </p>
 */
public class CurrencyRate {

  private String bankName;
  private String currency;
  private double buy;
  private double sell;
  private double rate;
  private LocalDate date;

  /**
   * Default constructor.
   */
  public CurrencyRate() {
  }

  /**
   * Constructs a new CurrencyRate.
   *
   * @param bankName the name of the bank providing the rate
   * @param currency the currency code (e.g., USD, EUR)
   * @param buy      the buy rate (0 if not applicable)
   * @param sell     the sell rate (0 if not applicable)
   * @param rate     the official rate (for NBU)
   * @param date     the date of the rate
   */
  public CurrencyRate(String bankName, String currency,
                      double buy, double sell, double rate, LocalDate date) {
    this.bankName = bankName;
    this.currency = currency;
    this.buy = buy;
    this.sell = sell;
    this.rate = rate;
    this.date = date;
  }

  public String getBankName() {
    return bankName;
  }

  public String getCurrency() {
    return currency;
  }

  public double getBuy() {
    return buy;
  }

  public double getSell() {
    return sell;
  }

  public double getRate() {
    return rate;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public void setBuy(double buy) {
    this.buy = buy;
  }

  public void setSell(double sell) {
    this.sell = sell;
  }

  public void setRate(double rate) {
    this.rate = rate;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return String.format("%s %s [Buy: %.4f, Sell: %.4f, Rate: %.4f]",
        bankName, currency, buy, sell, rate);
  }
}
