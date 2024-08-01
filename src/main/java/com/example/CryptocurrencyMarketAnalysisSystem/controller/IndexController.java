package com.example.CryptocurrencyMarketAnalysisSystem.controller;

import com.binance.api.client.domain.market.CandlestickInterval;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    // Handles the root URL, populates the model with candlestick intervals
    @GetMapping("/")
    public String index(Model model) {
        List<String> intervals = Arrays.stream(CandlestickInterval.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        model.addAttribute("intervals", intervals);
        model.addAttribute("defaultLimit", 500);
        model.addAttribute("maxLimit", 1500);
        return "index";
    }
}