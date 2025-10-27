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
 * PrivatBank implementation of {@link BankRateService}.
 * Singleton instance with per-bank cache (1 hour) + singleton HttpClient.
 * Debug printing can be toggled with the DEBUG flag.
 */
public class PrivatBankService implements BankRateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrivatBankService.class);

    /** Singleton instance. */
    private static final PrivatBankService INSTANCE = new PrivatBankService();

    /** Toggle for temporary stdout debug prints. */
    private static final boolean DEBUG = true;

    private static final String BANK_NAME = "PrivatBank";

    private static final String API_URL =
            "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static final Duration CACHE_TTL = Duration.ofHours(1);
    private volatile List<CurrencyRate> cachedRates;

    private volatile Instant lastFetchAt;

    /** Private constructor to enforce singleton usage. */
    private PrivatBankService() {
    }

    private static void debug(String msg) {
        if (DEBUG) {
            System.out.println("[PrivatBankService] " + msg);
        }
    }

    /**
     * Provides global singleton instance of this service.
     *
     * @return the PrivatBankService singleton
     */
    public static PrivatBankService getInstance() {
        return INSTANCE;
    }

    @Override
    public String getBankName() {
        return BANK_NAME;
    }

    @Override
    public List<CurrencyRate> getRates() throws Exception {
        final Instant now = Instant.now();
        final List<CurrencyRate> current = cachedRates;
        final Instant fetchedAt = lastFetchAt;

        if (current != null && fetchedAt != null
                && Duration.between(fetchedAt, now).compareTo(CACHE_TTL) < 0) {
            debug("Cache HIT. Age=" + Duration.between(fetchedAt, now).toMinutes()
                    + "m; size=" + current.size());
            return current;
        }

        debug("Cache MISS/EXPIRED. hadCache=" + (current != null)
                + ", lastFetchAt=" + fetchedAt);

        synchronized (this) {
            if (cachedRates != null && lastFetchAt != null
                    && Duration.between(lastFetchAt, now).compareTo(CACHE_TTL) < 0) {
                debug("Cache became valid during wait. Returning cached. Age="
                        + Duration.between(lastFetchAt, now).toMinutes()
                        + "m; size=" + cachedRates.size());
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

    private List<CurrencyRate> fetchFromApi() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response =
                CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        debug("HTTP status=" + response.statusCode());

        if (response.statusCode() != 200) {
            LOGGER.warn("PrivatBank API returned status {}", response.statusCode());
            debug("Non-200 status. Returning empty list.");
            return List.of();
        }

        String body = response.body();
        JsonArray arr = JsonParser.parseString(body).getAsJsonArray();
        debug("Parsing JSON array. count=" + arr.size());

        List<CurrencyRate> result = new ArrayList<>(arr.size());
        LocalDate today = LocalDate.now();

        // PrivatBank returns objects like: { "ccy":"USD","base_ccy":"UAH","buy":"41.20","sale":"41.80" }
        for (JsonElement el : arr) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            String base = o.has("base_ccy") ? o.get("base_ccy").getAsString() : "UAH";
            if (!"UAH".equalsIgnoreCase(base)) {
                continue; // keep only pairs vs UAH
            }

            String currency = o.has("ccy") ? o.get("ccy").getAsString() : "";
            double buy = o.has("buy") ? parseDoubleSafe(o.get("buy").getAsString()) : 0.0;
            double sell = o.has("sale") ? parseDoubleSafe(o.get("sale").getAsString()) : 0.0;

            result.add(new CurrencyRate(BANK_NAME, currency, buy, sell, 0.0, today));
        }

        debug("Mapped records: " + result.size());
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
