package com.example.MarkPriceController.service;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.MarkPriceController.util.CandlestickWrapper;
import com.example.MarkPriceController.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class BinanceHistoricalDataService {

    private static final int MAX_CANDLESTICKS_PER_REQUEST = 1000;
    private static final int MAX_REQUESTS_PER_MINUTE = 6000;
    private int requestCount = 0;
    private final BinanceApiRestClient binanceApiClient;
    private final CandlestickDataService candlestickDataService;

    @Autowired
    public BinanceHistoricalDataService(CandlestickDataService candlestickDataService) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        this.binanceApiClient = factory.newRestClient();
        this.candlestickDataService = candlestickDataService;
    }

    //Fetches historical candlestick data for a given symbol and interval, handling missing data by fetching from the API if necessary.
    public List<Candlestick> getHistoricalCandlestickData(String symbol, String interval, Long startTime, Long endTime) {

        Set<CandlestickWrapper> allCandlesticksSet = new HashSet<>();
        LocalDateTime startDateTime = DateUtils.convertMillisToLocalDateTime(startTime);
        LocalDateTime endDateTime = DateUtils.convertMillisToLocalDateTime(endTime);

        // Retrieve existing candlesticks from the local data service
        List<Candlestick> existingCandlesticks = candlestickDataService.getCandlestickData(symbol, startDateTime, endDateTime, interval);

        // Add existing candlesticks to the set
        for (Candlestick candlestick : existingCandlesticks) {
            CandlestickWrapper wrapper = new CandlestickWrapper(candlestick);
            allCandlesticksSet.add(wrapper);
        }

        // Find missing candlesticks that need to be fetched from the API
        List<Candlestick> missingCandlesticks = findMissingCandlesticks(allCandlesticksSet, startDateTime, endDateTime, interval);

        if (!missingCandlesticks.isEmpty()) {
            Long missingStartTime = missingCandlesticks.get(0).getOpenTime();
            Long missingEndTime = missingCandlesticks.get(missingCandlesticks.size() - 1).getCloseTime();

            // Check API request limits
            if (requestCount >= MAX_REQUESTS_PER_MINUTE) {
                waitForApiLimitReset();
                requestCount = 0;
            }

            // Fetch missing candlesticks from Binance API
            List<Candlestick> candlesticksFromApi = binanceApiClient.getCandlestickBars(symbol, CandlestickInterval.valueOf(interval), MAX_CANDLESTICKS_PER_REQUEST, missingStartTime, missingEndTime);
            requestCount += MAX_CANDLESTICKS_PER_REQUEST;

            // Save fetched candlesticks to the local data service and add to the set
            for (Candlestick candlestick : candlesticksFromApi) {
                LocalDateTime openTime = DateUtils.convertMillisToLocalDateTime(candlestick.getOpenTime());
                LocalDateTime closeTime = DateUtils.convertMillisToLocalDateTime(candlestick.getCloseTime());

                if (!candlestickDataService.existsBySymbolAndOpenTime(symbol, openTime, closeTime, interval)) {
                    candlestickDataService.saveCandlestickData(symbol, candlestick, interval);
                    allCandlesticksSet.add(new CandlestickWrapper(candlestick));
                }
            }
        }

        // Return the list of all candlesticks
        return allCandlesticksSet.stream().map(CandlestickWrapper::candlestick).collect(Collectors.toList());
    }

    //Adjusts the given time for the specified interval
    private LocalDateTime adjustTimeForInterval(LocalDateTime time, String interval) {
        return switch (interval) {
            case "ONE_MINUTE" -> time.plusMinutes(1);
            case "THREE_MINUTES" -> time.plusMinutes(3);
            case "FIVE_MINUTES" -> time.plusMinutes(5);
            case "FIFTEEN_MINUTES" -> time.plusMinutes(15);
            case "HALF_HOURLY" -> time.plusMinutes(30);
            case "HOURLY" -> time.plusHours(1);
            case "TWO_HOURLY" -> time.plusHours(2);
            case "FOUR_HOURLY" -> time.plusHours(4);
            case "SIX_HOURLY" -> time.plusHours(6);
            case "EIGHT_HOURLY" -> time.plusHours(8);
            case "TWELVE_HOURLY" -> time.plusHours(12);
            case "DAILY" -> time.plusDays(1);
            case "THREE_DAILY" -> time.plusDays(3);
            case "WEEKLY" -> time.plusWeeks(1);
            case "MONTHLY" -> time.plusMonths(1);
            default -> throw new IllegalArgumentException("Unsupported interval: " + interval);
        };
    }

    //Waits for the API limit to reset by sleeping the thread for one minute
    private void waitForApiLimitReset() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //Finds missing candlesticks within the given date range and interval that are not present in the provided set
    private List<Candlestick> findMissingCandlesticks(Set<CandlestickWrapper> allCandlesticksSet, LocalDateTime startDateTime, LocalDateTime endDateTime, String interval) {
        List<Candlestick> missingCandlesticks = new ArrayList<>();
        LocalDateTime currentDateTime = startDateTime;

        while (currentDateTime.isBefore(endDateTime)) {
            LocalDateTime nextDateTime = adjustTimeForInterval(currentDateTime, interval);
            long currentMillis = currentDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long nextMillis = nextDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            // Check if the candlestick exists in the set
            boolean exists = allCandlesticksSet.stream()
                    .anyMatch(c -> c.candlestick().getOpenTime() == currentMillis && c.candlestick().getCloseTime() == nextMillis);

            // If not, add to missing candlesticks
            if (!exists) {
                Candlestick missingCandlestick = new Candlestick();
                missingCandlestick.setOpenTime(currentMillis);
                missingCandlestick.setCloseTime(nextMillis);
                missingCandlesticks.add(missingCandlestick);
            }

            currentDateTime = nextDateTime;
        }

        return missingCandlesticks;
    }
}
