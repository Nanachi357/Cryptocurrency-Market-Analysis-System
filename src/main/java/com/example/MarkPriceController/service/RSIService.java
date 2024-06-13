package com.example.MarkPriceController.service;

import com.binance.api.client.domain.market.Candlestick;
import com.example.MarkPriceController.model.RSIData;
import com.example.MarkPriceController.util.CandlestickWrapper;
import com.example.MarkPriceController.util.DateUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RSIService {

    private final BinanceHistoricalDataService binanceHistoricalDataService;

    public RSIService(BinanceHistoricalDataService binanceHistoricalDataService) {
        this.binanceHistoricalDataService = binanceHistoricalDataService;
    }

    //Retrieves and calculates RSI data for a given symbol, interval, date range, and period
    public RSIData getRSIData(String symbol, String interval, long startDate, long endDate, int period) {
        // Fetch historical candlestick data
        List<Candlestick> candlesticks = binanceHistoricalDataService.getHistoricalCandlestickData(symbol, interval.toUpperCase(), startDate, endDate);


        if (candlesticks.size() < period) {
            throw new IllegalArgumentException("Not enough data to calculate RSI");
        }


        // Sort candlesticks by open time
        candlesticks.sort(Comparator.comparing(Candlestick::getOpenTime));

        Set<CandlestickWrapper> uniqueCloseTimes = new HashSet<>();
        List<Double> closePrices = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        // Extract unique close prices and corresponding dates
        for (Candlestick candlestick : candlesticks) {
            CandlestickWrapper wrapper = new CandlestickWrapper(candlestick);
            if (uniqueCloseTimes.add(wrapper)) {
                closePrices.add(Double.parseDouble(candlestick.getClose()));

                dates.add(DateUtils.convertMillisToDate(candlestick.getCloseTime()));
            }
        }


        // Calculate RSI values
        List<Double> rsiValues = calculateRSI(closePrices, period);

        // Prepare RSIData object
        RSIData rsiData = new RSIData();
        rsiData.setDates(dates);
        rsiData.setRsiValues(rsiValues);

        return rsiData;
    }


    // Calculate RSI values based on the given period
    private List<Double> calculateRSI(List<Double> closePrices, int period) {
        List<Double> rsiValues = new ArrayList<>();

        // Check if there is enough data to calculate RSI
        if (closePrices.size() < period) {
            throw new IllegalArgumentException("Not enough data to calculate RSI");
        }

        List<Double> gains = new ArrayList<>();
        List<Double> losses = new ArrayList<>();

        // Calculate gains and losses
        for (int i = 1; i < closePrices.size(); i++) {
            double change = closePrices.get(i) - closePrices.get(i - 1);
            if (change > 0) {
                gains.add(change);
                losses.add(0.0);
            } else {
                gains.add(0.0);
                losses.add(-change);
            }
        }

        double avgGain = 0;
        double avgLoss = 0;

        // Calculate average gains and losses for the initial period
        for (int i = 0; i < period; i++) {
            avgGain += gains.get(i);
            avgLoss += losses.get(i);
        }

        avgGain /= period;
        avgLoss /= period;

        double rs = avgGain / avgLoss;
        double rsi = 100 - (100 / (1 + rs));
        rsiValues.add(rsi);

        // Calculate RSI for the rest of the periods
        for (int i = 0; i < gains.size(); i++) {
            avgGain = ((avgGain * (period - 1)) + gains.get(i)) / period;
            avgLoss = ((avgLoss * (period - 1)) + losses.get(i)) / period;

            rs = avgGain / avgLoss;
            rsi = 100 - (100 / (1 + rs));
            rsiValues.add(rsi);
        }

        return rsiValues;
    }
}