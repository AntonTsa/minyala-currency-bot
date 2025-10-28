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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.service.BankRateService;
import org.project.ninjas.minyala.currency.bot.banks.util.HttpClientProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * National Bank of Ukraine (NBU) implementation of {@link BankRateService}.
 * Provides official rates with minimal in-memory cache (1 hour).
 */
public class NbuServiceImpl implements BankRateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NbuServiceImpl.class);
    private static final NbuServiceImpl INSTANCE = new NbuServiceImpl();

    private static final String BANK_NAME = "NBU";
    private static final String API_URL =
            "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private static HttpClient client;

    private volatile List<CurrencyRate> cachedRates;
    private volatile Instant lastFetchAt;

    private NbuServiceImpl() {
    }

    /**
     * Returns the singleton instance of {@link NbuServiceImpl}.
     *
     * @return the single shared instance of this service
     */
    public static NbuServiceImpl getInstance() {
        return INSTANCE;
    }

    /**
     * Lazily provides a shared {@link HttpClient} instance.
     * Avoids static initialization before Mockito mocks are applied in tests.
     *
     * @return the shared HttpClient
     */
    private static HttpClient getClient() {
        if (client == null) {
            client = HttpClientProvider.getClient();
        }
        return client;
    }

    @Override
    public String getBankName() {
        return BANK_NAME;
    }

    @Override
    public List<CurrencyRate> getRates() throws Exception {
        Instant now = Instant.now();
        List<CurrencyRate> current = cachedRates;
        Instant fetchedAt = lastFetchAt;

        if (current != null && fetchedAt != null
                && Duration.between(fetchedAt, now).compareTo(CACHE_TTL) < 0) {
            return current;
        }

        synchronized (this) {
            if (cachedRates != null && lastFetchAt != null
                    && Duration.between(lastFetchAt, now).compareTo(CACHE_TTL) < 0) {
                return cachedRates;
            }
            List<CurrencyRate> fresh = fetchFromApi();
            cachedRates = fresh;
            lastFetchAt = now;
            return fresh;
        }
    }

    private List<CurrencyRate> fetchFromApi() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response =
                getClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            LOGGER.warn("NBU API returned status {}", response.statusCode());
            return List.of();
        }

        JsonArray arr = JsonParser.parseString(response.body()).getAsJsonArray();
        List<CurrencyRate> result = new ArrayList<>(arr.size());

        for (JsonElement el : arr) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            String currency = o.has("cc") ? o.get("cc").getAsString() : "";
            double rate = o.has("rate") ? safeDouble(o.get("rate")) : 0.0;

            LocalDate date = parseExchangeDate(o);
            result.add(new CurrencyRate(BANK_NAME, currency, 0.0, 0.0, rate, date));
        }

        return result;
    }

    private static LocalDate parseExchangeDate(JsonObject o) {
        if (!o.has("exchangedate")) {
            return LocalDate.now();
        }
        try {
            String ds = o.get("exchangedate").getAsString();
            String[] parts = ds.split("\\.");
            if (parts.length == 3) {
                int d = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                return LocalDate.of(y, m, d);
            }
        } catch (Exception ignore) {
            // fallback to today
        }
        return LocalDate.now();
    }

    private static double safeDouble(JsonElement el) {
        try {
            return el.getAsDouble();
        } catch (Exception ex) {
            try {
                return Double.parseDouble(el.getAsString().replace(',', '.').trim());
            } catch (Exception ignore) {
                return 0.0;
            }
        }
    }
}
