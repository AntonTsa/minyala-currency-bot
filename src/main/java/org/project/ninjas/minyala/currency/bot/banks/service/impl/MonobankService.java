package org.project.ninjas.minyala.currency.bot.banks.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.service.BankRateService;

/**
 * Implementation of {@link BankRateService} for Monobank public API.
 */
public class MonobankService implements BankRateService {

  private static final String API_URL = "https://api.monobank.ua/bank/currency";

  @Override
  public String getBankName() {
    return "Monobank";
  }

  @Override
  public List<CurrencyRate> getRates() throws Exception {
    List<CurrencyRate> rates = new ArrayList<>();

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(API_URL))
        .GET()
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new RuntimeException("Failed to fetch Monobank API data: " + response.statusCode());
    }

    JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

    for (JsonElement el : jsonArray) {
      JsonObject obj = el.getAsJsonObject();

      int currencyCodeA = obj.get("currencyCodeA").getAsInt();
      int currencyCodeB = obj.get("currencyCodeB").getAsInt();

      // Only show currencies where base is UAH
      if (currencyCodeB == 980) { // 980 = UAH ISO code
        double rateBuy = obj.has("rateBuy") ? obj.get("rateBuy").getAsDouble() : 0.0;
        double rateSell = obj.has("rateSell") ? obj.get("rateSell").getAsDouble() : 0.0;
        double rateCross = obj.has("rateCross") ? obj.get("rateCross").getAsDouble() : 0.0;

        String currency = switch (currencyCodeA) {
          case 840 -> "USD";
          case 978 -> "EUR";
          case 985 -> "PLN";
          case 826 -> "GBP";
          default -> String.valueOf(currencyCodeA);
        };

        rates.add(new CurrencyRate(
            getBankName(),
            currency,
            rateBuy,
            rateSell,
            rateCross,
            LocalDate.now()
        ));
      }
    }

    return rates;
  }
}
