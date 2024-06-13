package com.example.MarkPriceController.controller;

import com.binance.api.client.domain.market.Candlestick;
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

    /**
     * Handles GET requests for candlestick data, converting date and time parameters to timestamps.
     *
     * @param symbol the trading symbol (e.g., BTCUSDT)
     * @param interval the candlestick interval (e.g., 1m, 1h)
     * @param startDate the start date in the format yyyy-MM-dd (optional)
     * @param startTime the start time in the format HH:mm (optional)
     * @param endDate the end date in the format yyyy-MM-dd (optional)
     * @param endTime the end time in the format HH:mm (optional)
     * @param limit the maximum number of candlesticks to retrieve (default is 500)
     * @param model the model to pass attributes to the view
     * @return the name of the view to render
     */
    @GetMapping("/candlestick")
    public String getCandlestickData(@RequestParam String symbol,
                                     @RequestParam String interval,
                                     @RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String startTime,
                                     @RequestParam(required = false) String endDate,
                                     @RequestParam(required = false) String endTime,
                                     @RequestParam(required = false, defaultValue = "500") Integer limit,
                                     Model model) {

        // Convert date and time parameters to timestamps
        Long startTimestamp = convertToTimestamp(startDate, startTime);
        Long endTimestamp = convertToTimestamp(endDate, endTime);

        // Retrieve historical candlestick data
        List<Candlestick> candlestickData = historicalDataService.getHistoricalCandlestickData(symbol, interval, startTimestamp, endTimestamp);

        // Add attributes to the model
        model.addAttribute("symbol", symbol);
        model.addAttribute("interval", interval);
        model.addAttribute("startTime", startTimestamp);
        model.addAttribute("endTime", endTimestamp);
        model.addAttribute("limit", limit);
        model.addAttribute("candlestickData", candlestickData);

        // Return the view name
        return "candlestick";
    }

    // Converts date and time strings to a timestamp in milliseconds
    private Long convertToTimestamp(String date, String time) {
        if (date == null || date.isEmpty()) {
            return null;
        }

        // Parse the date and time
        LocalDate localDate = LocalDate.parse(date);
        LocalTime localTime = (time == null || time.isEmpty()) ? LocalTime.MIDNIGHT : LocalTime.parse(time);

        // Combine date and time into a LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        // Convert LocalDateTime to timestamp in milliseconds
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}