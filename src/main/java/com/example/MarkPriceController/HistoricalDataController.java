package com.example.MarkPriceController;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/historicalData")
public class HistoricalDataController {

    private final BinanceHistoricalDataService historicalDataService;

    public HistoricalDataController() {
        this.historicalDataService = new BinanceHistoricalDataService();
    }

    @GetMapping("/candlestick")
    public List<Candlestick> getHistoricalCandlestickData(@RequestParam String symbol,
                                                          @RequestParam CandlestickInterval interval,
                                                          @RequestParam(required = false) Long startTime,
                                                          @RequestParam(required = false) Long endTime,
                                                          @RequestParam(required = false, defaultValue = "500") Integer limit) {
        return historicalDataService.getHistoricalCandlestickData(symbol, interval, startTime, endTime, limit);
    }

}