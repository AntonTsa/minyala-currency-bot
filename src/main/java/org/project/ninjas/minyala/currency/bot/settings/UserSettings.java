package org.project.ninjas.minyala.currency.bot.settings;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.project.ninjas.minyala.currency.bot.bot.util.Bank;

/**
 * Entity that represents settings of currency rates representation.
 */
@Data
public class UserSettings {
    private Long userId;
    private int decimalPlaces;
    private List<String> banks;
    private List<String> currencies;
    private String notifyTime;

    /**
     * Constructor to create default settings.
     *
     * @param userId - user's chat id
     */
    public UserSettings(Long userId) {
        this.userId = userId;
        this.decimalPlaces = 2;
        this.banks = new ArrayList<>(List.of(Bank.PRIVAT.getDisplayName()));
        this.currencies = new ArrayList<>(List.of("USD"));
        this.notifyTime = "09:00";
    }
}
