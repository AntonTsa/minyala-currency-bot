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
    private int decimalPlaces = 2;
    private String bank = String.valueOf(Bank.PRIVAT);
    private List<String> currencies = new ArrayList<>(List.of("USD"));
    private String notifyTime = "9:00";

    /**
     * Constructor to create default settings.
     *
     * @param userId - user's chat id
     */
    public UserSettings(Long userId) {
        this.userId = userId;
    }
}
