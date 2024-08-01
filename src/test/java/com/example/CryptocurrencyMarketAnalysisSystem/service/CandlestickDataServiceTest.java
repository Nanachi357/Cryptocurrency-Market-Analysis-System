package com.example.CryptocurrencyMarketAnalysisSystem.service;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.model.CandlestickEntity;
import com.example.CryptocurrencyMarketAnalysisSystem.repository.CandlestickRepository;
import com.example.CryptocurrencyMarketAnalysisSystem.util.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class CandlestickDataServiceTest {

    @Autowired
    private CandlestickDataService candlestickDataService;

    @Autowired
    private CandlestickRepository candlestickRepository;

    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        candlestickRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    //Positive Tests

    @Test
    public void testSaveCandlestickData() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.FOUR_HOURLY;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L); // 2021-01-01 00:00:00 UTC
        candlestick.setOpen("30000.00");
        candlestick.setHigh("32000.00");
        candlestick.setLow("29000.00");
        candlestick.setClose("31000.00");
        candlestick.setVolume("100.0");
        candlestick.setCloseTime(1609473599999L); // Закриття через 1 мс перед наступним відкриттям
        candlestick.setQuoteAssetVolume("3000000.0");
        candlestick.setNumberOfTrades(1000L);
        candlestick.setTakerBuyBaseAssetVolume("50.0");
        candlestick.setTakerBuyQuoteAssetVolume("1500000.0");

        candlestickDataService.saveCandlestickData(symbol, candlestick, interval);

        ZonedDateTime startTime = DateUtils.convertMillisToUtcZonedDateTime(1609459200000L);
        ZonedDateTime endTime = DateUtils.convertMillisToUtcZonedDateTime(1609473599999L);

        List<CandlestickEntity> savedCandlesticks = candlestickRepository.findBySymbolAndOpenTimeBetweenAndInterval(symbol, startTime, endTime, interval);
        assertEquals(1, savedCandlesticks.size());
        CandlestickEntity savedEntity = savedCandlesticks.get(0);

        assertEquals(symbol, savedCandlesticks.get(0).getSymbol());
        assertEquals(interval, savedCandlesticks.get(0).getCandlestickInterval());

        assertEquals(30000.00, savedEntity.getOpen());
        assertEquals(31000.00, savedEntity.getClose());
        assertEquals(32000.00, savedEntity.getHigh());
        assertEquals(29000.00, savedEntity.getLow());
        assertEquals(100.0, savedEntity.getVolume());
        assertEquals(3000000.0, savedEntity.getQuoteAssetVolume());
        assertEquals(1000L, savedEntity.getNumberOfTrades());
        assertEquals(50.0, savedEntity.getTakerBuyBaseAssetVolume());
        assertEquals(1500000.0, savedEntity.getTakerBuyQuoteAssetVolume());
    }

    @Test
    public void testGetCandlestickData() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = getCandlestick();

        candlestickDataService.saveCandlestickData(symbol, candlestick, interval);

        ZonedDateTime startTime = DateUtils.convertMillisToUtcZonedDateTime(1609459200000L);
        ZonedDateTime endTime = DateUtils.convertMillisToUtcZonedDateTime(1609459260000L);

        List<Candlestick> retrievedCandlesticks = candlestickDataService.getCandlestickData(symbol, startTime, endTime, interval);
        assertEquals(1, retrievedCandlesticks.size());
        Candlestick retrievedCandlestick = retrievedCandlesticks.get(0);

        assertEquals(1609459200000L, retrievedCandlestick.getOpenTime());
        assertEquals(30000.00, Double.parseDouble(retrievedCandlestick.getOpen()));
        assertEquals(31000.00, Double.parseDouble(retrievedCandlestick.getClose()));
        assertEquals(32000.00, Double.parseDouble(retrievedCandlestick.getHigh()));
        assertEquals(29000.00, Double.parseDouble(retrievedCandlestick.getLow()));
        assertEquals(100.0, Double.parseDouble(retrievedCandlestick.getVolume()));
        assertEquals(3000000.0, Double.parseDouble(retrievedCandlestick.getQuoteAssetVolume()));
        assertEquals(1000L, retrievedCandlestick.getNumberOfTrades());
        assertEquals(50.0, Double.parseDouble(retrievedCandlestick.getTakerBuyBaseAssetVolume()));
        assertEquals(1500000.0, Double.parseDouble(retrievedCandlestick.getTakerBuyQuoteAssetVolume()));
    }

    @NotNull
    private static Candlestick getCandlestick() {
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L); // 2021-01-01 00:00:00 UTC
        candlestick.setOpen("30000.00");
        candlestick.setHigh("32000.00");
        candlestick.setLow("29000.00");
        candlestick.setClose("31000.00");
        candlestick.setVolume("100.0");
        candlestick.setCloseTime(1609459260000L); // 2021-01-01 00:01:00 UTC
        candlestick.setQuoteAssetVolume("3000000.0");
        candlestick.setNumberOfTrades(1000L);
        candlestick.setTakerBuyBaseAssetVolume("50.0");
        candlestick.setTakerBuyQuoteAssetVolume("1500000.0");
        return candlestick;
    }

    @Test
    public void testExistsBySymbolAndOpenTime() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L); // 2021-01-01 00:00:00 UTC
        candlestick.setOpen("30000.00");
        candlestick.setHigh("32000.00");
        candlestick.setLow("29000.00");
        candlestick.setClose("31000.00");
        candlestick.setVolume("100.0");
        candlestick.setCloseTime(1609459260000L); // 2021-01-01 00:01:00 UTC
        candlestick.setQuoteAssetVolume("3000000.0");
        candlestick.setNumberOfTrades(1000L);
        candlestick.setTakerBuyBaseAssetVolume("50.0");
        candlestick.setTakerBuyQuoteAssetVolume("1500000.0");

        candlestickDataService.saveCandlestickData(symbol, candlestick, interval);

        ZonedDateTime openTime = DateUtils.convertMillisToUtcZonedDateTime(1609459200000L);
        ZonedDateTime closeTime = DateUtils.convertMillisToUtcZonedDateTime(1609459260000L);

        boolean exists = candlestickDataService.existsBySymbolAndOpenTime(symbol, openTime, closeTime, interval);
        assertTrue(exists);
    }

    //Negative Tests

    @Test
    public void testSaveCandlestickDataWithNullOpenTime() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(null);
        candlestick.setCloseTime(1609459260000L); // 2021-01-01 00:01:00 UTC
        candlestick.setOpen("30000.00");
        candlestick.setHigh("32000.00");
        candlestick.setLow("29000.00");
        candlestick.setClose("31000.00");
        candlestick.setVolume("100.0");
        candlestick.setQuoteAssetVolume("3000000.0");
        candlestick.setNumberOfTrades(1000L);
        candlestick.setTakerBuyBaseAssetVolume("50.0");
        candlestick.setTakerBuyQuoteAssetVolume("1500000.0");

        assertThrows(IllegalArgumentException.class, () -> candlestickDataService.saveCandlestickData(symbol, candlestick, interval));
    }

    @Test
    public void testSaveCandlestickDataWithNullSymbol() {
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L); // 2021-01-01 00:00:00 UTC
        candlestick.setCloseTime(1609459260000L); // 2021-01-01 00:01:00 UTC
        candlestick.setOpen("30000.00");
        candlestick.setHigh("32000.00");
        candlestick.setLow("29000.00");
        candlestick.setClose("31000.00");
        candlestick.setVolume("100.0");
        candlestick.setQuoteAssetVolume("3000000.0");
        candlestick.setNumberOfTrades(1000L);
        candlestick.setTakerBuyBaseAssetVolume("50.0");
        candlestick.setTakerBuyQuoteAssetVolume("1500000.0");

        assertThrows(IllegalArgumentException.class, () -> candlestickDataService.saveCandlestickData(null, candlestick, interval));
    }

    @Test
    public void testSaveCandlestickDataWithInvalidNumericValues() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L); // 2021-01-01 00:00:00 UTC
        candlestick.setCloseTime(1609459260000L); // 2021-01-01 00:01:00 UTC
        candlestick.setOpen("invalid");
        candlestick.setHigh("32000.00");
        candlestick.setLow("29000.00");
        candlestick.setClose("31000.00");
        candlestick.setVolume("100.0");
        candlestick.setQuoteAssetVolume("3000000.0");
        candlestick.setNumberOfTrades(1000L);
        candlestick.setTakerBuyBaseAssetVolume("50.0");
        candlestick.setTakerBuyQuoteAssetVolume("1500000.0");

        assertThrows(NumberFormatException.class, () -> candlestickDataService.saveCandlestickData(symbol, candlestick, interval));
    }

    @Test
    public void testSaveCandlestickDataWithInvalidDates() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459260000L); // 2021-01-01 00:01:00 UTC
        candlestick.setCloseTime(1609459200000L); // 2021-01-01 00:00:00 UTC
        candlestick.setOpen("30000.00");
        candlestick.setHigh("32000.00");
        candlestick.setLow("29000.00");
        candlestick.setClose("31000.00");
        candlestick.setVolume("100.0");
        candlestick.setQuoteAssetVolume("3000000.0");
        candlestick.setNumberOfTrades(1000L);
        candlestick.setTakerBuyBaseAssetVolume("50.0");
        candlestick.setTakerBuyQuoteAssetVolume("1500000.0");

        assertThrows(IllegalArgumentException.class, () -> candlestickDataService.saveCandlestickData(symbol, candlestick, interval));
    }

    @Test
    public void testGetCandlestickDataWithInvalidSymbol() {
        String symbol = "INVALID";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        ZonedDateTime startTime = DateUtils.convertMillisToUtcZonedDateTime(1609459200000L); // 2021-01-01 00:00:00 UTC
        ZonedDateTime endTime = DateUtils.convertMillisToUtcZonedDateTime(1609459260000L); // 2021-01-01 00:01:00 UTC

        List<Candlestick> results = candlestickDataService.getCandlestickData(symbol, startTime, endTime, interval);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetCandlestickDataWithInvalidTimeRange() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        ZonedDateTime startTime = DateUtils.convertMillisToUtcZonedDateTime(1609459260000L); // 2021-01-01 00:01:00 UTC
        ZonedDateTime endTime = DateUtils.convertMillisToUtcZonedDateTime(1609459200000L); // 2021-01-01 00:00:00 UTC

        List<Candlestick> results = candlestickDataService.getCandlestickData(symbol, startTime, endTime, interval);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testExistsBySymbolAndOpenTimeWithInvalidSymbol() {
        String symbol = "INVALID";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        ZonedDateTime openTime = DateUtils.convertMillisToUtcZonedDateTime(1609459200000L); // 2021-01-01 00:00:00 UTC
        ZonedDateTime closeTime = DateUtils.convertMillisToUtcZonedDateTime(1609459260000L); // 2021-01-01 00:01:00 UTC

        boolean exists = candlestickDataService.existsBySymbolAndOpenTime(symbol, openTime, closeTime, interval);
        assertFalse(exists);
    }

    @Test
    public void testExistsBySymbolAndOpenTimeWithInvalidTimeRange() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        ZonedDateTime openTime = DateUtils.convertMillisToUtcZonedDateTime(1609459260000L); // 2021-01-01 00:01:00 UTC
        ZonedDateTime closeTime = DateUtils.convertMillisToUtcZonedDateTime(1609459200000L); // 2021-01-01 00:00:00 UTC

        boolean exists = candlestickDataService.existsBySymbolAndOpenTime(symbol, openTime, closeTime, interval);
        assertFalse(exists);
    }

    //Boundary Tests

    @Test
    public void testSaveCandlestickDataWithMinDates() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(Long.MIN_VALUE);
        candlestick.setCloseTime(Long.MIN_VALUE + 1);
        candlestick.setOpen("0.01");
        candlestick.setHigh("0.02");
        candlestick.setLow("0.01");
        candlestick.setClose("0.02");
        candlestick.setVolume("0.001");
        candlestick.setQuoteAssetVolume("0.002");
        candlestick.setNumberOfTrades(1L);
        candlestick.setTakerBuyBaseAssetVolume("0.0005");
        candlestick.setTakerBuyQuoteAssetVolume("0.001");

        assertThrows(IllegalArgumentException.class, () -> candlestickDataService.saveCandlestickData(symbol, candlestick, interval));
    }

    @Test
    public void testSaveCandlestickDataWithMaxDates() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(Long.MAX_VALUE - 1);
        candlestick.setCloseTime(Long.MAX_VALUE);
        candlestick.setOpen("1000000.00");
        candlestick.setHigh("1000000.00");
        candlestick.setLow("1000000.00");
        candlestick.setClose("1000000.00");
        candlestick.setVolume("1000000.0");
        candlestick.setQuoteAssetVolume("1000000.0");
        candlestick.setNumberOfTrades(Long.MAX_VALUE);
        candlestick.setTakerBuyBaseAssetVolume("1000000.0");
        candlestick.setTakerBuyQuoteAssetVolume("1000000.0");

        assertThrows(IllegalArgumentException.class, () -> candlestickDataService.saveCandlestickData(symbol, candlestick, interval));
    }

    @Test
    public void testSaveCandlestickDataWithMinValues() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L);
        candlestick.setCloseTime(1609459260000L);
        candlestick.setOpen("0.0");
        candlestick.setHigh("0.0");
        candlestick.setLow("0.0");
        candlestick.setClose("0.0");
        candlestick.setVolume("0.0");
        candlestick.setQuoteAssetVolume("0.0");
        candlestick.setNumberOfTrades(0L);
        candlestick.setTakerBuyBaseAssetVolume("0.0");
        candlestick.setTakerBuyQuoteAssetVolume("0.0");

        candlestickDataService.saveCandlestickData(symbol, candlestick, interval);

        List<CandlestickEntity> savedCandlesticks = candlestickRepository.findBySymbolAndOpenTimeBetweenAndInterval(symbol,
                DateUtils.convertMillisToUtcZonedDateTime(1609459200000L),
                DateUtils.convertMillisToUtcZonedDateTime(1609459260000L), interval);
        assertEquals(1, savedCandlesticks.size());
    }

    @Test
    public void testSaveCandlestickDataWithMaxValues() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L);
        candlestick.setCloseTime(1609459260000L);
        candlestick.setOpen(String.valueOf(Double.MAX_VALUE));
        candlestick.setHigh(String.valueOf(Double.MAX_VALUE));
        candlestick.setLow(String.valueOf(Double.MAX_VALUE));
        candlestick.setClose(String.valueOf(Double.MAX_VALUE));
        candlestick.setVolume(String.valueOf(Double.MAX_VALUE));
        candlestick.setQuoteAssetVolume(String.valueOf(Double.MAX_VALUE));
        candlestick.setNumberOfTrades(Long.MAX_VALUE);
        candlestick.setTakerBuyBaseAssetVolume(String.valueOf(Double.MAX_VALUE));
        candlestick.setTakerBuyQuoteAssetVolume(String.valueOf(Double.MAX_VALUE));

        candlestickDataService.saveCandlestickData(symbol, candlestick, interval);

        List<CandlestickEntity> savedCandlesticks = candlestickRepository.findBySymbolAndOpenTimeBetweenAndInterval(symbol,
                DateUtils.convertMillisToUtcZonedDateTime(1609459200000L),
                DateUtils.convertMillisToUtcZonedDateTime(1609459260000L), interval);
        assertEquals(1, savedCandlesticks.size());
    }

    @Test
    public void testSaveCandlestickDataWithMinVolume() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L);
        candlestick.setCloseTime(1609459260000L);
        candlestick.setOpen("0.01");
        candlestick.setHigh("0.02");
        candlestick.setLow("0.01");
        candlestick.setClose("0.02");
        candlestick.setVolume("0.0");
        candlestick.setQuoteAssetVolume("0.002");
        candlestick.setNumberOfTrades(1L);
        candlestick.setTakerBuyBaseAssetVolume("0.0005");
        candlestick.setTakerBuyQuoteAssetVolume("0.001");

        candlestickDataService.saveCandlestickData(symbol, candlestick, interval);

        List<CandlestickEntity> savedCandlesticks = candlestickRepository.findBySymbolAndOpenTimeBetweenAndInterval(symbol,
                DateUtils.convertMillisToUtcZonedDateTime(1609459200000L),
                DateUtils.convertMillisToUtcZonedDateTime(1609459260000L), interval);
        assertEquals(1, savedCandlesticks.size());
    }

    @Test
    public void testSaveCandlestickDataWithMaxVolume() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L);
        candlestick.setCloseTime(1609459260000L);
        candlestick.setOpen("0.01");
        candlestick.setHigh("0.02");
        candlestick.setLow("0.01");
        candlestick.setClose("0.02");
        candlestick.setVolume(String.valueOf(Double.MAX_VALUE));
        candlestick.setQuoteAssetVolume(String.valueOf(Double.MAX_VALUE));
        candlestick.setNumberOfTrades(1L);
        candlestick.setTakerBuyBaseAssetVolume("0.0005");
        candlestick.setTakerBuyQuoteAssetVolume("0.001");

        candlestickDataService.saveCandlestickData(symbol, candlestick, interval);

        List<CandlestickEntity> savedCandlesticks = candlestickRepository.findBySymbolAndOpenTimeBetweenAndInterval(symbol,
                DateUtils.convertMillisToUtcZonedDateTime(1609459200000L),
                DateUtils.convertMillisToUtcZonedDateTime(1609459260000L), interval);
        assertEquals(1, savedCandlesticks.size());
    }

    @Test
    public void testSaveCandlestickDataWithMinNumericValues() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L); // Valid date
        candlestick.setCloseTime(1609459260000L); // Valid date
        candlestick.setOpen("0.0"); 
        candlestick.setHigh("0.0"); 
        candlestick.setLow("0.0"); 
        candlestick.setClose("0.0"); 
        candlestick.setVolume("0.0"); 
        candlestick.setQuoteAssetVolume("0.0"); 
        candlestick.setNumberOfTrades(0L); 
        candlestick.setTakerBuyBaseAssetVolume("0.0"); 
        candlestick.setTakerBuyQuoteAssetVolume("0.0"); 

        assertDoesNotThrow(() -> candlestickDataService.saveCandlestickData(symbol, candlestick, interval));
    }

    @Test
    public void testSaveCandlestickDataWithNegativeNumericValues() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L); // Valid date
        candlestick.setCloseTime(1609459260000L); // Valid date
        candlestick.setOpen("-1.0"); 
        candlestick.setHigh("-1.0"); 
        candlestick.setLow("-1.0"); 
        candlestick.setClose("-1.0"); 
        candlestick.setVolume("-1.0"); 
        candlestick.setQuoteAssetVolume("-1.0"); 
        candlestick.setNumberOfTrades(-1L); 
        candlestick.setTakerBuyBaseAssetVolume("-1.0"); 
        candlestick.setTakerBuyQuoteAssetVolume("-1.0"); 

        assertThrows(IllegalArgumentException.class, () -> candlestickDataService.saveCandlestickData(symbol, candlestick, interval));
    }

    @Test
    public void testSaveCandlestickDataWithMinNumberOfTrades() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1609459200000L); // Valid date
        candlestick.setCloseTime(1609459260000L); // Valid date
        candlestick.setOpen("30000.00");
        candlestick.setHigh("32000.00");
        candlestick.setLow("29000.00");
        candlestick.setClose("31000.00");
        candlestick.setVolume("100.0");
        candlestick.setQuoteAssetVolume("3000000.0");
        candlestick.setNumberOfTrades(-1L);
        candlestick.setTakerBuyBaseAssetVolume("50.0");
        candlestick.setTakerBuyQuoteAssetVolume("1500000.0");

        assertThrows(IllegalArgumentException.class, () -> candlestickDataService.saveCandlestickData(symbol, candlestick, interval));
    }


}
