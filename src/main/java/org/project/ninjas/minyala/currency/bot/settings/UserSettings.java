package org.project.ninjas.minyala.currency.bot.settings;

import java.util.List;
import lombok.Data;

/**
 * Entity that represents settings of currency rates representation.
 */
@Data
public class UserSettings {
    private Long chatId;
    private int decimalPlaces = 2;
    private Bank bank = Bank.PRIVATBANK;
    private List<String> currency = List.of("USD");
    private String notifyTime = "09:00";

    /**
     * Constructs default settings for the given chat id.
     *
     * @param chatId the user's chat id.
     */
    public UserSettings(Long chatId) {
        this.chatId = chatId;
    }
}
