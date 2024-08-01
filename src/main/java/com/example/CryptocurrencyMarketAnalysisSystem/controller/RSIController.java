package com.example.CryptocurrencyMarketAnalysisSystem.controller;

import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.model.RSIData;
import com.example.CryptocurrencyMarketAnalysisSystem.service.RSIService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalDate;
import java.time.ZoneId;

@Controller
@SessionAttributes({"symbol", "interval", "startDate", "endDate", "period"})
public class RSIController {

    private final RSIService rsiService;

    public RSIController(RSIService rsiService) {
        this.rsiService = rsiService;
    }

    // Endpoint to display the historical RSI form
    @GetMapping("/historical-rsi-form")
    public String getHistoricalRSIForm(Model model) {
        // Set default values for the form
        if (!model.containsAttribute("symbol")) {
            model.addAttribute("symbol", "");
        }
        if (!model.containsAttribute("interval")) {
            model.addAttribute("interval", "DAILY");
        }
        if (!model.containsAttribute("startDate")) {
            model.addAttribute("startDate", LocalDate.now().minusDays(30));
        }
        if (!model.containsAttribute("endDate")) {
            model.addAttribute("endDate", LocalDate.now());
        }
        if (!model.containsAttribute("period")) {
            model.addAttribute("period", 14);
        }
        return "historical-rsi-form";
    }

    // Endpoint to handle the form submission and fetch the RSI data
    @GetMapping("/historical-rsi")
    public String getRSIData(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) CandlestickInterval interval,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer period,
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
            // Convert start and end dates to timestamps in milliseconds
            long startTime = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
            long endTime = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;

            if (startTime >= endTime) {
                model.addAttribute("error", "Start time must be before end time");
                model.addAttribute("symbol", symbol);
                model.addAttribute("interval", interval);
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);
                model.addAttribute("period", period);
                return "historical-rsi-form";
            }

            // Fetch RSI data using the RSIService
            RSIData rsiData = rsiService.getRSIData(symbol, interval, startTime, endTime, period);


            // Add RSI data to the model
            model.addAttribute("rsiValues", rsiData.rsiValues());
            model.addAttribute("dates", rsiData.dates());

            // Clear session attributes
            status.setComplete();

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("error", "Error calculating RSI data: " + e.getMessage());
        }
        return "historical-rsi";
    }
}
