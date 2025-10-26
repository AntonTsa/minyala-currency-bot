package org.project.ninjas.minyala.currency.bot.settings;

import java.util.List;
import lombok.Data;

@Data
public class UserSettings {
    private Long chatId;
    private int decimalPlaces = 2;
    private String bank = "Приватбанк";
    private List<String> currency = List.of("USD");
    private String notifyTime = "09:00";

    public UserSettings(Long chatId) {
        this.chatId = chatId;
    }
}









@Data
public class UserSettings {
    private Long chatId;
    private int decimalPlaces = 2;
    private String bank = "Приватбанк";
    private List<String> currency = List.of("USD");
    private String notifyTime = "09:00"; 
    private boolean notificationsEnabled = true;

    public UserSettings(Long chatId) {
        this.chatId = chatId;
    }
}
