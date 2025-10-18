package org.project.ninjas.minyala.currency.bot.banks.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
 * Implementation of {@link BankRateService} for PrivatBank public API.
 */
public class PrivatBankService implements BankRateService {

  private static final String API_URL =
      "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";

  @Override
  public String getBankName() {
    return "PrivatBank";
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
      throw new RuntimeException("Failed to fetch PrivatBank API data: " + response.statusCode());
    }

    JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

    for (JsonElement el : jsonArray) {
      var obj = el.getAsJsonObject();

      String ccy = obj.get("ccy").getAsString();
      String base = obj.get("base_ccy").getAsString();
      double buy = Double.parseDouble(obj.get("buy").getAsString());
      double sell = Double.parseDouble(obj.get("sale").getAsString());

      // Only add major currencies
      if (base.equalsIgnoreCase("UAH")) {
        rates.add(new CurrencyRate(
            getBankName(),
            ccy,
            buy,
            sell,
            0.0,
            LocalDate.now()
        ));
      }
    }

    return rates;
  }
}
