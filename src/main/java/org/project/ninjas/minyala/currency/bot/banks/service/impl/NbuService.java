package org.project.ninjas.minyala.currency.bot.banks.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
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
 * Implementation of {@link BankRateService} for
 * the National Bank of Ukraine (NBU) official exchange rates.
 */
public class NbuService implements BankRateService {

    private static final String API_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

    @Override
    public String getBankName() {
        return "NBU";
    }

    @Override
    public List<CurrencyRate> getRates() {
        List<CurrencyRate> rates = new ArrayList<>();
        HttpResponse<String> response = null;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (response == null || response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch NBU API data: " + response.statusCode());
        }

        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        for (JsonElement el : jsonArray) {
            JsonObject obj = el.getAsJsonObject();

            String code = obj.get("cc").getAsString();
            double rate = obj.get("rate").getAsDouble();

            // Limit to popular currencies
            if (code.equals("USD")
                    || code.equals("EUR")
                    || code.equals("GBP")
                    || code.equals("PLN")) {
                rates.add(new CurrencyRate(
                        getBankName(),
                        code,
                        0.0,
                        0.0,
                        rate,
                        LocalDate.parse(LocalDate.now().toString()) // today’s date
                ));
            }
        }

        return rates;
    }
}
