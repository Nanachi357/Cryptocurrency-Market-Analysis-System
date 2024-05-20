package com.example.MarkPriceController.service;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.OrderBook;
import org.springframework.stereotype.Service;

@Service
public class OrderBookService {

    private final BinanceApiRestClient binanceApiClient;

    public OrderBookService() {
        // Initialize the BinanceApiClientFactory with your API key and private key if needed
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("YOUR_API_KEY", "YOUR_SECRET_KEY");
        this.binanceApiClient = factory.newRestClient();
    }

    public OrderBook getOrderBook(String symbol, int limit) {
        // Use BinanceApiRestClient to get OrderBook
        return binanceApiClient.getOrderBook(symbol.toUpperCase(), limit);
    }
}