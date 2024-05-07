package com.example.MarkPriceController;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Service for retrieving cryptocurrency prices from the Binance API
@Service
public class BinancePriceService {
    private final BinanceApiRestClient binanceApiClient;

    // Variable to store the update interval for prices (in seconds)
    @Value("${price.update.interval.seconds}")
    private int updateIntervalSeconds;

    // Constructor that initializes the BinanceApiClient
    public BinancePriceService() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        binanceApiClient = factory.newRestClient();
    }

    // Method to get the current price of a specified cryptocurrency (e.g., "BTCUSDT")
    public String getCurrentPrice(String symbol) {
        TickerPrice tickerPrice = binanceApiClient.getPrice(symbol);
        return "Current price of " + symbol + ": " + tickerPrice.getPrice();
    }

    // Method for periodically updating cryptocurrency prices
    public void updatePricesPeriodically() {
        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.scheduleAtFixedRate(() -> {
                System.out.println(getCurrentPrice("BTCUSDT"));
                System.out.println(getCurrentPrice("ETHUSDT"));
            }, 0, updateIntervalSeconds, TimeUnit.SECONDS);
        }
    }

    // Method to get prices of multiple cryptocurrencies simultaneously via the console
    public void getCurrentPrices(String... symbols) {
        for (String symbol : symbols) {
            System.out.println(getCurrentPrice(symbol));
        }
    }
}