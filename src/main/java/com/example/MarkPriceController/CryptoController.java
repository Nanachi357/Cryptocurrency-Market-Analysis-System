package com.example.MarkPriceController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/currentPrice")
public class CryptoController {
    private final BinancePriceService binancePriceService;

    // Constructor of the controller with BinancePriceService parameter
    @Autowired
    public CryptoController(BinancePriceService binancePriceService) {
        this.binancePriceService = binancePriceService;
    }

    // HTTP GET endpoint for retrieving the current price of BTCUSDT
    @GetMapping("/btc-usdt")
    public Mono<String> getCurrentPriceBTCUSDT() {
        String symbol = "BTCUSDT";
        binancePriceService.getCurrentPrice(symbol);
        return Mono.just("Current price of " + symbol + " is retrieved successfully.");
    }

    // HTTP GET endpoint for retrieving the current price of ETHUSDT
    @GetMapping("/eth-usdt")
    public Mono<String> getCurrentPriceETHUSDT() {
        String symbol = "ETHUSDT";
        binancePriceService.getCurrentPrice(symbol);
        return Mono.just("Current price of " + symbol + " is retrieved successfully.");
    }
}