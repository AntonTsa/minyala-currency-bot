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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monobank implementation of {@link BankRateService}.
 * Minimal in-memory cache (1 hour) + singleton HttpClient.
 * Debug printing can be toggled with the DEBUG flag.
 */
public class MonobankService implements BankRateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonobankService.class);

    /** Singleton instance. */
    private static final MonobankService INSTANCE = new MonobankService();

    /** Private constructor to enforce singleton usage. */
    private MonobankService() {
    }

    /**
     * Provides global singleton instance of this service.
     *
     * @return the MonobankService singleton
     */
    public static MonobankService getInstance() {
        return INSTANCE;
    }

    /** Toggle for temporary stdout debug prints. */
    private static final boolean DEBUG = true;

    private static final String BANK_NAME = "Monobank";

    private static final String API_URL = "https://api.monobank.ua/bank/currency";
    /** Singleton HTTP client (connection reuse, DNS caching, etc.). */
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    /** Cache config and storage. */
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private volatile List<CurrencyRate> cachedRates;
    private volatile Instant lastFetchAt;

    private static void debug(String msg) {
        if (DEBUG) {
            System.out.println("[MonobankService] " + msg);
        }
    }

    @Override
    public String getBankName() {
        return BANK_NAME;
    }

    /**
     * Returns rates, from cache if not older than 1 hour; otherwise fetches fresh data.
     */
    @Override
    public List<CurrencyRate> getRates() throws Exception {
        final Instant now = Instant.now();
        final List<CurrencyRate> current = cachedRates;
        final Instant fetchedAt = lastFetchAt;

        if (current != null && fetchedAt != null
                && Duration.between(fetchedAt, now).compareTo(CACHE_TTL) < 0) {
            debug("Cache HIT. Age=" + Duration.between(fetchedAt, now).toMinutes() + "m; size=" + current.size());
            return current;
        }

        debug("Cache MISS/EXPIRED. "
                + "hadCache=" + (current != null)
                + ", lastFetchAt=" + fetchedAt);

        synchronized (this) {
            if (cachedRates != null && lastFetchAt != null
                    && Duration.between(lastFetchAt, now).compareTo(CACHE_TTL) < 0) {
                debug("Cache became valid during wait. Returning cached. "
                        + "Age=" + Duration.between(lastFetchAt, now).toMinutes() + "m; size=" + cachedRates.size());
                return cachedRates;
            }
            debug("Fetching from API: " + API_URL);
            final List<CurrencyRate> fresh = fetchFromApi();
            cachedRates = fresh;
            lastFetchAt = now;
            debug("Stored fresh data in cache. size=" + fresh.size());
            return fresh;
        }
    }

    /**
     * Calls Monobank API and maps its response to our unified CurrencyRate list.
     */
    private List<CurrencyRate> fetchFromApi() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        debug("HTTP status=" + response.statusCode());

        if (response.statusCode() != 200) {
            LOGGER.warn("Monobank API returned status {}", response.statusCode());
            debug("Non-200 status. Returning empty list.");
            return List.of();
        }

        String body = response.body();
        JsonArray arr = JsonParser.parseString(body).getAsJsonArray();
        debug("Parsing JSON array. count=" + arr.size());

        List<CurrencyRate> result = new ArrayList<>(arr.size());
        LocalDate today = LocalDate.now();

        for (JsonElement el : arr) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            int codeA = o.has("currencyCodeA") ? o.get("currencyCodeA").getAsInt() : 0;
            int codeB = o.has("currencyCodeB") ? o.get("currencyCodeB").getAsInt() : 0;

            // Keep only pairs vs UAH (980)
            if (codeB != 980) {
                continue;
            }

            String currency = String.valueOf(codeA);
            double buy = o.has("rateBuy") && !o.get("rateBuy").isJsonNull() ? o.get("rateBuy").getAsDouble() : 0.0;
            double sell = o.has("rateSell") && !o.get("rateSell").isJsonNull() ? o.get("rateSell").getAsDouble() : 0.0;
            double cross = o.has("rateCross") && !o.get("rateCross").isJsonNull() ? o.get("rateCross").getAsDouble() : 0.0;

            result.add(new CurrencyRate(BANK_NAME, currency, buy, sell, cross, today));
        }

        debug("Mapped records: " + result.size());
        return result;
    }
}
