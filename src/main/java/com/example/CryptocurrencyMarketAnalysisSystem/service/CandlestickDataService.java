package com.example.CryptocurrencyMarketAnalysisSystem.service;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.model.CandlestickEntity;
import com.example.CryptocurrencyMarketAnalysisSystem.repository.CandlestickRepository;
import com.example.CryptocurrencyMarketAnalysisSystem.service.binance.BinanceHistoricalDataService;
import com.example.CryptocurrencyMarketAnalysisSystem.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CandlestickDataService {
    private static final Logger logger = LoggerFactory.getLogger(BinanceHistoricalDataService.class);


    private final CandlestickRepository candlestickRepository;

    @Autowired
    public CandlestickDataService(CandlestickRepository candlestickRepository) {
        this.candlestickRepository = candlestickRepository;
    }

    //Retrieves candlestick data for a given symbol, time range, and interval
    public List<Candlestick> getCandlestickData(String symbol, ZonedDateTime startTime, ZonedDateTime endTime, CandlestickInterval interval) {
        if (symbol == null || startTime == null || endTime == null || interval == null) {
            throw new IllegalArgumentException("Parameters symbol, startTime, endTime, and interval must not be null");
        }

        logger.info("Retrieving candlestick data for symbol: {}, startTime: {}, endTime: {}, interval: {}", symbol, startTime, endTime, interval);

        List<CandlestickEntity> entities = candlestickRepository.findBySymbolAndOpenTimeBetweenAndInterval(symbol, startTime, endTime, interval);
        return entities.stream().map(this::convertToCandlestick).collect(Collectors.toList());
    }

    //Checks if a candlestick exists for a given symbol, open time, close time, and interval
    public boolean existsBySymbolAndOpenTime(String symbol, ZonedDateTime openTime, ZonedDateTime closeTime, CandlestickInterval interval) {
        if (symbol == null || openTime == null || closeTime == null || interval == null) {
            throw new IllegalArgumentException("Parameters symbol, openTime, closeTime, and interval must not be null");
        }

        logger.info("Checking existence of candlestick: symbol={}, openTime={}, closeTime={}, interval={}", symbol, openTime, closeTime, interval);
        boolean exists = candlestickRepository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(symbol, openTime, closeTime, interval);
        logger.info("Exists: {}", exists);
        return exists;
    }

    //Saves candlestick data to the repository
    public void saveCandlestickData(String symbol, Candlestick candlestick, CandlestickInterval interval) {
        logger.info("In saveCandlestickData - Candlestick {},CandlestickInterval {}, symbol{}", candlestick, interval, symbol);
        if (symbol == null || candlestick == null || interval == null) {
            throw new IllegalArgumentException("Parameters symbol, candlestick, and interval must not be null");
        }
        if (candlestick.getOpenTime() == null || candlestick.getCloseTime() == null ||
                candlestick.getOpen() == null || candlestick.getClose() == null ||
                candlestick.getHigh() == null || candlestick.getLow() == null ||
                candlestick.getVolume() == null || candlestick.getQuoteAssetVolume() == null ||
                candlestick.getNumberOfTrades() == null ||
                candlestick.getTakerBuyBaseAssetVolume() == null ||
                candlestick.getTakerBuyQuoteAssetVolume() == null) {
            throw new IllegalArgumentException("All candlestick parameters must not be null");
        }

        if (DateUtils.isMillisInRange(candlestick.getOpenTime()) || DateUtils.isMillisInRange(candlestick.getCloseTime())) {
            throw new IllegalArgumentException("Millis value is out of range");
        }

        ZonedDateTime openTime;
        ZonedDateTime closeTime;
        try {
            openTime = DateUtils.convertMillisToUtcZonedDateTime(candlestick.getOpenTime());
            closeTime = DateUtils.convertMillisToUtcZonedDateTime(candlestick.getCloseTime());
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid date time values", e);
        }
        if (openTime.isAfter(closeTime)) {
            throw new IllegalArgumentException("Open time must be before close time");
        }

        double open = Double.parseDouble(candlestick.getOpen());
        double close = Double.parseDouble(candlestick.getClose());
        double high = Double.parseDouble(candlestick.getHigh());
        double low = Double.parseDouble(candlestick.getLow());
        double volume = Double.parseDouble(candlestick.getVolume());
        double quoteAssetVolume = Double.parseDouble(candlestick.getQuoteAssetVolume());
        double takerBuyBaseAssetVolume = Double.parseDouble(candlestick.getTakerBuyBaseAssetVolume());
        double takerBuyQuoteAssetVolume = Double.parseDouble(candlestick.getTakerBuyQuoteAssetVolume());
        long numberOfTrades = candlestick.getNumberOfTrades();

        if (high < low) {
            throw new IllegalArgumentException("High value cannot be less than low value");
        }
        if (open < 0 || close < 0 || high < 0 || low < 0 || volume < 0 || quoteAssetVolume < 0 || takerBuyBaseAssetVolume < 0 || takerBuyQuoteAssetVolume < 0 || numberOfTrades < 0) {
            throw new IllegalArgumentException("Numeric values must be non-negative");
        }

        if (!candlestickRepository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(symbol, openTime, closeTime, interval)) {
            CandlestickEntity entity = CandlestickEntity.fromCandlestick(candlestick, symbol, interval);

            try {
                candlestickRepository.save(entity);
                logger.info("Saved new candlestick to database: {}", entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("Candlestick already exists in database: symbol={}, openTime={}, closeTime={}, interval={}",
                    symbol, openTime, closeTime, interval);
        }
    }

    //Converts a CandlestickEntity to a Candlestick object
    private Candlestick convertToCandlestick(Object data) {
        if (data instanceof CandlestickEntity entity) {
            Candlestick candlestick = new Candlestick();
            candlestick.setOpenTime(DateUtils.convertDateToMillis(entity.getOpenTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")), ZoneId.of("UTC")));
            candlestick.setCloseTime(DateUtils.convertDateToMillis(entity.getCloseTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")), ZoneId.of("UTC")));
            candlestick.setOpen(entity.getOpen().toString());
            candlestick.setClose(entity.getClose().toString());
            candlestick.setHigh(entity.getHigh().toString());
            candlestick.setLow(entity.getLow().toString());
            candlestick.setVolume(entity.getVolume().toString());
            candlestick.setQuoteAssetVolume(entity.getQuoteAssetVolume().toString());
            candlestick.setNumberOfTrades(Long.valueOf(entity.getNumberOfTrades().toString()));
            candlestick.setTakerBuyBaseAssetVolume(entity.getTakerBuyBaseAssetVolume().toString());
            candlestick.setTakerBuyQuoteAssetVolume(entity.getTakerBuyQuoteAssetVolume().toString());

            return candlestick;
        }
        throw new IllegalArgumentException("Unsupported data type: " + data.getClass().getName());
    }

}