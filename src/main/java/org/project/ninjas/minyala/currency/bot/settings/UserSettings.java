package org.project.ninjas.minyala.currency.bot.settings;

import static org.project.ninjas.minyala.currency.bot.bot.util.Constants.Banks.PRIVAT;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Entity that represents settings of currency rates representation.
 */
@Data
public class UserSettings {
    private Long userId;
    private int decimalPlaces = 2;
    private String bank = PRIVAT.getDisplayName();
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
