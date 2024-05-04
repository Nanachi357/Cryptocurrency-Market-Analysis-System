package com.example.MarkPriceController;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import org.springframework.stereotype.Service;

@Service
public class BinancePriceService {
    private final BinanceApiRestClient binanceApiClient;

    public BinancePriceService() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        binanceApiClient = factory.newRestClient();
    }

    public void getCurrentPrice(String symbol) {
        TickerPrice tickerPrice = binanceApiClient.getPrice(symbol);
        System.out.println("Current price of " + symbol + ": " + tickerPrice.getPrice());
    }
}