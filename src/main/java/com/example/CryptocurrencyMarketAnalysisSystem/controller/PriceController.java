package com.example.CryptocurrencyMarketAnalysisSystem.controller;

import com.binance.api.client.domain.market.TickerPrice;
import com.example.CryptocurrencyMarketAnalysisSystem.service.BinancePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PriceController {

    private final BinancePriceService priceService;

    @Autowired
    public PriceController(BinancePriceService priceService) {
        this.priceService = priceService;
    }

    // Handles requests for current price data
    @GetMapping("/price")
    public String getCurrentPrice(@RequestParam String symbol, Model model) {
        TickerPrice price = priceService.getCurrentPrice(symbol);
        model.addAttribute("symbol", symbol);
        model.addAttribute("price", price.getPrice());
        return "price";
    }
}