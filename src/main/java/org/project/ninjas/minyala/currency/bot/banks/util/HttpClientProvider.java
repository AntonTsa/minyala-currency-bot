package org.project.ninjas.minyala.currency.bot.banks.util;

import java.net.http.HttpClient;

/**
 * Provides a single reusable {@link HttpClient} instance for all bank services.
 * Using a shared client allows connection reuse, reduces resource load,
 * and improves DNS caching performance.
 */
public final class HttpClientProvider {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private HttpClientProvider() {
        // Utility class; prevent instantiation
    }

    /**
     * Returns the shared singleton {@link HttpClient} used across the application.
     *
     * @return the shared HttpClient instance
     */
    public static HttpClient getClient() {
        return CLIENT;
    }
}
