package com.example.MarkPriceController;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BinanceHistoricalDataService {

    private final BinanceApiRestClient binanceApiClient;

    public BinanceHistoricalDataService() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        binanceApiClient = factory.newRestClient();
    }

    public List<Candlestick> getHistoricalCandlestickData(String symbol, CandlestickInterval interval, Long startTime, Long endTime, Integer limit) {
        return binanceApiClient.getCandlestickBars(symbol, interval, limit, startTime, endTime);
    }
}