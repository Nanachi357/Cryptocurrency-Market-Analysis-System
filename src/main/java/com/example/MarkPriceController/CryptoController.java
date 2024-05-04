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

    @Autowired
    public CryptoController(BinancePriceService binancePriceService) {
        this.binancePriceService = binancePriceService;
    }

    @GetMapping("/btc-usdt")
    public Mono<String> getCurrentPriceBTCUSDT() {
        String symbol = "BTCUSDT";
        binancePriceService.getCurrentPrice(symbol);
        return Mono.just("Current price of " + symbol + " is retrieved successfully.");
    }
}