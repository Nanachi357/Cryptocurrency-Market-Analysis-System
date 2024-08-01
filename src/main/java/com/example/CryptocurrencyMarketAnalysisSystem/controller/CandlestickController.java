package com.example.CryptocurrencyMarketAnalysisSystem.controller;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.service.binance.BinanceHistoricalDataService;
import com.example.CryptocurrencyMarketAnalysisSystem.util.CandlestickWrapper;
import com.example.CryptocurrencyMarketAnalysisSystem.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionAttributes({"symbol", "interval", "startDate", "startTime", "endDate", "endTime"})
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
                                          Model model,
                                          SessionStatus status) {
        try {
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

            if (startTimestamp > endTimestamp) {
                throw new IllegalArgumentException("Start time must be before end time");
            }

            // Convert the interval string to CandlestickInterval
            CandlestickInterval candlestickInterval = CandlestickInterval.valueOf(interval);

            // Fetch historical candlestick data from the service
            List<Candlestick> candlestickData = historicalDataService.getHistoricalCandlestickData(symbol, candlestickInterval, startTimestamp, endTimestamp);

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

            // Complete the session if everything is successful
            status.setComplete();

            return "candlestick-chart";

        } catch (IllegalArgumentException e) {
            // Add error message and form data back to the model
            model.addAttribute("error", e.getMessage());
            model.addAttribute("symbol", symbol);
            model.addAttribute("interval", interval);
            model.addAttribute("startDate", startDate);
            model.addAttribute("startTime", startTime);
            model.addAttribute("endDate", endDate);
            model.addAttribute("endTime", endTime);
            return "candlestick-chart-form";
        }
    }
}