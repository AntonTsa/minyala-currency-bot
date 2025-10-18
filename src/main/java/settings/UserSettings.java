package settings;

import lombok.Data;

@Data
public class UserSettings {
	private Long chatId;
	private int decimalPlaces = 2;
	private String bank = "Приватбанк";
	private String currency = "USD";
	private String notifyTime = "09:00";

	public UserSettings(Long chatId) {
		this.chatId = chatId;
	}
}
