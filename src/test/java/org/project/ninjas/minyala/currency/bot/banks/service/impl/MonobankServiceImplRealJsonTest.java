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
 * Verifies MonobankService parsing and cache behavior using a mocked HTTP client.
 */
public class MonobankServiceImplRealJsonTest {

    private static final String REAL_MONOBANK_JSON = """
            [{"currencyCodeA":840,"currencyCodeB":980,"date":1761632406,"rateBuy":41.8,"rateSell":42.1905},
             {"currencyCodeA":978,"currencyCodeB":980,"date":1761636606,"rateBuy":48.7,"rateSell":49.3998},
             {"currencyCodeA":978,"currencyCodeB":840,"date":1761632406,"rateBuy":1.16,"rateSell":1.17},
             {"currencyCodeA":826,"currencyCodeB":980,"date":1761659821,"rateCross":56.3542},
             {"currencyCodeA":392,"currencyCodeB":980,"date":1761659546,"rateCross":0.2765}]
            """;

    private MonobankServiceImpl service;
    private HttpClient httpClientMock;

    @BeforeEach
    void setUp() throws Exception {
        service = MonobankServiceImpl.getInstance();
        httpClientMock = mock(HttpClient.class);
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(REAL_MONOBANK_JSON);
        when(httpClientMock.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
    }

    @Test
    void testRealJsonParsingAndCache() throws Exception {
        try (MockedStatic<HttpClientProvider> mocked = Mockito.mockStatic(HttpClientProvider.class)) {
            mocked.when(HttpClientProvider::getClient).thenReturn(httpClientMock);

            // First call → should fetch and parse data
            List<CurrencyRate> first = service.getRates();
            Assertions.assertFalse(first.isEmpty(), "Should parse real JSON data");
            verify(httpClientMock, times(1)).send(any(), any());

            // Second call → should use cached data
            List<CurrencyRate> second = service.getRates();
            Assertions.assertSame(first, second, "Should return cached data");
            verify(httpClientMock, times(1)).send(any(), any());

            // Check content
            CurrencyRate usd = first.stream()
                    .filter(r -> "840".equals(r.getCurrency()))
                    .findFirst()
                    .orElseThrow();
            Assertions.assertEquals("Monobank", usd.getBankName());
            Assertions.assertEquals(41.8, usd.getBuy());
            Assertions.assertEquals(42.1905, usd.getSell());
        }
    }
}
