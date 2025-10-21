package org.project.ninjas.minyala.currency.bot.banks.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Represents a unified currency rate from any supported bank.
 * Used by Monobank, PrivatBank and NBU implementations.
 */
@Getter
@Setter
@AllArgsConstructor
public class CurrencyRate {

    private String bankName;
    private String currency;
    private double buy;
    private double sell;
    private double rate;
    private LocalDate date;

    @Override
    public String toString() {
        return String.format("%s %s [Buy: %.4f, Sell: %.4f, Rate: %.4f]",
                bankName, currency, buy, sell, rate);
    }
}
