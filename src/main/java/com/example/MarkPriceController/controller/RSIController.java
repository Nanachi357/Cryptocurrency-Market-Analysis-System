package com.example.MarkPriceController.controller;

import com.example.MarkPriceController.model.RSIData;
import com.example.MarkPriceController.service.RSIService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

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
    public String getRSIData(@RequestParam String symbol,
                             @RequestParam String interval,
                             @RequestParam int period,
                             @RequestParam(required = false) LocalDate startDate,
                             @RequestParam(required = false) LocalDate endDate,
                             Model model) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Fetch RSI data using the service
        RSIData rsiData = rsiService.getRSIData(symbol, interval, period, startDate, endDate);
        // Add RSI data to the model to be used in the view
        model.addAttribute("dates", rsiData.getDates());
        model.addAttribute("rsiValues", rsiData.getRsiValues());

        return "historical-rsi";
    }
}