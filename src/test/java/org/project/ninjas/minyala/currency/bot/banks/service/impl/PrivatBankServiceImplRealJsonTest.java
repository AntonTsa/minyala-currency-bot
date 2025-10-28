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
 * Unit test for {@link PrivatBankServiceImpl} using real PrivatBank JSON data.
 * Verifies correct parsing and cache behavior with mocked HTTP client.
 */
public class PrivatBankServiceImplRealJsonTest {

    private static final String REAL_PRIVAT_JSON = """
            [
             {"ccy":"EUR","base_ccy":"UAH","buy":"48.40000","sale":"49.40000"},
             {"ccy":"USD","base_ccy":"UAH","buy":"41.65000","sale":"42.25000"}
            ]
            """;

    private PrivatBankServiceImpl service;
    private HttpClient httpClientMock;

    @BeforeEach
    void setUp() throws Exception {
        service = PrivatBankServiceImpl.getInstance();
        httpClientMock = mock(HttpClient.class);
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);

        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(REAL_PRIVAT_JSON);
        when(httpClientMock.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
    }

    @Test
    void testRealJsonParsingAndCache() throws Exception {
        try (MockedStatic<HttpClientProvider> mockedProvider =
                     Mockito.mockStatic(HttpClientProvider.class)) {
            mockedProvider.when(HttpClientProvider::getClient).thenReturn(httpClientMock);

            // First call → should parse JSON
            List<CurrencyRate> first = service.getRates();
            Assertions.assertFalse(first.isEmpty(), "Should parse PrivatBank JSON");
            verify(httpClientMock, times(1)).send(any(), any());

            // Second call → should use cache
            List<CurrencyRate> second = service.getRates();
            Assertions.assertSame(first, second, "Should reuse cached data");
            verify(httpClientMock, times(1)).send(any(), any());

            // Verify one known entry
            CurrencyRate usd = first.stream()
                    .filter(r -> "USD".equals(r.getCurrency()))
                    .findFirst()
                    .orElseThrow();
            Assertions.assertEquals("PrivatBank", usd.getBankName());
            Assertions.assertEquals(41.65, usd.getBuy(), 0.0001);
            Assertions.assertEquals(42.25, usd.getSell(), 0.0001);
        }
    }
}
