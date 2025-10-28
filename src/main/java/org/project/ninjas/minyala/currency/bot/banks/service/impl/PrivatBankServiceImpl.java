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
 * PrivatBank implementation of {@link BankRateService}.
 * Provides exchange rates with minimal in-memory caching (1 hour).
 */
public class PrivatBankServiceImpl implements BankRateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrivatBankServiceImpl.class);
    private static final PrivatBankServiceImpl INSTANCE = new PrivatBankServiceImpl();

    private static final String BANK_NAME = "PrivatBank";
    private static final String API_URL =
            "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private static HttpClient client;

    private volatile List<CurrencyRate> cachedRates;
    private volatile Instant lastFetchAt;

    private PrivatBankServiceImpl() {
    }

    /**
     * Returns the singleton instance of {@link PrivatBankServiceImpl}.
     *
     * @return the single shared instance of this service
     */
    public static PrivatBankServiceImpl getInstance() {
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
            LOGGER.warn("PrivatBank API returned status {}", response.statusCode());
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
            String base = o.has("base_ccy") ? o.get("base_ccy").getAsString() : "UAH";
            if (!"UAH".equalsIgnoreCase(base)) {
                continue;
            }

            String currency = o.has("ccy") ? o.get("ccy").getAsString() : "";
            double buy = o.has("buy") ? parseDoubleSafe(o.get("buy").getAsString()) : 0.0;
            double sell = o.has("sale") ? parseDoubleSafe(o.get("sale").getAsString()) : 0.0;

            result.add(new CurrencyRate(BANK_NAME, currency, buy, sell, 0.0, today));
        }

        return result;
    }

    private static double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.replace(',', '.').trim());
        } catch (Exception ex) {
            return 0.0;
        }
    }
}
