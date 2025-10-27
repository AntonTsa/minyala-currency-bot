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
 * National Bank of Ukraine (NBU) implementation of {@link BankRateService}.
 * Singleton instance with per-bank cache (1 hour) + singleton HttpClient.
 * Debug printing can be toggled with the DEBUG flag.
 */
public class NbuService implements BankRateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NbuService.class);

    /** Singleton instance. */
    private static final NbuService INSTANCE = new NbuService();

    /** Private constructor to enforce singleton usage. */
    private NbuService() {
    }

    /**
     * Provides global singleton instance of this service.
     *
     * @return the NbuService singleton
     */
    public static NbuService getInstance() {
        return INSTANCE;
    }

    /** Toggle for temporary stdout debug prints. */
    private static final boolean DEBUG = true;

    private static final String BANK_NAME = "NBU";

    private static final String API_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private volatile List<CurrencyRate> cachedRates;
    private volatile Instant lastFetchAt;

    private static void debug(String msg) {
        if (DEBUG) {
            System.out.println("[NbuService] " + msg);
        }
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
     * NBU returns official rates once per day (fields: cc, r030, rate, txt, exchangedate).
     * We keep 'rate' in the {@code rate} field and leave buy/sell as 0.0.
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
            LOGGER.warn("NBU API returned status {}", response.statusCode());
            debug("Non-200 status. Returning empty list.");
            return List.of();
        }

        String body = response.body();
        JsonArray arr = JsonParser.parseString(body).getAsJsonArray();
        debug("Parsing JSON array. count=" + arr.size());

        List<CurrencyRate> result = new ArrayList<>(arr.size());

        for (JsonElement el : arr) {
            if (!el.isJsonObject()) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            String currency = o.has("cc") ? o.get("cc").getAsString() : "";
            double rate = o.has("rate") ? safeDouble(o.get("rate")) : 0.0;

            LocalDate date = LocalDate.now();
            if (o.has("exchangedate")) {
                try {
                    // NBU format like "27.10.2025"
                    String ds = o.get("exchangedate").getAsString();
                    String[] parts = ds.split("\\.");
                    if (parts.length == 3) {
                        int d = Integer.parseInt(parts[0]);
                        int m = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        date = LocalDate.of(y, m, d);
                    }
                } catch (Exception ignore) {
                    // fallback to today
                }
            }

            result.add(new CurrencyRate(BANK_NAME, currency, 0.0, 0.0, rate, date));
        }

        debug("Mapped records: " + result.size());
        return result;
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
