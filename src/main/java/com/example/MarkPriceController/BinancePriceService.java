package com.example.MarkPriceController;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import org.springframework.stereotype.Service;

// Service for retrieving cryptocurrency prices from the Binance API
@Service
public class BinancePriceService {
    private final BinanceApiRestClient binanceApiClient;

    // Constructor that initializes the BinanceApiClient
    public BinancePriceService() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        this.binanceApiClient = factory.newRestClient();
    }

    // Method to get the current price of a specified cryptocurrency (e.g., "BTCUSDT")
    public TickerPrice getCurrentPrice(String symbol) {
        return binanceApiClient.getPrice(symbol);
    }

}