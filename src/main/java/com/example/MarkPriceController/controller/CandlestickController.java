package com.example.MarkPriceController.controller;

import com.binance.api.client.domain.market.Candlestick;
import com.example.MarkPriceController.service.BinanceHistoricalDataService;
import com.example.MarkPriceController.util.CandlestickWrapper;
import com.example.MarkPriceController.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CandlestickController {

    private final BinanceHistoricalDataService historicalDataService;

    @Autowired
    public CandlestickController(BinanceHistoricalDataService historicalDataService) {
        this.historicalDataService = historicalDataService;
    }

    // Endpoint to display the candlestick chart form
    @GetMapping("/candlestick-chart-form")
    public String getCandlestickChartForm() {
        return "candlestick-chart-form";
    }

    // Endpoint to handle the form submission and fetch the candlestick data
    @GetMapping("/candlestick-chart")
    public String getCandlestickChartPage(@RequestParam String symbol,
                                          @RequestParam String interval,
                                          @RequestParam(required = false) LocalDate startDate,
                                          @RequestParam(required = false) String startTime,
                                          @RequestParam(required = false) LocalDate endDate,
                                          @RequestParam(required = false) String endTime,
                                          Model model) {
        // Default to 30 days ago if startDate is not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }

        // Default to today if endDate is not provided
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        symbol = symbol.toUpperCase();

        // Convert startDate and endDate with startTime and endTime to timestamps
        Long startTimestamp = DateUtils.convertToTimestamp(startDate.toString(), startTime);
        Long endTimestamp = DateUtils.convertToTimestamp(endDate.toString(), endTime);

        // Fetch historical candlestick data from the service
        List<Candlestick> candlestickData = historicalDataService.getHistoricalCandlestickData(symbol, interval, startTimestamp, endTimestamp);

        // Convert candlestick data to wrappers for easier handling in the view
        List<CandlestickWrapper> candlestickWrappers = (candlestickData != null) ?
                candlestickData.stream().map(CandlestickWrapper::new).collect(Collectors.toList()) :
                List.of();

        // Add attributes to the model for the view
        model.addAttribute("symbol", symbol);
        model.addAttribute("interval", interval);
        model.addAttribute("startTimestamp", startTimestamp);
        model.addAttribute("endTimestamp", endTimestamp);
        model.addAttribute("candlestickData", candlestickWrappers);

        return "candlestick-chart";
    }
}