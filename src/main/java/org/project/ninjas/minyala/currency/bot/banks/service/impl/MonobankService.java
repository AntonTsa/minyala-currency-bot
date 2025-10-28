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
 * Monobank implementation of {@link BankRateService}.
 * Provides exchange rates with minimal in-memory caching (1 hour).
 */
public class MonobankService implements BankRateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonobankService.class);
    private static final MonobankService INSTANCE = new MonobankService();

    private static final String BANK_NAME = "Monobank";
    private static final String API_URL = "https://api.monobank.ua/bank/currency";
    private static HttpClient client;

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private volatile List<CurrencyRate> cachedRates;

    private volatile Instant lastFetchAt;

    private MonobankService() {
    }

    /**
     * Lazily provides a shared {@link HttpClient} instance.
     * This avoids static initialization before Mockito mocks are applied in tests.
     *
     * @return the shared HttpClient
     */
    private static HttpClient getClient() {
        if (client == null) {
            client = HttpClientProvider.getClient();
        }
        return client;
    }

    /**
     * Returns the singleton instance of {@link MonobankService}.
     *
     * @return the single shared instance of this service
     */
    public static MonobankService getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBankName() {
        return BANK_NAME;
    }

    /**
     * Returns cached rates if valid; otherwise fetches from Monobank API.
     */
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

    /**
     * Fetches data from Monobank API and converts to CurrencyRate list.
     */
    private List<CurrencyRate> fetchFromApi() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            LOGGER.warn("Monobank API returned status {}", response.statusCode());
            return List.of();
        }

        JsonArray arr = JsonParser.parseString(response.body()).getAsJsonArray();
        List<CurrencyRate> result = new ArrayList<>(arr.size());
        LocalDate today = LocalDate.now();

        for (JsonElement el : arr) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            int codeA = o.has("currencyCodeA") ? o.get("currencyCodeA").getAsInt() : 0;
            int codeB = o.has("currencyCodeB") ? o.get("currencyCodeB").getAsInt() : 0;
            if (codeB != 980) {
                continue;
            }

            String currency = String.valueOf(codeA);
            double buy = getDouble(o, "rateBuy");
            double sell = getDouble(o, "rateSell");
            double cross = getDouble(o, "rateCross");

            result.add(new CurrencyRate(BANK_NAME, currency, buy, sell, cross, today));
        }
        return result;
    }

    private static double getDouble(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsDouble() : 0.0;
    }
}
