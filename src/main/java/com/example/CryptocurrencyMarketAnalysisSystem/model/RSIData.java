package com.example.CryptocurrencyMarketAnalysisSystem.model;

import java.util.List;

public record RSIData(List<String> dates, List<Double> rsiValues) {
    public RSIData(List<String> dates, List<Double> rsiValues) {
        if (dates == null || rsiValues == null) {
            throw new NullPointerException("Dates and RSI values cannot be null");
        }
        for (String date : dates) {
            if (date == null) {
                throw new NullPointerException("Date cannot be null");
            }
        }
        for (Double rsiValue : rsiValues) {
            if (rsiValue == null) {
                throw new NullPointerException("RSI value cannot be null");
            }
            if (rsiValue < 0 || rsiValue > 100) {
                throw new IllegalArgumentException("RSI value must be between 0 and 100");
            }
        }
        this.dates = List.copyOf(dates);
        this.rsiValues = List.copyOf(rsiValues);
    }
}