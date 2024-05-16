package com.example.MarkPriceController;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebController {

    private final BinanceHistoricalDataService historicalDataService;
    private final BinancePriceService priceService;

    // Constructor injection of services
    @Autowired
    public WebController(BinanceHistoricalDataService historicalDataService, BinancePriceService priceService) {
        this.historicalDataService = historicalDataService;
        this.priceService = priceService;
    }

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

    // Handles requests for candlestick data, converting date and time parameters to timestamps
    @GetMapping("/candlestick")
    public String getCandlestickData(@RequestParam String symbol,
                                     @RequestParam String interval,
                                     @RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String startTime,
                                     @RequestParam(required = false) String endDate,
                                     @RequestParam(required = false) String endTime,
                                     @RequestParam(required = false, defaultValue = "500") Integer limit,
                                     Model model) {
        CandlestickInterval candlestickInterval = CandlestickInterval.valueOf(interval);
        Long startTimestamp = convertToTimestamp(startDate, startTime);
        Long endTimestamp = convertToTimestamp(endDate, endTime);
        List<Candlestick> candlestickData = historicalDataService.getHistoricalCandlestickData(symbol, candlestickInterval, startTimestamp, endTimestamp, limit);

        model.addAttribute("symbol", symbol);
        model.addAttribute("interval", interval);
        model.addAttribute("startTime", startTimestamp);
        model.addAttribute("endTime", endTimestamp);
        model.addAttribute("limit", limit);
        model.addAttribute("candlestickData", candlestickData);
        return "candlestick";
    }

    // Handles requests for current price data
    @GetMapping("/price")
    public String getCurrentPrice(@RequestParam String symbol, Model model) {
        TickerPrice price = priceService.getCurrentPrice(symbol);
        model.addAttribute("symbol", symbol);
        model.addAttribute("price", price.getPrice());
        return "price";
    }

    // Converts date and time strings to a timestamp in milliseconds
    private Long convertToTimestamp(String date, String time) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        LocalDate localDate = LocalDate.parse(date);
        LocalTime localTime = (time == null || time.isEmpty()) ? LocalTime.MIDNIGHT : LocalTime.parse(time);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}