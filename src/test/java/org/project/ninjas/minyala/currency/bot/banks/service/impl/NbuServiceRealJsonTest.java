package org.project.ninjas.minyala.currency.bot.banks.service.impl;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.util.HttpClientProvider;

/**
 * Unit test for {@link NbuService} using real JSON content from NBU API.
 * Validates correct parsing and one-hour cache behavior.
 */
public class NbuServiceRealJsonTest {

    private static final String REAL_NBU_JSON = """
            [
             {"r030":840,"txt":"Долар США","rate":42.0831,"cc":"USD","exchangedate":"29.10.2025"},
             {"r030":978,"txt":"Євро","rate":48.9784,"cc":"EUR","exchangedate":"29.10.2025"},
             {"r030":392,"txt":"Єна","rate":0.27656,"cc":"JPY","exchangedate":"29.10.2025"}
            ]
            """;

    private NbuService service;
    private HttpClient httpClientMock;
    private HttpResponse<String> httpResponseMock;

    @BeforeEach
    void setUp() throws Exception {
        service = NbuService.getInstance();
        httpClientMock = mock(HttpClient.class);
        httpResponseMock = mock(HttpResponse.class);

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(REAL_NBU_JSON);
        when(httpClientMock.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
    }

    @Test
    void testRealJsonParsingAndCache() throws Exception {
        try (MockedStatic<HttpClientProvider> mockedProvider =
                     Mockito.mockStatic(HttpClientProvider.class)) {
            mockedProvider.when(HttpClientProvider::getClient).thenReturn(httpClientMock);

            // First call → should fetch and parse data
            List<CurrencyRate> first = service.getRates();
            Assertions.assertFalse(first.isEmpty(), "Should parse real NBU JSON data");
            verify(httpClientMock, times(1)).send(any(), any());

            // Second call → should return cached data (no new HTTP calls)
            List<CurrencyRate> second = service.getRates();
            Assertions.assertSame(first, second, "Should return cached data");
            verify(httpClientMock, times(1)).send(any(), any());

            // Verify correctness for a known entry
            CurrencyRate usd = first.stream()
                    .filter(r -> "USD".equals(r.getCurrency()))
                    .findFirst()
                    .orElseThrow();
            Assertions.assertEquals("NBU", usd.getBankName());
            Assertions.assertEquals(42.0831, usd.getRate(), 0.0001);
        }
    }
}
