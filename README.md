# Minyala Currency Bot

A Java-based Telegram bot that provides real-time currency exchange rates from multiple Ukrainian banks. Built as a core Java project (no Spring Boot), it demonstrates state-based bot logic, modular design, and API integration.

## 🧩 Overview

The **Minyala Currency Bot** allows users to:
- View currency rates from **Monobank**, **PrivatBank**, or **NBU** (National Bank of Ukraine).
- Switch between banks dynamically.
- Configure display settings (e.g., decimal precision).
- Navigate via Telegram inline and reply keyboards.

This project is designed as a **state-driven bot**, meaning every user interaction is handled according to their current `BotState`.

---

## ⚙️ Technologies

- **Java 17+**
- **TelegramBots Java Library** (long polling)
- **Gson / Jackson** (JSON parsing)
- **Java HTTP Client (java.net.http)**
- **Lombok** (for model boilerplate)
- **Gradle** (build tool)
- **Docker** (deployment ready)



---

## 🧠 Bot States and Flow


```
[START]
   │ 
   ▼
[Main Menu]
├── "Get Info" ───► [Current Info]
│                     ▲
│                     └── returns to Main Menu
│
└── "Settings" ───► [Settings Menu]
├── "Precision" ───► [Precision Options]
├── "Bank" ─────────► [Bank Options]
├── "Currency" ─────► [Currency Options]
└── "Notifications" ─► [Notification Options]
```
Each state is processed by a specific handler implementing the `StateHandler` interface and registered in `BotStateContext`.

---

## 🏦 Bank APIs

### 1. Monobank
- Endpoint: `https://api.monobank.ua/bank/currency`
- Returns buy/sell rates for multiple currencies.

### 2. PrivatBank
- Endpoint: `https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11`
- Returns standard list of currency pairs (USD, EUR, etc.).

### 3. NBU (National Bank of Ukraine)
- Endpoint: `https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json`
- Provides official rates with timestamps.

Each API response is parsed into a `CurrencyRate` object and formatted according to the user’s settings.

---

## 💬 Example Flow

1. User starts bot → receives main menu.
2. Selects **Bank choice** → chooses e.g., *Monobank*.
3. Bot fetches JSON → maps it into `CurrencyRate` list.
4. Rates are displayed in a clean message format:

```
Monobank | USD | Buy: 41.55 | Sell: 41.99
Monobank | EUR | Buy: 48.50 | Sell: 49.24
```

5. User may open **Settings** to change number of decimals or switch to another bank.

---

## 💡 Developer Notes

- Each **bank API** is handled within the `BankRateService`. Missing fields (like timestamp) are returned as `null` to allow the developer to handle display logic (e.g., showing dash or server time).
- **Extending the bot:**
    1. Add a new `Bank` enum constant.
    2. Extend `BankRateService` with a parser for that bank.
    3. Update `CurrencyChoiceHandler` to support the new option.

- **Custom formatting:** Developers may use `CurrencyRate` getters to implement custom display logic.

```java
CurrencyRate rate = bankRateService.getRate(Bank.MONOBANK, "USD");
System.out.println(rate.toString()); // or custom formatting
```

---

## 🚀 Running the Bot

### 1. Set Environment Variables

```
BOT_TOKEN=your_telegram_bot_token
BOT_USERNAME=your_bot_username
```

### 2. Build and Run

```bash
gradle build
java -jar build/libs/minyala-currency-bot.jar
```

---

## 🧩 Future Enhancements

- Multi-language support (Ukrainian, English)
- Inline keyboard updates instead of sending new messages
- Historical rate charts
- Caching and rate comparison between banks

---

## 👥 Authors

- [AntonTsa](https://github.com/AntonTsa)
- [inna-lisa](https://github.com/inna-lisa)
- [RuslanLomaka](https://github.com/RuslanLomaka)
- [AnonymousMar](https://github.com/AnonymousMar)
- [KatyaMakarichak](https://github.com/KatyaMakarichak)

**Project Ninjas** — Educational Java Core project by the team.

---

## 📄 License

MIT License – free to use and modify.
