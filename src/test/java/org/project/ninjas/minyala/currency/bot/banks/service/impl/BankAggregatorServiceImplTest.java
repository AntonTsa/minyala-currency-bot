package org.project.ninjas.minyala.currency.bot.banks.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.service.BankRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for {@link BankAggregatorServiceImpl}.
 */
public class BankAggregatorServiceImplTest {

    private BankRateService monoMock;
    private BankRateService privatMock;
    private BankRateService nbuMock;
    private BankAggregatorServiceImpl aggregator;

    @BeforeEach
    void setUp() throws Exception {
        monoMock = mock(BankRateService.class);
        privatMock = mock(BankRateService.class);
        nbuMock = mock(BankRateService.class);

        aggregator = BankAggregatorServiceImpl.getInstance();

        Field field = BankAggregatorServiceImpl.class.getDeclaredField("bankServices");
        field.setAccessible(true);
        List<BankRateService> mocks = Arrays.asList(monoMock, privatMock, nbuMock);
        field.set(aggregator, mocks);
    }

    @Test
    void testSingletonInstance() {
        BankAggregatorServiceImpl instance1 = BankAggregatorServiceImpl.getInstance();
        BankAggregatorServiceImpl instance2 = BankAggregatorServiceImpl.getInstance();
        assertSame(instance1, instance2, "There must be he same instance (singleton)");
    }

    @Test
    void testGetAllRates_successfulAggregation() throws Exception {
        CurrencyRate rate1 = new CurrencyRate("Monobank", "USD", 38.5, 38.5, 38.5, LocalDate.now());
        CurrencyRate rate2 = new CurrencyRate("NBU", "USD", 38.5, 38.5, 38.5, LocalDate.now());

        when(monoMock.getRates()).thenReturn(Collections.singletonList(rate1));
        when(privatMock.getRates()).thenReturn(Collections.singletonList(rate2));
        when(nbuMock.getRates()).thenReturn(Collections.emptyList());

        List<CurrencyRate> result = aggregator.getAllRates();

        assertEquals(2, result.size());
        assertTrue(result.contains(rate1));
        assertTrue(result.contains(rate2));

        verify(monoMock).getRates();
        verify(privatMock).getRates();
        verify(nbuMock).getRates();
    }

    @Test
    void testGetAllRates_withExceptionInOneService() throws Exception {
        CurrencyRate rate = new CurrencyRate("Monobank", "USD", 38.5, 38.5, 38.5, LocalDate.now());

        when(monoMock.getRates()).thenReturn(Collections.singletonList(rate));
        when(privatMock.getRates()).thenThrow(new RuntimeException("Connection error"));
        when(privatMock.getBankName()).thenReturn("PrivatBank");
        when(nbuMock.getRates()).thenReturn(Collections.emptyList());

        List<CurrencyRate> result = aggregator.getAllRates();

        assertEquals(1, result.size());
        assertEquals("USD", result.getFirst().getCurrency());
        verify(privatMock).getBankName();
    }

    @Test
    void testPrivateConstructorAccessibleViaReflection() throws Exception {
        Constructor<BankAggregatorServiceImpl> constructor =
                BankAggregatorServiceImpl.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        BankAggregatorServiceImpl newInstance = constructor.newInstance();
        assertNotNull(newInstance);
    }

    @Test
    void testLoggerFieldExists() throws Exception {
        Field loggerField = BankAggregatorServiceImpl.class.getDeclaredField("LOGGER");
        loggerField.setAccessible(true);
        Logger logger = (Logger) loggerField.get(null);
        assertEquals(LoggerFactory.getLogger(BankAggregatorServiceImpl.class).getName(), logger.getName());
    }
}
