package com.example.CryptocurrencyMarketAnalysisSystem.repository;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.model.CandlestickEntity;
import com.example.CryptocurrencyMarketAnalysisSystem.service.CandlestickDataService;
import com.example.CryptocurrencyMarketAnalysisSystem.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CandlestickRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CandlestickRepository repository;

    @MockBean
    private CandlestickDataService candlestickDataService;

    private CandlestickEntity candlestickEntity;

    @BeforeEach
    public void setUp() {
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(DateUtils.convertDateToMillis("2023-07-01T00:00:00.000", ZoneId.of("UTC")));
        candlestick.setCloseTime(DateUtils.convertDateToMillis("2023-07-01T00:00:59.999", ZoneId.of("UTC")));
        candlestick.setOpen("1.0");
        candlestick.setClose("2.0");
        candlestick.setHigh("3.0");
        candlestick.setLow("0.5");
        candlestick.setVolume("1000");
        candlestick.setQuoteAssetVolume("2000");
        candlestick.setNumberOfTrades(500L);
        candlestick.setTakerBuyBaseAssetVolume("700");
        candlestick.setTakerBuyQuoteAssetVolume("1400");

        candlestickEntity = CandlestickEntity.fromCandlestick(candlestick, "BTCUSDT", CandlestickInterval.ONE_MINUTE);
        System.out.println("Persisting entity: " + candlestickEntity);
        entityManager.persistAndFlush(candlestickEntity);
    }

    // Positive Tests

    @Test
    public void testFindBySymbolAndOpenTimeBetweenAndInterval() {
        List<CandlestickEntity> results = repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 1, 0, 0, ZoneId.of("UTC")),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(results).hasSize(1).contains(candlestickEntity);
    }

    @Test
    public void testExistsBySymbolAndOpenTimeAndCloseTimeAndInterval() {
        boolean exists = repository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 0, 59, 999_000_000, ZoneId.of("UTC")),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(exists).isTrue();
    }

    // Negative Tests

    @Test
    public void testFindBySymbolAndOpenTimeBetweenAndInterval_InvalidSymbol() {
        List<CandlestickEntity> results = repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "INVALID_SYMBOL",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 1, 0, 0, ZoneId.of("UTC")),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(results).isEmpty();
    }

    @Test
    public void testFindBySymbolAndOpenTimeBetweenAndInterval_NoMatchingInterval() {
        List<CandlestickEntity> results = repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 1, 0, 0, ZoneId.of("UTC")),
                CandlestickInterval.HOURLY
        );
        assertThat(results).isEmpty();
    }

    @Test
    public void testExistsBySymbolAndOpenTimeAndCloseTimeAndInterval_InvalidSymbol() {
        boolean exists = repository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
                "INVALID_SYMBOL",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 0, 59, 999_000_000, ZoneId.of("UTC")),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(exists).isFalse();
    }

    @Test
    public void testExistsBySymbolAndOpenTimeAndCloseTimeAndInterval_NoMatchingInterval() {
        boolean exists = repository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 0, 59, 999_000_000, ZoneId.of("UTC")),
                CandlestickInterval.HOURLY
        );
        assertThat(exists).isFalse();
    }

    @Test
    public void testFindBySymbolAndOpenTimeBetweenAndInterval_NoMatchingTimeRange() {
        List<CandlestickEntity> results = repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 6, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 6, 1, 0, 0, 30, 0, ZoneId.of("UTC")),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(results).isEmpty();
    }

    @Test
    public void testExistsBySymbolAndOpenTimeAndCloseTimeAndInterval_NoMatchingTimeRange() {
        boolean exists = repository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 1, 59, 999_000_000, ZoneId.of("UTC")),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(exists).isFalse();
    }

    // Boundary Tests

    @Test
    public void testFindBySymbolAndOpenTimeBetweenAndInterval_StartTimeBoundary() {
        ZonedDateTime startTime = candlestickEntity.getOpenTime();
        ZonedDateTime endTime = candlestickEntity.getOpenTime().plusMinutes(1);

        System.out.println("Testing with start time: " + startTime + " and end time: " + endTime);

        List<CandlestickEntity> results = repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "BTCUSDT",
                startTime,
                endTime,
                CandlestickInterval.ONE_MINUTE
        );
        System.out.println("Query results: " + results);

        assertThat(results).hasSize(1).contains(candlestickEntity);
    }

    @Test
    public void testFindBySymbolAndOpenTimeBetweenAndInterval_EndTimeBoundary() {
        List<CandlestickEntity> results = repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "BTCUSDT",
                candlestickEntity.getCloseTime().minusMinutes(1),
                candlestickEntity.getCloseTime(),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(results).hasSize(1).contains(candlestickEntity);
    }

    @Test
    public void testExistsBySymbolAndOpenTimeAndCloseTimeAndInterval_OpenTimeBoundary() {
        boolean exists = repository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
                "BTCUSDT",
                candlestickEntity.getOpenTime(),
                candlestickEntity.getCloseTime(),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(exists).isTrue();
    }

    @Test
    public void testExistsBySymbolAndOpenTimeAndCloseTimeAndInterval_CloseTimeBoundary() {
        boolean exists = repository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
                "BTCUSDT",
                candlestickEntity.getOpenTime(),
                candlestickEntity.getCloseTime(),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(exists).isTrue();
    }

    @Test
    public void testFindBySymbolAndOpenTimeBetweenAndInterval_SmallestValidInterval() {
        List<CandlestickEntity> results = repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 1, 0, 0, ZoneId.of("UTC")),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(results).hasSize(1).contains(candlestickEntity);
    }

    @Test
    public void testFindBySymbolAndOpenTimeBetweenAndInterval_LargestValidInterval() {
        List<CandlestickEntity> results = repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 2, 0, 0, 0, 0, ZoneId.of("UTC")),
                CandlestickInterval.DAILY
        );
        assertThat(results).isEmpty(); // Assuming no data for this interval
    }


    // Output Verification Tests

    @Test
    public void testFindBySymbolAndOpenTimeBetweenAndInterval_OutputVerification() {
        List<CandlestickEntity> results = repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 0, 59, 999_000_000, ZoneId.of("UTC")),
                CandlestickInterval.ONE_MINUTE
        );
        System.out.println("Query results: " + results);
        assertThat(results).hasSize(1);
        CandlestickEntity result = results.get(0);
        assertThat(result.getOpen()).isEqualTo(1.0);
        assertThat(result.getClose()).isEqualTo(2.0);
        assertThat(result.getHigh()).isEqualTo(3.0);
        assertThat(result.getLow()).isEqualTo(0.5);
        assertThat(result.getVolume()).isEqualTo(1000);
        assertThat(result.getQuoteAssetVolume()).isEqualTo(2000);
        assertThat(result.getNumberOfTrades()).isEqualTo(500);
        assertThat(result.getTakerBuyBaseAssetVolume()).isEqualTo(700);
        assertThat(result.getTakerBuyQuoteAssetVolume()).isEqualTo(1400);
    }

    @Test
    public void testExistsBySymbolAndOpenTimeAndCloseTimeAndInterval_OutputVerification() {
        boolean exists = repository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
                "BTCUSDT",
                candlestickEntity.getOpenTime(),
                candlestickEntity.getCloseTime(),
                CandlestickInterval.ONE_MINUTE
        );
        assertThat(exists).isTrue();
    }

    @Test
    public void testFindBySymbolAndOpenTimeBetweenAndInterval_InvalidIntervalOutputVerification() {
        List<CandlestickEntity> results = repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 0, 59, 999_000_000, ZoneId.of("UTC")),
                CandlestickInterval.HOURLY
        );
        assertThat(results).isEmpty();
    }

    @Test
    public void testExistsBySymbolAndOpenTimeAndCloseTimeAndInterval_InvalidIntervalOutputVerification() {
        boolean exists = repository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
                "BTCUSDT",
                candlestickEntity.getOpenTime(),
                candlestickEntity.getCloseTime(),
                CandlestickInterval.HOURLY
        );
        assertThat(exists).isFalse();
    }
}
