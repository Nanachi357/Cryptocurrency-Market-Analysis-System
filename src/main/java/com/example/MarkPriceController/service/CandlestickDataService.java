package com.example.MarkPriceController.service;

import com.binance.api.client.domain.market.Candlestick;
import com.example.MarkPriceController.model.CandlestickEntity;
import com.example.MarkPriceController.repository.CandlestickRepository;
import com.example.MarkPriceController.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public List<Candlestick> getCandlestickData(String symbol, LocalDateTime startTime, LocalDateTime endTime, String interval) {
        List<CandlestickEntity> entities = candlestickRepository.findBySymbolAndOpenTimeBetweenAndInterval(symbol, startTime, endTime, interval);
        return entities.stream().map(this::convertToCandlestick).collect(Collectors.toList());
    }

    //Checks if a candlestick exists for a given symbol, open time, close time, and interval
    public boolean existsBySymbolAndOpenTime(String symbol, LocalDateTime openTime, LocalDateTime closeTime, String interval) {
        boolean exists = candlestickRepository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(symbol, openTime, closeTime, interval);
        logger.info("Checking existence of candlestick: symbol={}, openTime={}, closeTime={}, interval={}. Exists: {}", symbol, openTime, closeTime, interval, exists);

        return exists;
    }

    //Saves candlestick data to the repository
    public void saveCandlestickData(String symbol, Candlestick candlestick, String interval) {
        CandlestickEntity entity = new CandlestickEntity();
        entity.setSymbol(symbol);
        entity.setOpenTime(DateUtils.convertMillisToLocalDateTime(candlestick.getOpenTime()));
        entity.setCloseTime(DateUtils.convertMillisToLocalDateTime(candlestick.getCloseTime()));
        entity.setOpen(Double.parseDouble(candlestick.getOpen()));
        entity.setClose(Double.parseDouble(candlestick.getClose()));
        entity.setHigh(Double.parseDouble(candlestick.getHigh()));
        entity.setLow(Double.parseDouble(candlestick.getLow()));
        entity.setVolume(Double.parseDouble(candlestick.getVolume()));
        entity.setQuoteAssetVolume(Double.parseDouble(candlestick.getQuoteAssetVolume()));
        entity.setNumberOfTrades(Long.parseLong(String.valueOf(candlestick.getNumberOfTrades())));
        entity.setTakerBuyBaseAssetVolume(Double.parseDouble(candlestick.getTakerBuyBaseAssetVolume()));
        entity.setTakerBuyQuoteAssetVolume(Double.parseDouble(candlestick.getTakerBuyQuoteAssetVolume()));
        entity.setInterval(interval);



        try {
            candlestickRepository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Converts a CandlestickEntity to a Candlestick object
    private Candlestick convertToCandlestick(Object data) {
        if (data instanceof CandlestickEntity entity) {
            Candlestick candlestick = new Candlestick();
            candlestick.setOpenTime(DateUtils.convertDateToMillis(entity.getOpenTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))));
            candlestick.setCloseTime(DateUtils.convertDateToMillis(entity.getCloseTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))));
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