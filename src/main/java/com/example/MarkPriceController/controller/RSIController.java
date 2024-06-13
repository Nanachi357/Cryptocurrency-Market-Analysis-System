package com.example.MarkPriceController.controller;

import com.example.MarkPriceController.model.RSIData;
import com.example.MarkPriceController.service.RSIService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.ZoneId;

@Controller
public class RSIController {

    private final RSIService rsiService;

    public RSIController(RSIService rsiService) {
        this.rsiService = rsiService;
    }

    // Endpoint to display the historical RSI form
    @GetMapping("/historical-rsi-form")
    public String getHistoricalRSIForm() {
        return "historical-rsi-form";
    }

    // Endpoint to handle the form submission and fetch the RSI data
    @GetMapping("/historical-rsi")
    public String getRSIData(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) String interval,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer period,
            Model model) {
        try {
            // Default to 30 days ago if startDate is not provided
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(30);
            }

            // Default to today if endDate is not provided
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            // Default period to 14 if not provided
            if (period == null) {
                period = 14;
            }

            symbol = symbol.toUpperCase();
            // Convert start and end dates to timestamps in milliseconds
            long startTime = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
            long endTime = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;

            // Fetch RSI data using the RSIService
            RSIData rsiData = rsiService.getRSIData(symbol, interval, startTime, endTime, period);


            // Add RSI data to the model
            model.addAttribute("rsiValues", rsiData.getRsiValues());
            model.addAttribute("dates", rsiData.getDates());
        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("error", "Error calculating RSI data: " + e.getMessage());
        }
        return "historical-rsi";
    }
}
