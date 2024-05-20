package com.example.MarkPriceController.controller;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.MarkPriceController.service.BinanceHistoricalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Controller
public class CandlestickController {

    private final BinanceHistoricalDataService historicalDataService;

    @Autowired
    public CandlestickController(BinanceHistoricalDataService historicalDataService) {
        this.historicalDataService = historicalDataService;
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