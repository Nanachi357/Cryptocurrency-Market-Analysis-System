package com.example.MarkPriceController.service;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.MarkPriceController.model.RSIData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.MarkPriceController.util.DateUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RSIService {

    private static final Logger logger = LoggerFactory.getLogger(RSIService.class);
    private final BinanceHistoricalDataService binanceHistoricalDataService;

    public RSIService(BinanceHistoricalDataService binanceHistoricalDataService) {
        this.binanceHistoricalDataService = binanceHistoricalDataService;
    }

    // Retrieve RSI data for a given symbol and interval between the specified dates
    public RSIData getRSIData(String symbol, String interval, int period, LocalDate startDate, LocalDate endDate) {
        try {
            // Convert LocalDate to milliseconds
            long startTime = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
            long endTime = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;

            // Fetch historical candlestick data from Binance
            List<Candlestick> candlesticks = binanceHistoricalDataService.getHistoricalCandlestickData(
                    symbol, CandlestickInterval.valueOf(interval), startTime, endTime, 1500);

            // Extract open times (dates) from the candlestick data and convert them to String format
            List<Long> datesInMillis = candlesticks.stream().map(Candlestick::getOpenTime).toList();
            List<String> dates = datesInMillis.stream().map(DateUtils::convertMillisToDate).collect(Collectors.toList());

            // Extract close prices from the candlestick data
            List<Double> closePrices = candlesticks.stream().map(c -> Double.parseDouble(c.getClose())).collect(Collectors.toList());

            // Calculate RSI values
            List<Double> rsiValues = calculateRSI(closePrices, period);

            // Log the dates and RSI values for debugging purposes
            logger.debug("Dates: {}", dates);
            logger.debug("RSI Values: {}", rsiValues);

            // Create and populate an RSIData object with the dates and RSI values
            RSIData rsiData = new RSIData();
            rsiData.setDates(dates);
            rsiData.setRsiValues(rsiValues);

            return rsiData;
        } catch (Exception e) {
            logger.error("Error calculating RSI data", e);
            throw e;
        }
    }

    // Calculate RSI values based on the given period
    private List<Double> calculateRSI(List<Double> closePrices, int period) {
        List<Double> rsiValues = new ArrayList<>();
        if (closePrices.size() < period) {
            throw new IllegalArgumentException("Not enough data to calculate RSI");
        }
        double gain = 0;
        double loss = 0;

        // Initialize the gain and loss sums for the first period
        for (int i = 1; i < period; i++) {
            double change = closePrices.get(i) - closePrices.get(i - 1);
            if (change > 0) {
                gain += change;
            } else {
                loss -= change;
            }
        }

        // Calculate the first RSI value
        gain /= period;
        loss /= period;
        double rs = gain / loss;
        rsiValues.add(100 - (100 / (1 + rs)));

        // Calculate subsequent RSI values
        for (int i = period; i < closePrices.size(); i++) {
            double change = closePrices.get(i) - closePrices.get(i - 1);
            if (change > 0) {
                gain = (gain * (period - 1) + change) / period;
                loss = (loss * (period - 1)) / period;
            } else {
                gain = (gain * (period - 1)) / period;
                loss = (loss * (period - 1) - change) / period;
            }
            rs = gain / loss;
            rsiValues.add(100 - (100 / (1 + rs)));
        }
        return rsiValues;
    }
}