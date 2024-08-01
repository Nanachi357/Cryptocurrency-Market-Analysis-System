package com.example.CryptocurrencyMarketAnalysisSystem.service.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.service.CandlestickDataService;
import com.example.CryptocurrencyMarketAnalysisSystem.util.CandlestickWrapper;
import com.example.CryptocurrencyMarketAnalysisSystem.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BinanceHistoricalDataService {
     static final Logger logger = LoggerFactory.getLogger(BinanceHistoricalDataService.class);


     static final int MAX_CANDLESTICKS_PER_REQUEST = 1000;
     static final int MAX_REQUESTS_PER_MINUTE = 6000;
     int requestCount = 0;
     final BinanceApiRestClient binanceApiClient;
     final CandlestickDataService candlestickDataService;

    @Autowired
    public BinanceHistoricalDataService(CandlestickDataService candlestickDataService) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        this.binanceApiClient = factory.newRestClient();
        this.candlestickDataService = candlestickDataService;
    }

    /**
     * Retrieves historical candlestick data for the specified symbol and interval.
     * This method fetches existing candlesticks from the database, identifies missing candlesticks,
     * and retrieves them from the Binance API. It ensures that all missing candlesticks are fetched
     * and stored in the database.
     *
     * @param symbol The trading pair symbol (e.g., BTCUSDT).
     * @param interval The candlestick interval (e.g., ONE_MINUTE, FIVE_MINUTES).
     * @param startTime The start time of the data in milliseconds since epoch.
     * @param endTime The end time of the data in milliseconds since epoch.
     * @return A list of Candlestick objects with the requested data.
     */
    public List<Candlestick> getHistoricalCandlestickData(String symbol, CandlestickInterval interval, Long startTime, Long endTime) {
        Instant startInstant = Instant.ofEpochMilli(startTime);
        Instant endInstant = Instant.ofEpochMilli(endTime);
        validateTimeRange(startInstant, endInstant);

        ZonedDateTime startDateTime = DateUtils.convertMillisToUtcZonedDateTime(startTime);
        ZonedDateTime endDateTime = DateUtils.convertMillisToUtcZonedDateTime(endTime);

        Set<CandlestickWrapper> allCandlesticksSet = fetchExistingCandlesticks(symbol, startDateTime, endDateTime, interval);
        List<Candlestick> missingCandlesticks = findMissingCandlesticks(allCandlesticksSet, startDateTime, endDateTime, interval);
        Set<Long> processedStartTimes = new HashSet<>();

        while (!missingCandlesticks.isEmpty()) {
            missingCandlesticks = processMissingCandlesticks(symbol, interval, startTime, endTime, missingCandlesticks, processedStartTimes, startDateTime, endDateTime, allCandlesticksSet);
        }

        return allCandlesticksSet.stream().map(CandlestickWrapper::candlestick).collect(Collectors.toList());
    }

    /**
     * Validates that the start time is before the end time.
     *
     * @param startInstant The start time as an Instant.
     * @param endInstant The end time as an Instant.
     * @throws IllegalArgumentException If the start time is after the end time.
     */
    boolean validateTimeRange(Instant startInstant, Instant endInstant) {
        if (startInstant.isAfter(endInstant)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        return false;
    }

    /**
     * Fetches existing candlesticks from the database for the specified symbol, time range, and interval.
     *
     * @param symbol The trading pair symbol.
     * @param startDateTime The start time as a ZonedDateTime.
     * @param endDateTime The end time as a ZonedDateTime.
     * @param interval The candlestick interval.
     * @return A set of CandlestickWrapper objects containing the existing candlestick data.
     */
     Set<CandlestickWrapper> fetchExistingCandlesticks(String symbol, ZonedDateTime startDateTime, ZonedDateTime endDateTime, CandlestickInterval interval) {
        Set<CandlestickWrapper> allCandlesticksSet = new HashSet<>();
        logger.info("fetchExistingCandlesticks symbol: {}, startDateTime: {}, endDateTime: {}, interval: {}", symbol, startDateTime, endDateTime, interval);
        List<Candlestick> existingCandlesticks = candlestickDataService.getCandlestickData(symbol, startDateTime, endDateTime, interval);
        logger.info("Fetched candlesticks by fetchExistingCandlesticks: " + existingCandlesticks);
        for (Candlestick candlestick : existingCandlesticks) {
            allCandlesticksSet.add(new CandlestickWrapper(candlestick));
        }
        return allCandlesticksSet;
    }

    /**
     * Processes the list of missing candlesticks by fetching them from the API and updating the database.
     *
     * @param symbol The trading pair symbol.
     * @param interval The candlestick interval.
     * @param startTime The start time of the data in milliseconds since epoch.
     * @param endTime The end time of the data in milliseconds since epoch.
     * @param missingCandlesticks The list of missing candlesticks.
     * @param processedStartTimes The set of start times that have been processed.
     * @param startDateTime The start time as a ZonedDateTime.
     * @param endDateTime The end time as a ZonedDateTime.
     * @param allCandlesticksSet The set of all candlesticks.
     * @return The updated list of missing candlesticks after processing.
     */
     List<Candlestick> processMissingCandlesticks(String symbol, CandlestickInterval interval, Long startTime, Long endTime, List<Candlestick> missingCandlesticks, Set<Long> processedStartTimes, ZonedDateTime startDateTime, ZonedDateTime endDateTime, Set<CandlestickWrapper> allCandlesticksSet) {
        Long missingStartTime = missingCandlesticks.get(0).getOpenTime();
        if (processedStartTimes.contains(missingStartTime)) {
            logger.warn("Start time {} has already been processed, breaking out of the loop", missingStartTime);
            return Collections.emptyList();
        }
        processedStartTimes.add(missingStartTime);

        Long missingEndTime = Math.min(missingStartTime + MAX_CANDLESTICKS_PER_REQUEST * getCandlestickIntervalMillis(interval), endTime);

        if (requestCount >= MAX_REQUESTS_PER_MINUTE) {
            waitForApiLimitReset();
            requestCount = 0;
        }

        logger.info("Fetching candlesticks from API: symbol={}, interval={}, start={}, end={}",
                 symbol, interval, missingStartTime, missingEndTime);
        List<Candlestick> candlesticksFromApi = fetchCandlesticksFromApi(symbol, interval, missingStartTime, missingEndTime);
        if (candlesticksFromApi.isEmpty()) {
            logger.warn("No candlesticks returned from API, breaking out of the loop");
            return Collections.emptyList();
        }

        logger.info("Validating candlestick order for symbol={}, interval={}", symbol, interval);
        validateCandlestickOrder(candlesticksFromApi);

        logger.info("Saving candlesticks to database: symbol={}, interval={}", symbol, interval);
        saveCandlesticksToDatabase(symbol, candlesticksFromApi, interval);

        logger.info("Updating candlestick set: symbol={}, startDateTime={}, endDateTime={}, interval={}",
                 symbol, startDateTime, endDateTime, interval);
        List<Candlestick> newMissingCandlesticks = updateCandlestickSet(symbol, startDateTime, endDateTime, interval, allCandlesticksSet);
        if (newMissingCandlesticks.isEmpty() || newMissingCandlesticks.equals(missingCandlesticks)) {
            logger.warn("No progress in fetching missing candlesticks, breaking out of the loop");
            return Collections.emptyList();
        }

        return newMissingCandlesticks;
    }

    /**
     * Fetches candlesticks from the Binance API for the specified symbol, interval, and time range.
     *
     * @param symbol The trading pair symbol.
     * @param interval The candlestick interval.
     * @param missingStartTime The start time of the missing candlesticks in milliseconds since epoch.
     * @param missingEndTime The end time of the missing candlesticks in milliseconds since epoch.
     * @return A list of Candlestick objects retrieved from the API.
     */
     List<Candlestick> fetchCandlesticksFromApi(String symbol, CandlestickInterval interval, Long missingStartTime, Long missingEndTime) {
         logger.info("Fetching missing candlesticks from {} to {}", missingStartTime, missingEndTime);
         List<Candlestick> candlesticksFromApi = binanceApiClient.getCandlestickBars(symbol, interval, MAX_CANDLESTICKS_PER_REQUEST, missingStartTime, missingEndTime);
         requestCount += MAX_CANDLESTICKS_PER_REQUEST;
         logger.info("Candlesticks fetched from API: {}", candlesticksFromApi.size());

         candlesticksFromApi.forEach(c -> logger.info("Fetched candlestick from API: openTime={}, closeTime={}", c.getOpenTime(), c.getCloseTime()));

         return candlesticksFromApi;
    }

    /**
     * Validates that the candlesticks are in chronological order based on their open times.
     *
     * @param candlesticksFromApi The list of candlesticks to validate.
     * @throws IllegalStateException If any candlestick is out of order.
     */
    void validateCandlestickOrder(List<Candlestick> candlesticksFromApi) {
        long lastOpenTime = -1;
        for (Candlestick candlestick : candlesticksFromApi) {
            if (candlestick.getOpenTime() <= lastOpenTime) {
                logger.error("Candlestick out of order: {}", candlestick);
                throw new IllegalStateException("Candlesticks are not in chronological order");
            }
            lastOpenTime = candlestick.getOpenTime();
        }
    }

    /**
     * Saves the candlesticks to the database if they do not already exist.
     *
     * @param symbol The trading pair symbol.
     * @param candlesticksFromApi The list of candlesticks to save.
     * @param interval The candlestick interval.
     */
     void saveCandlesticksToDatabase(String symbol, List<Candlestick> candlesticksFromApi, CandlestickInterval interval) {
        for (Candlestick candlestick : candlesticksFromApi) {
            ZonedDateTime openTime = DateUtils.convertMillisToUtcZonedDateTime(candlestick.getOpenTime());
            ZonedDateTime closeTime = DateUtils.convertMillisToUtcZonedDateTime(candlestick.getCloseTime());
            logger.info("Candlestick to be processed: {},CandlestickInterval {}", candlestick, interval);

            boolean exists = candlestickDataService.existsBySymbolAndOpenTime(symbol, openTime, closeTime, interval);
            logger.info("Checking if candlestick exists: {}", exists);

            if (!exists) {
                candlestickDataService.saveCandlestickData(symbol, candlestick, interval);
                logger.info("Saving new candlestick to database: {}", candlestick);
            } else {
                logger.info("Candlestick already exists in database: {}", candlestick);
            }
        }
    }

    /**
     * Updates the set of all candlesticks by fetching the latest data from the database and finding missing candlesticks.
     *
     * @param symbol The trading pair symbol.
     * @param startDateTime The start time as a ZonedDateTime.
     * @param endDateTime The end time as a ZonedDateTime.
     * @param interval The candlestick interval.
     * @param allCandlesticksSet The set of all candlesticks to update.
     * @return A list of missing candlesticks after updating.
     */
     List<Candlestick> updateCandlestickSet(String symbol, ZonedDateTime startDateTime, ZonedDateTime endDateTime, CandlestickInterval interval, Set<CandlestickWrapper> allCandlesticksSet) {
        List<Candlestick> existingCandlesticks = candlestickDataService.getCandlestickData(symbol, startDateTime, endDateTime, interval);
        allCandlesticksSet.clear();
        for (Candlestick candlestick : existingCandlesticks) {
            allCandlesticksSet.add(new CandlestickWrapper(candlestick));
        }
        return findMissingCandlesticks(allCandlesticksSet, startDateTime, endDateTime, interval);
    }

    //Adjusts the given time for the specified interval
    ZonedDateTime adjustTimeForInterval(ZonedDateTime time, CandlestickInterval interval) {
        return switch (interval) {
            case ONE_MINUTE -> time.plusMinutes(1);
            case THREE_MINUTES -> time.plusMinutes(3);
            case FIVE_MINUTES -> time.plusMinutes(5);
            case FIFTEEN_MINUTES -> time.plusMinutes(15);
            case HALF_HOURLY -> time.plusMinutes(30);
            case HOURLY -> time.plusHours(1);
            case TWO_HOURLY -> time.plusHours(2);
            case FOUR_HOURLY -> time.plusHours(4);
            case SIX_HOURLY -> time.plusHours(6);
            case EIGHT_HOURLY -> time.plusHours(8);
            case TWELVE_HOURLY -> time.plusHours(12);
            case DAILY -> time.plusDays(1);
            case THREE_DAILY -> time.plusDays(3);
            case WEEKLY -> time.plusWeeks(1);
            case MONTHLY -> time.plusMonths(1);
        };
    }

    //Waits for the API limit to reset by sleeping the thread for one minute
    void waitForApiLimitReset() {
        long currentTimeMillis = System.currentTimeMillis();
        long millisUntilNextMinute = 60000 - (currentTimeMillis % 60000);

        logger.info("Waiting for {} milliseconds until the next minute", millisUntilNextMinute);
        try {
            sleep(millisUntilNextMinute);
        } catch (InterruptedException e) {
            logger.error("Wait for API limit reset interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    //Finds missing candlesticks within the given date range and interval that are not present in the provided set
    public List<Candlestick> findMissingCandlesticks(Set<CandlestickWrapper> allCandlesticksSet, ZonedDateTime startDateTime, ZonedDateTime endDateTime, CandlestickInterval interval) {
        List<Candlestick> missingCandlesticks = new ArrayList<>();
        ZonedDateTime currentDateTime = startDateTime;
        Set<ZonedDateTime> existingOpenTimes = allCandlesticksSet.stream()
                .map(c -> DateUtils.convertMillisToUtcZonedDateTime(c.getCandlestick().getOpenTime()))
                .collect(Collectors.toSet());

        logger.info("Start date-time: {}", startDateTime);
        logger.info("End date-time: {}", endDateTime);
        logger.info("Existing open times: {}", existingOpenTimes);

        while (currentDateTime.isBefore(endDateTime)) {
            logger.info("Current date-time: {}", currentDateTime);
            ZonedDateTime currentDateTimeUTC = currentDateTime.withZoneSameInstant(ZoneId.of("UTC"));
            if (!existingOpenTimes.contains(currentDateTimeUTC)) {
                ZonedDateTime nextDateTime = adjustTimeForInterval(currentDateTime, interval);
                Candlestick candlestick = new Candlestick();
                candlestick.setOpenTime(DateUtils.convertZonedDateTimeToMillis(currentDateTimeUTC));
                candlestick.setCloseTime(DateUtils.convertZonedDateTimeToMillis(nextDateTime.withZoneSameInstant(ZoneId.of("UTC"))) - 1);
                missingCandlesticks.add(candlestick);
                logger.info("Missing candlestick added: Candlestick[openTime={}, closeTime={}]", currentDateTime, nextDateTime);
            }
            currentDateTime = adjustTimeForInterval(currentDateTime, interval);
        }

        logger.info("Final list of missing candlesticks: {}", missingCandlesticks.stream()
                .map(c -> DateUtils.convertMillisToUtcLocalDateTime(c.getOpenTime()))
                .collect(Collectors.toList()));
        return missingCandlesticks;
    }

    // Returns the interval duration in milliseconds
    long getCandlestickIntervalMillis(CandlestickInterval interval) {
        return switch (interval) {
            case ONE_MINUTE -> 60000L;
            case THREE_MINUTES -> 3 * 60000L;
            case FIVE_MINUTES -> 5 * 60000L;
            case FIFTEEN_MINUTES -> 15 * 60000L;
            case HALF_HOURLY -> 30 * 60000L;
            case HOURLY -> 60 * 60000L;
            case TWO_HOURLY -> 2 * 60 * 60000L;
            case FOUR_HOURLY -> 4 * 60 * 60000L;
            case SIX_HOURLY -> 6 * 60 * 60000L;
            case EIGHT_HOURLY -> 8 * 60 * 60000L;
            case TWELVE_HOURLY -> 12 * 60 * 60000L;
            case DAILY -> 24 * 60 * 60000L;
            case THREE_DAILY -> 3 * 24 * 60 * 60000L;
            case WEEKLY -> 7 * 24 * 60 * 60000L;
            case MONTHLY -> 30 * 24 * 60 * 60000L; // Approximation
        };
    }
}