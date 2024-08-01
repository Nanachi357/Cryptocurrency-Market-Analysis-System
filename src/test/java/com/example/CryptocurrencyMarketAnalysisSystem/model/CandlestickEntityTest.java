package com.example.CryptocurrencyMarketAnalysisSystem.model;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.repository.CandlestickRepository;
import com.example.CryptocurrencyMarketAnalysisSystem.service.CandlestickDataService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CandlestickEntityTest {
    @Autowired
    private CandlestickRepository repository;
    @MockBean
    private CandlestickDataService candlestickDataService;

    //Positive Tests
    @Test
    public void testCandlestickEntityConstructor() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        CandlestickEntity entity = new CandlestickEntity("BTCUSDT", now, now, 100.0, 110.0, 120.0, 90.0, 1000.0, 2000.0, 100L, 500.0, 1000.0, CandlestickInterval.DAILY);

        assertEquals("BTCUSDT", entity.getSymbol());
        assertEquals(now, entity.getOpenTime());
        assertEquals(now, entity.getCloseTime());
        assertEquals(100.0, entity.getOpen());
        assertEquals(110.0, entity.getClose());
        assertEquals(120.0, entity.getHigh());
        assertEquals(90.0, entity.getLow());
        assertEquals(1000.0, entity.getVolume());
        assertEquals(2000.0, entity.getQuoteAssetVolume());
        assertEquals(100L, entity.getNumberOfTrades());
        assertEquals(500.0, entity.getTakerBuyBaseAssetVolume());
        assertEquals(1000.0, entity.getTakerBuyQuoteAssetVolume());
        assertEquals(CandlestickInterval.DAILY, entity.getCandlestickInterval());
    }

    @Test
    public void testCandlestickEntityConstructorWithDifferentData() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        CandlestickEntity entity = new CandlestickEntity("ETHUSDT", now.minusDays(1), now, 200.0, 210.0, 220.0, 190.0, 2000.0, 4000.0, 200L, 1000.0, 2000.0, CandlestickInterval.HOURLY);

        assertEquals("ETHUSDT", entity.getSymbol());
        assertEquals(now.minusDays(1), entity.getOpenTime());
        assertEquals(now, entity.getCloseTime());
        assertEquals(200.0, entity.getOpen());
        assertEquals(210.0, entity.getClose());
        assertEquals(220.0, entity.getHigh());
        assertEquals(190.0, entity.getLow());
        assertEquals(2000.0, entity.getVolume());
        assertEquals(4000.0, entity.getQuoteAssetVolume());
        assertEquals(200L, entity.getNumberOfTrades());
        assertEquals(1000.0, entity.getTakerBuyBaseAssetVolume());
        assertEquals(2000.0, entity.getTakerBuyQuoteAssetVolume());
        assertEquals(CandlestickInterval.HOURLY, entity.getCandlestickInterval());
    }

    @Test
    public void testFromCandlestick() {
        Candlestick candlestick = getCandlestick();

        CandlestickEntity entity = CandlestickEntity.fromCandlestick(candlestick, "BTCUSDT", CandlestickInterval.DAILY);

        assertEquals("BTCUSDT", entity.getSymbol());
        assertEquals(ZonedDateTime.ofInstant(Instant.ofEpochMilli(1625256000000L), ZoneOffset.UTC), entity.getOpenTime());
        assertEquals(ZonedDateTime.ofInstant(Instant.ofEpochMilli(1625342400000L), ZoneOffset.UTC), entity.getCloseTime());
        assertEquals(100.0, entity.getOpen());
        assertEquals(110.0, entity.getClose());
        assertEquals(120.0, entity.getHigh());
        assertEquals(90.0, entity.getLow());
        assertEquals(1000.0, entity.getVolume());
        assertEquals(2000.0, entity.getQuoteAssetVolume());
        assertEquals(100L, entity.getNumberOfTrades());
        assertEquals(500.0, entity.getTakerBuyBaseAssetVolume());
        assertEquals(1000.0, entity.getTakerBuyQuoteAssetVolume());
        assertEquals(CandlestickInterval.DAILY, entity.getCandlestickInterval());
    }

    @NotNull
    private static Candlestick getCandlestick() {
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1625256000000L); // 2021-07-02T00:00:00Z
        candlestick.setCloseTime(1625342400000L); // 2021-07-03T00:00:00Z
        candlestick.setOpen("100.0");
        candlestick.setClose("110.0");
        candlestick.setHigh("120.0");
        candlestick.setLow("90.0");
        candlestick.setVolume("1000.0");
        candlestick.setQuoteAssetVolume("2000.0");
        candlestick.setNumberOfTrades(100L);
        candlestick.setTakerBuyBaseAssetVolume("500.0");
        candlestick.setTakerBuyQuoteAssetVolume("1000.0");
        return candlestick;
    }

    //Negative Tests
    @Test
    public void testCandlestickEntityConstructorWithNullValues() {
        assertThrows(IllegalArgumentException.class, () ->
                new CandlestickEntity(null, null, null, 100.0, 110.0, 120.0, 90.0, 1000.0, 2000.0, 100L, 500.0, 1000.0, CandlestickInterval.DAILY));
    }

    @Test
    public void testFromCandlestickWithNullValues() {
        assertThrows(IllegalArgumentException.class, () ->
                CandlestickEntity.fromCandlestick(null, "BTCUSDT", CandlestickInterval.DAILY));
        assertThrows(IllegalArgumentException.class, () ->
                CandlestickEntity.fromCandlestick(new Candlestick(), null, CandlestickInterval.DAILY));
        assertThrows(IllegalArgumentException.class, () ->
                CandlestickEntity.fromCandlestick(new Candlestick(), "BTCUSDT", null));
    }

    @Test
    public void testFromCandlestickWithInvalidData() {
        Candlestick invalidCandlestick = new Candlestick();
        invalidCandlestick.setOpenTime(0L);
        invalidCandlestick.setCloseTime(0L);
        invalidCandlestick.setOpen("invalid");
        invalidCandlestick.setClose("invalid");
        invalidCandlestick.setHigh("invalid");
        invalidCandlestick.setLow("invalid");
        invalidCandlestick.setVolume("invalid");
        invalidCandlestick.setQuoteAssetVolume("invalid");
        invalidCandlestick.setTakerBuyBaseAssetVolume("invalid");
        invalidCandlestick.setTakerBuyQuoteAssetVolume("invalid");

        assertThrows(NumberFormatException.class, () ->
                CandlestickEntity.fromCandlestick(invalidCandlestick, "BTCUSDT", CandlestickInterval.DAILY));
    }

    //Boundary Tests

    @Test
    public void testCandlestickEntityConstructorWithMinValues() {
        ZonedDateTime minDateTime = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
        CandlestickEntity candlestickEntity = new CandlestickEntity(
                "MIN_SYMBOL",
                minDateTime,
                minDateTime,
                Double.MIN_VALUE,
                Double.MIN_VALUE,
                Double.MIN_VALUE,
                Double.MIN_VALUE,
                Double.MIN_VALUE,
                Double.MIN_VALUE,
                Long.MIN_VALUE,
                Double.MIN_VALUE,
                Double.MIN_VALUE,
                CandlestickInterval.ONE_MINUTE
        );
        assertNotNull(candlestickEntity);
        assertEquals("MIN_SYMBOL", candlestickEntity.getSymbol());
        assertEquals(minDateTime.withZoneSameInstant(ZoneOffset.UTC), candlestickEntity.getOpenTime());
        assertEquals(minDateTime.withZoneSameInstant(ZoneOffset.UTC), candlestickEntity.getCloseTime());
        assertEquals(Double.MIN_VALUE, candlestickEntity.getOpen());
        assertEquals(Double.MIN_VALUE, candlestickEntity.getClose());
        assertEquals(Double.MIN_VALUE, candlestickEntity.getHigh());
        assertEquals(Double.MIN_VALUE, candlestickEntity.getLow());
        assertEquals(Double.MIN_VALUE, candlestickEntity.getVolume());
        assertEquals(Double.MIN_VALUE, candlestickEntity.getQuoteAssetVolume());
        assertEquals(Long.MIN_VALUE, candlestickEntity.getNumberOfTrades());
        assertEquals(Double.MIN_VALUE, candlestickEntity.getTakerBuyBaseAssetVolume());
        assertEquals(Double.MIN_VALUE, candlestickEntity.getTakerBuyQuoteAssetVolume());
        assertEquals(CandlestickInterval.ONE_MINUTE, candlestickEntity.getCandlestickInterval());
    }

    @Test
    public void testCandlestickEntityConstructorWithMaxValues() {
        ZonedDateTime maxDateTime = ZonedDateTime.of(9999, 12, 31, 23, 59, 59, 999_000_000, ZoneId.of("UTC"));
        CandlestickEntity candlestickEntity = new CandlestickEntity(
                "MAX_SYMBOL",
                maxDateTime,
                maxDateTime,
                Double.MAX_VALUE,
                Double.MAX_VALUE,
                Double.MAX_VALUE,
                Double.MAX_VALUE,
                Double.MAX_VALUE,
                Double.MAX_VALUE,
                Long.MAX_VALUE,
                Double.MAX_VALUE,
                Double.MAX_VALUE,
                CandlestickInterval.ONE_MINUTE
        );
        assertNotNull(candlestickEntity);
        assertEquals("MAX_SYMBOL", candlestickEntity.getSymbol());
        assertEquals(maxDateTime.withZoneSameInstant(ZoneOffset.UTC), candlestickEntity.getOpenTime());
        assertEquals(maxDateTime.withZoneSameInstant(ZoneOffset.UTC), candlestickEntity.getCloseTime());
        assertEquals(Double.MAX_VALUE, candlestickEntity.getOpen());
        assertEquals(Double.MAX_VALUE, candlestickEntity.getClose());
        assertEquals(Double.MAX_VALUE, candlestickEntity.getHigh());
        assertEquals(Double.MAX_VALUE, candlestickEntity.getLow());
        assertEquals(Double.MAX_VALUE, candlestickEntity.getVolume());
        assertEquals(Double.MAX_VALUE, candlestickEntity.getQuoteAssetVolume());
        assertEquals(Long.MAX_VALUE, candlestickEntity.getNumberOfTrades());
        assertEquals(Double.MAX_VALUE, candlestickEntity.getTakerBuyBaseAssetVolume());
        assertEquals(Double.MAX_VALUE, candlestickEntity.getTakerBuyQuoteAssetVolume());
        assertEquals(CandlestickInterval.ONE_MINUTE, candlestickEntity.getCandlestickInterval());
    }

    @Test
    public void testFromCandlestickWithMinMaxValues() {
        Candlestick minCandlestick = new Candlestick();
        minCandlestick.setOpenTime(0L);
        minCandlestick.setCloseTime(0L);
        minCandlestick.setOpen(Double.toString(-Double.MAX_VALUE));
        minCandlestick.setClose(Double.toString(-Double.MAX_VALUE));
        minCandlestick.setHigh(Double.toString(-Double.MAX_VALUE));
        minCandlestick.setLow(Double.toString(-Double.MAX_VALUE));
        minCandlestick.setVolume(Double.toString(-Double.MAX_VALUE));
        minCandlestick.setQuoteAssetVolume(Double.toString(-Double.MAX_VALUE));
        minCandlestick.setNumberOfTrades(0L);
        minCandlestick.setTakerBuyBaseAssetVolume(Double.toString(-Double.MAX_VALUE));
        minCandlestick.setTakerBuyQuoteAssetVolume(Double.toString(-Double.MAX_VALUE));

        CandlestickEntity minCandlestickEntity = CandlestickEntity.fromCandlestick(minCandlestick, "MIN_SYMBOL", CandlestickInterval.ONE_MINUTE);
        assertNotNull(minCandlestickEntity);

        Candlestick maxCandlestick = new Candlestick();
        maxCandlestick.setOpenTime(System.currentTimeMillis());
        maxCandlestick.setCloseTime(System.currentTimeMillis());
        maxCandlestick.setOpen(Double.toString(Double.MAX_VALUE));
        maxCandlestick.setClose(Double.toString(Double.MAX_VALUE));
        maxCandlestick.setHigh(Double.toString(Double.MAX_VALUE));
        maxCandlestick.setLow(Double.toString(Double.MAX_VALUE));
        maxCandlestick.setVolume(Double.toString(Double.MAX_VALUE));
        maxCandlestick.setQuoteAssetVolume(Double.toString(Double.MAX_VALUE));
        maxCandlestick.setNumberOfTrades(Long.MAX_VALUE / 2);
        maxCandlestick.setTakerBuyBaseAssetVolume(Double.toString(Double.MAX_VALUE));
        maxCandlestick.setTakerBuyQuoteAssetVolume(Double.toString(Double.MAX_VALUE));

        CandlestickEntity maxCandlestickEntity = CandlestickEntity.fromCandlestick(maxCandlestick, "MAX_SYMBOL", CandlestickInterval.ONE_MINUTE);
        assertNotNull(maxCandlestickEntity);
    }

    //Exception Tests

    @Test
    public void testCandlestickEntityConstructorThrowsException() {
        ZonedDateTime validDateTime = ZonedDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                null, validDateTime, validDateTime, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1L, 1.0, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", null, validDateTime, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1L, 1.0, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", validDateTime, null, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1L, 1.0, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", validDateTime, validDateTime, null, 1.0, 1.0, 1.0, 1.0, 1.0, 1L, 1.0, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", validDateTime, validDateTime, 1.0, null, 1.0, 1.0, 1.0, 1.0, 1L, 1.0, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", validDateTime, validDateTime, 1.0, 1.0, null, 1.0, 1.0, 1.0, 1L, 1.0, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () ->  new CandlestickEntity(
                "BTCUSDT", validDateTime, validDateTime, 1.0, 1.0, 1.0, null, 1.0, 1.0, 1L, 1.0, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", validDateTime, validDateTime, 1.0, 1.0, 1.0, 1.0, null, 1.0, 1L, 1.0, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", validDateTime, validDateTime, 1.0, 1.0, 1.0, 1.0, 1.0, null, 1L, 1.0, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", validDateTime, validDateTime, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, null, 1.0, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", validDateTime, validDateTime, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1L, null, 1.0, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", validDateTime, validDateTime, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1L, 1.0, null, CandlestickInterval.ONE_MINUTE
        ));

        assertThrows(IllegalArgumentException.class, () -> new CandlestickEntity(
                "BTCUSDT", validDateTime, validDateTime, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1L, 1.0, 1.0, null
        ));
    }

    @Test
    public void testFromCandlestickThrowsException() {
        Candlestick validCandlestick = getValidCandlestick();

        assertThrows(IllegalArgumentException.class, () -> CandlestickEntity.fromCandlestick(null, "BTCUSDT", CandlestickInterval.ONE_MINUTE));

        assertThrows(IllegalArgumentException.class, () -> CandlestickEntity.fromCandlestick(validCandlestick, null, CandlestickInterval.ONE_MINUTE));

        assertThrows(IllegalArgumentException.class, () -> CandlestickEntity.fromCandlestick(validCandlestick, "BTCUSDT", null));
    }

    @NotNull
    private static Candlestick getValidCandlestick() {
        Candlestick validCandlestick = new Candlestick();
        validCandlestick.setOpenTime(0L);
        validCandlestick.setCloseTime(0L);
        validCandlestick.setOpen("1.0");
        validCandlestick.setClose("1.0");
        validCandlestick.setHigh("1.0");
        validCandlestick.setLow("1.0");
        validCandlestick.setVolume("1.0");
        validCandlestick.setQuoteAssetVolume("1.0");
        validCandlestick.setNumberOfTrades(1L);
        validCandlestick.setTakerBuyBaseAssetVolume("1.0");
        validCandlestick.setTakerBuyQuoteAssetVolume("1.0");
        return validCandlestick;
    }

    //Requirements Conformance Tests

    @Test
    public void testUniqueConstraint() {
        ZonedDateTime now = ZonedDateTime.now();
        CandlestickEntity entity1 = new CandlestickEntity("BTCUSDT", now, now, 100.0, 110.0, 120.0, 90.0, 1000.0, 2000.0, 100L, 500.0, 1000.0, CandlestickInterval.DAILY);
        repository.save(entity1);

        assertThrows(DataIntegrityViolationException.class, () -> {
            CandlestickEntity entity2 = new CandlestickEntity("BTCUSDT", now, now, 100.0, 110.0, 120.0, 90.0, 1000.0, 2000.0, 100L, 500.0, 1000.0, CandlestickInterval.DAILY);
            repository.save(entity2);
        });
    }

    @Test
    public void testInitialization() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        CandlestickEntity entity = new CandlestickEntity("BTCUSDT", now, now, 100.0, 110.0, 120.0, 90.0, 1000.0, 2000.0, 100L, 500.0, 1000.0, CandlestickInterval.DAILY);

        assertEquals("BTCUSDT", entity.getSymbol());
        assertEquals(now, entity.getOpenTime());
        assertEquals(now, entity.getCloseTime());
        assertEquals(100.0, entity.getOpen());
        assertEquals(110.0, entity.getClose());
        assertEquals(120.0, entity.getHigh());
        assertEquals(90.0, entity.getLow());
        assertEquals(1000.0, entity.getVolume());
        assertEquals(2000.0, entity.getQuoteAssetVolume());
        assertEquals(100L, entity.getNumberOfTrades());
        assertEquals(500.0, entity.getTakerBuyBaseAssetVolume());
        assertEquals(1000.0, entity.getTakerBuyQuoteAssetVolume());
        assertEquals(CandlestickInterval.DAILY, entity.getCandlestickInterval());
    }

    @Test
    public void testCorrectDataTypes() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        CandlestickEntity entity = new CandlestickEntity("BTCUSDT", now, now, 100.0, 110.0, 120.0, 90.0, 1000.0, 2000.0, 100L, 500.0, 1000.0, CandlestickInterval.DAILY);

        assertNotNull(entity.getSymbol());
        assertNotNull(entity.getOpenTime());
        assertNotNull(entity.getCloseTime());
        assertNotNull(entity.getOpen());
        assertNotNull(entity.getClose());
        assertNotNull(entity.getHigh());
        assertNotNull(entity.getLow());
        assertNotNull(entity.getVolume());
        assertNotNull(entity.getQuoteAssetVolume());
        assertNotNull(entity.getNumberOfTrades());
        assertNotNull(entity.getTakerBuyBaseAssetVolume());
        assertNotNull(entity.getTakerBuyQuoteAssetVolume());
        assertNotNull(entity.getCandlestickInterval());
    }

    @Test
    public void testStoreData() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        CandlestickEntity entity = new CandlestickEntity("BTCUSDT", now, now, 100.0, 110.0, 120.0, 90.0, 1000.0, 2000.0, 100L, 500.0, 1000.0, CandlestickInterval.DAILY);

        assertEquals("BTCUSDT", entity.getSymbol());
        assertEquals(now, entity.getOpenTime());
        assertEquals(now, entity.getCloseTime());
        assertEquals(100.0, entity.getOpen());
        assertEquals(110.0, entity.getClose());
        assertEquals(120.0, entity.getHigh());
        assertEquals(90.0, entity.getLow());
        assertEquals(1000.0, entity.getVolume());
        assertEquals(2000.0, entity.getQuoteAssetVolume());
        assertEquals(100L, entity.getNumberOfTrades());
        assertEquals(500.0, entity.getTakerBuyBaseAssetVolume());
        assertEquals(1000.0, entity.getTakerBuyQuoteAssetVolume());
        assertEquals(CandlestickInterval.DAILY, entity.getCandlestickInterval());
    }

    @Test
    public void testToString() {
        ZonedDateTime now = ZonedDateTime.now();
        CandlestickEntity entity = new CandlestickEntity("BTCUSDT", now, now, 100.0, 110.0, 120.0, 90.0, 1000.0, 2000.0, 100L, 500.0, 1000.0, CandlestickInterval.DAILY);

        String expectedString = "CandlestickEntity{" +
                "id=" + entity.getId() +
                ", symbol='" + entity.getSymbol() + '\'' +
                ", openTime=" + entity.getOpenTime() +
                ", closeTime=" + entity.getCloseTime() +
                ", open=" + entity.getOpen() +
                ", close=" + entity.getClose() +
                ", high=" + entity.getHigh() +
                ", low=" + entity.getLow() +
                ", volume=" + entity.getVolume() +
                ", quoteAssetVolume=" + entity.getQuoteAssetVolume() +
                ", numberOfTrades=" + entity.getNumberOfTrades() +
                ", takerBuyBaseAssetVolume=" + entity.getTakerBuyBaseAssetVolume() +
                ", takerBuyQuoteAssetVolume=" + entity.getTakerBuyQuoteAssetVolume() +
                ", interval='" + entity.getCandlestickInterval() + '\'' +
                '}';

        assertEquals(expectedString, entity.toString());
    }
}