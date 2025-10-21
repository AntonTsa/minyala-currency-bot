package org.project.ninjas.minyala.currency.bot.settings;

import lombok.Getter;

/**
 * Enum that represents supported banks for currency rates.
 */
@Getter
public enum Bank {
    /** Monobank option. */
    MONOBANK("Monobank"),
    /** PrivatBank option. */
    PRIVATBANK("ПриватБанк"),
    /** National Bank of Ukraine option. */
    NBU("НБУ");

    private final String label;

    /**
     * Constructs a bank enum with a human-readable label.
     *
     * @param label the display label of the bank.
     */
    Bank(String label) {
        this.label = label;
    }
}
