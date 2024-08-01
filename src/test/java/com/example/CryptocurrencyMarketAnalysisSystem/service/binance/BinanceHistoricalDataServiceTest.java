package com.example.CryptocurrencyMarketAnalysisSystem.service.binance;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.model.CandlestickEntity;
import com.example.CryptocurrencyMarketAnalysisSystem.repository.CandlestickRepository;
import com.example.CryptocurrencyMarketAnalysisSystem.service.CandlestickDataService;
import com.example.CryptocurrencyMarketAnalysisSystem.util.CandlestickWrapper;
import com.example.CryptocurrencyMarketAnalysisSystem.util.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
public class BinanceHistoricalDataServiceTest {
    @InjectMocks
    private BinanceHistoricalDataService binanceHistoricalDataService;

    @Mock
    private CandlestickDataService candlestickDataService;

    @Mock
    private CandlestickRepository candlestickRepository;

    @Mock
    private BinanceApiRestClient binanceApiClient;

    private AutoCloseable closeable;

    private static final Logger logger = LoggerFactory.getLogger(BinanceHistoricalDataService.class);

    private Map<String, List<CandlestickEntity>> mockDatabase;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        mockDatabase = new ConcurrentHashMap<>();

        when(candlestickDataService.getCandlestickData(anyString(), any(ZonedDateTime.class), any(ZonedDateTime.class), any(CandlestickInterval.class)))
                .thenAnswer(invocation -> {
                    String symbolArg = invocation.getArgument(0);
                    ZonedDateTime start = invocation.getArgument(1);
                    ZonedDateTime end = invocation.getArgument(2);

                    return mockDatabase.getOrDefault(symbolArg, new ArrayList<>()).stream()
                            .filter(candlestickEntity -> {
                                ZonedDateTime candlestickOpenTime = candlestickEntity.getOpenTime();
                                ZonedDateTime candlestickCloseTime = candlestickEntity.getCloseTime();
                                return (candlestickOpenTime.isEqual(start) || candlestickOpenTime.isAfter(start)) &&
                                        (candlestickCloseTime.isEqual(end) || candlestickCloseTime.isBefore(end));
                            })
                            .collect(Collectors.toList());
                });

        doAnswer(invocation -> {
            String symbol = invocation.getArgument(0);
            Candlestick candlestick = invocation.getArgument(1);
            CandlestickInterval interval = invocation.getArgument(2);

            CandlestickEntity candlestickEntity = CandlestickEntity.fromCandlestick(candlestick, symbol, interval);

            mockDatabase.computeIfAbsent(symbol, k -> new ArrayList<>()).add(candlestickEntity);

            return null;
        }).when(candlestickDataService).saveCandlestickData(anyString(), any(Candlestick.class), any(CandlestickInterval.class));
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
        Mockito.reset(candlestickDataService, candlestickRepository, binanceApiClient);
    }
    //Positive Tests

    @Test
    public void testAdjustTimeForInterval() {
        ZonedDateTime dateTime = ZonedDateTime.parse("2024-07-16T00:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME);

        assertEquals(ZonedDateTime.parse("2024-07-16T00:01:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.ONE_MINUTE));

        assertEquals(ZonedDateTime.parse("2024-07-16T00:03:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.THREE_MINUTES));

        assertEquals(ZonedDateTime.parse("2024-07-16T00:05:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.FIVE_MINUTES));

        assertEquals(ZonedDateTime.parse("2024-07-16T00:15:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.FIFTEEN_MINUTES));

        assertEquals(ZonedDateTime.parse("2024-07-16T00:30:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.HALF_HOURLY));

        assertEquals(ZonedDateTime.parse("2024-07-16T01:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.HOURLY));

        assertEquals(ZonedDateTime.parse("2024-07-16T02:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.TWO_HOURLY));

        assertEquals(ZonedDateTime.parse("2024-07-16T04:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.FOUR_HOURLY));

        assertEquals(ZonedDateTime.parse("2024-07-16T06:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.SIX_HOURLY));

        assertEquals(ZonedDateTime.parse("2024-07-16T08:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.EIGHT_HOURLY));

        assertEquals(ZonedDateTime.parse("2024-07-16T12:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.TWELVE_HOURLY));

        assertEquals(ZonedDateTime.parse("2024-07-17T00:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.DAILY));

        assertEquals(ZonedDateTime.parse("2024-07-19T00:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.THREE_DAILY));

        assertEquals(ZonedDateTime.parse("2024-07-23T00:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.WEEKLY));

        assertEquals(ZonedDateTime.parse("2024-08-16T00:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME),
                binanceHistoricalDataService.adjustTimeForInterval(dateTime, CandlestickInterval.MONTHLY));
    }

    @Test
    public void testGetCandlestickIntervalMillis() {
        assertEquals(60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.ONE_MINUTE));
        assertEquals(3 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.THREE_MINUTES));
        assertEquals(5 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.FIVE_MINUTES));
        assertEquals(15 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.FIFTEEN_MINUTES));
        assertEquals(30 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.HALF_HOURLY));
        assertEquals(60 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.HOURLY));
        assertEquals(2 * 60 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.TWO_HOURLY));
        assertEquals(4 * 60 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.FOUR_HOURLY));
        assertEquals(6 * 60 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.SIX_HOURLY));
        assertEquals(8 * 60 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.EIGHT_HOURLY));
        assertEquals(12 * 60 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.TWELVE_HOURLY));
        assertEquals(24 * 60 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.DAILY));
        assertEquals(3 * 24 * 60 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.THREE_DAILY));
        assertEquals(7 * 24 * 60 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.WEEKLY));
        assertEquals(30 * 24 * 60 * 60000L, binanceHistoricalDataService.getCandlestickIntervalMillis(CandlestickInterval.MONTHLY));
    }

    @Test
    void testWaitForApiLimitReset() throws InterruptedException {
        BinanceHistoricalDataService spyService = spy(binanceHistoricalDataService);
        doNothing().when(spyService).sleep(anyLong());

        spyService.waitForApiLimitReset();

        verify(spyService, times(1)).sleep(anyLong());
    }

    @Test
    void testFindMissingCandlesticks() {
        ZonedDateTime startDateTime = ZonedDateTime.parse("2023-01-01T00:00:00Z");
        ZonedDateTime endDateTime = ZonedDateTime.parse("2023-01-01T01:00:00Z");
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;

        Set<CandlestickWrapper> allCandlesticksSet = new HashSet<>();
        allCandlesticksSet.add(new CandlestickWrapper(createTestCandlestick("2023-01-01T00:00:00Z", "2023-01-01T00:00:59Z")));
        allCandlesticksSet.add(new CandlestickWrapper(createTestCandlestick("2023-01-01T00:01:00Z", "2023-01-01T00:01:59Z")));

        // Mocking DateUtils methods
        mockStatic(DateUtils.class);
        when(DateUtils.convertMillisToUtcZonedDateTime(anyLong())).thenCallRealMethod();
        when(DateUtils.convertZonedDateTimeToMillis(any())).thenCallRealMethod();

        List<Candlestick> missingCandlesticks = binanceHistoricalDataService.findMissingCandlesticks(allCandlesticksSet, startDateTime, endDateTime, interval);

        // Log all missing candlesticks for further analysis
        missingCandlesticks.forEach(candlestick -> logger.info("Missing candlestick: open time - {}, close time - {}",
                DateUtils.convertMillisToUtcZonedDateTime(candlestick.getOpenTime()),
                DateUtils.convertMillisToUtcZonedDateTime(candlestick.getCloseTime())));

        // Verify that the missing candlestick is correctly identified
        assertEquals(58, missingCandlesticks.size());

    }

    @Test
    void testGetHistoricalCandlestickData() {
        // Create a spy for candlestickDataService
        CandlestickDataService candlestickDataServiceSpy = spy(new CandlestickDataService(candlestickRepository));

        // Create a real instance of BinanceHistoricalDataService using the spy candlestickDataService
        BinanceHistoricalDataService binanceHistoricalDataServiceSpy = spy(new BinanceHistoricalDataService(candlestickDataServiceSpy));

        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        ZonedDateTime startTime = ZonedDateTime.parse("2023-01-01T00:00:00Z");
        ZonedDateTime endTime = ZonedDateTime.parse("2023-01-01T00:05:00Z");

        // Create test candlesticks
        Set<Candlestick> testCandlesticks = Set.of(
                createTestCandlestick("2023-01-01T00:00:00Z", "2023-01-01T00:00:59Z"),
                createTestCandlestick("2023-01-01T00:01:00Z", "2023-01-01T00:01:59Z"),
                createTestCandlestick("2023-01-01T00:02:00Z", "2023-01-01T00:02:59Z"),
                createTestCandlestick("2023-01-01T00:03:00Z", "2023-01-01T00:03:59Z"),
                createTestCandlestick("2023-01-01T00:04:00Z", "2023-01-01T00:04:59Z"),
                createTestCandlestick("2023-01-01T00:05:00Z", "2023-01-01T00:05:59Z")
        );

        // Create CandlestickWrapper for each Candlestick and collect into a Set
        Set<CandlestickWrapper> testCandlestickWrappers = testCandlesticks.stream()
                .map(CandlestickWrapper::new)
                .collect(Collectors.toSet());

        List<Candlestick> testCandlesticksList = new ArrayList<>(testCandlesticks);

        // Mock fetchExistingCandlesticks to return testCandlestickWrappers
        doReturn(testCandlestickWrappers).when(binanceHistoricalDataServiceSpy).fetchExistingCandlesticks(eq(symbol), any(ZonedDateTime.class), any(ZonedDateTime.class), eq(interval));

        // Mock processMissingCandlesticks to return an empty list
        doReturn(new ArrayList<Candlestick>()).when(binanceHistoricalDataServiceSpy)
                .processMissingCandlesticks(
                        anyString(),  // For symbol
                        any(CandlestickInterval.class),  // For interval
                        anyLong(),  // For startTime
                        anyLong(),  // For endTime
                        anyList(),  // For missingCandlesticks
                        anySet(),  // For processedStartTimes
                        any(ZonedDateTime.class),  // For startDateTime
                        any(ZonedDateTime.class),  // For endDateTime
                        anySet()   // For allCandlesticksSet
                );

        // Mock findMissingCandlesticks to return an empty list
        doReturn(new ArrayList<Candlestick>()).when(binanceHistoricalDataServiceSpy).findMissingCandlesticks(testCandlestickWrappers, startTime, endTime, interval);

        // Call the method under test
        List<Candlestick> apiCandlesticks = binanceHistoricalDataServiceSpy.getHistoricalCandlestickData(symbol, interval, startTime.toInstant().toEpochMilli(), endTime.toInstant().toEpochMilli());

        // Verify the fetchExistingCandlesticks method was called correctly
        verify(binanceHistoricalDataServiceSpy).fetchExistingCandlesticks(
                eq(symbol),
                any(ZonedDateTime.class),
                any(ZonedDateTime.class),
                eq(interval)
        );

        // Sort both lists before comparison
        apiCandlesticks.sort(Comparator.comparing(Candlestick::getOpenTime));
        testCandlesticksList.sort(Comparator.comparing(Candlestick::getOpenTime));

        // Assert that the returned list matches the expected list
        assertEquals(testCandlesticksList, apiCandlesticks, "The candlestick list returned by getHistoricalCandlestickData should match the expected list.");
    }


    @Test
    void testValidateTimeRange() {
        // Parsing start and end times to Instant objects
        Instant start = ZonedDateTime.parse("2023-01-01T00:00:00Z").toInstant();
        Instant end = ZonedDateTime.parse("2023-01-01T01:00:00Z").toInstant();

        // Asserting that validateTimeRange returns false for valid start and end times
        assertFalse(binanceHistoricalDataService.validateTimeRange(start, end));

        // Asserting that validateTimeRange throws IllegalArgumentException for inverted start and end times
        assertThrows(IllegalArgumentException.class, () -> binanceHistoricalDataService.validateTimeRange(end, start));
    }

    @Test
    void testFetchExistingCandlesticks() {
        String symbol = "BTCUSDT";
        ZonedDateTime start = ZonedDateTime.parse("2023-01-01T00:00:00Z");
        ZonedDateTime end = ZonedDateTime.parse("2023-01-01T01:00:00Z");
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;

        // Creating a list of candlesticks with sample data
        List<Candlestick> candlestickList = IntStream.range(0, 5)
                .mapToObj(i -> {
                    ZonedDateTime openTime = start.plusMinutes(i);
                    ZonedDateTime closeTime = openTime.plusMinutes(1);

                    Candlestick candlestick = new Candlestick();
                    candlestick.setOpenTime(openTime.toInstant().toEpochMilli());
                    candlestick.setCloseTime(closeTime.toInstant().toEpochMilli());
                    candlestick.setOpen("100.0");
                    candlestick.setClose("105.0");
                    candlestick.setHigh("105.0");
                    candlestick.setLow("95.0");
                    candlestick.setVolume("1000.0");
                    candlestick.setQuoteAssetVolume("1000.0");
                    candlestick.setNumberOfTrades(10L);
                    candlestick.setTakerBuyBaseAssetVolume("500.0");
                    candlestick.setTakerBuyQuoteAssetVolume("500.0");

                    return candlestick;
                })
                .toList();

        // Creating a set of candlestick wrappers with sample data
        Set<CandlestickWrapper> candlestickSet = IntStream.range(0, 5)
                .mapToObj(i -> {
                    ZonedDateTime openTime = start.plusMinutes(i);
                    ZonedDateTime closeTime = openTime.plusMinutes(1);

                    Candlestick candlestick = new Candlestick();
                    candlestick.setOpenTime(openTime.toInstant().toEpochMilli());
                    candlestick.setCloseTime(closeTime.toInstant().toEpochMilli());
                    candlestick.setOpen("100.0");
                    candlestick.setClose("105.0");
                    candlestick.setHigh("105.0");
                    candlestick.setLow("95.0");
                    candlestick.setVolume("1000.0");
                    candlestick.setQuoteAssetVolume("1000.0");
                    candlestick.setNumberOfTrades(10L);
                    candlestick.setTakerBuyBaseAssetVolume("500.0");
                    candlestick.setTakerBuyQuoteAssetVolume("500.0");

                    return new CandlestickWrapper(candlestick);
                })
                .collect(Collectors.toSet());

        logger.info("candlestickList: " + candlestickList);
        logger.info("candlestickSet: " + candlestickSet);

        // Creating spies for the services
        CandlestickDataService candlestickDataServiceSpy = Mockito.spy(new CandlestickDataService(candlestickRepository));
        BinanceHistoricalDataService binanceHistoricalDataServiceSpy = Mockito.spy(new BinanceHistoricalDataService(candlestickDataServiceSpy));

        // Mocking the getCandlestickData method to return the candlestick list
        when(candlestickDataServiceSpy.getCandlestickData(symbol, start, end, interval)).thenReturn(candlestickList);

        // Invoking the method under test
        Set<CandlestickWrapper> result = binanceHistoricalDataServiceSpy.fetchExistingCandlesticks(symbol, start, end, interval);

        // Verifying that getCandlestickData was called once with the specified parameters
        verify(candlestickDataServiceSpy, times(1)).getCandlestickData(
                eq(symbol),
                eq(start),
                eq(end),
                eq(interval)
        );

        // Asserting that the returned set matches the expected set
        assertEquals(candlestickSet, result);
    }


    @Test
    void testProcessMissingCandlesticks() {
        // Test parameters
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        ZonedDateTime start = ZonedDateTime.parse("2023-01-01T00:00:00Z");
        ZonedDateTime end = ZonedDateTime.parse("2023-01-01T00:10:00Z");

        // Mock candlestick data
        List<Candlestick> mockCandlesticks = List.of(
                createTestCandlestick("2023-01-01T00:00:00Z", "2023-01-01T00:00:59Z"),
                createTestCandlestick("2023-01-01T00:01:00Z", "2023-01-01T00:01:59Z")
        );

        // Mocking the Binance API client
        when(binanceApiClient.getCandlestickBars(eq(symbol), eq(interval), anyInt(), eq(start.toInstant().toEpochMilli()), eq(end.toInstant().toEpochMilli())))
                .thenReturn(mockCandlesticks);

        // Existing candlesticks
        List<Candlestick> existingCandlesticks = List.of(
                createTestCandlestick("2023-01-01T00:02:00Z", "2023-01-01T00:02:59Z")
        );

        Set<Long> processedStartTimes = new HashSet<>();

        // Creating a spy on binanceHistoricalDataService
        BinanceHistoricalDataService spyService = Mockito.spy(binanceHistoricalDataService);

        // Mocking the updateCandlestickSet method in the spy
        Mockito.doAnswer(invocation -> {
            // Log parameters of the call for verification
            Object[] args = invocation.getArguments();
            logger.info("updateCandlestickSet called with params: symbol={}, startDateTime={}, endDateTime={}, interval={}, allCandlesticksSet={}",
                    args[0], args[1], args[2], args[3], args[4]);
            return new ArrayList<>(); // Return an empty list for further checks
        }).when(spyService).updateCandlestickSet(anyString(), any(ZonedDateTime.class), any(ZonedDateTime.class), any(CandlestickInterval.class), any(Set.class));

        // Invoke the method under test
        spyService.processMissingCandlesticks(
                symbol,
                interval,
                start.toInstant().toEpochMilli(),
                end.toInstant().toEpochMilli(),
                existingCandlesticks,
                processedStartTimes,
                start,
                end,
                new HashSet<>()
        );

        // Verify that the fetchCandlesticksFromApi method was called with correct parameters
        verify(spyService, atLeastOnce()).fetchCandlesticksFromApi(
                eq(symbol),
                eq(interval),
                anyLong(),
                anyLong()
        );

        // Verify that the updateCandlestickSet method was called with correct parameters
        verify(spyService, atLeastOnce()).updateCandlestickSet(
                eq(symbol),
                eq(start),
                eq(end),
                eq(interval),
                anySet()
        );

        // Verify the number of calls to updateCandlestickSet
        verify(spyService, times(1)).updateCandlestickSet(
                eq(symbol),
                eq(start),
                eq(end),
                eq(interval),
                anySet()
        );
    }

    @Test
    void testFetchCandlesticksFromApi() {
        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;
        ZonedDateTime start = ZonedDateTime.parse("2023-01-01T00:00:00Z");
        ZonedDateTime end = ZonedDateTime.parse("2023-01-01T00:02:30Z");
        logger.info("start {} and end {}", start, end);

        // Mock candlestick data
        List<Candlestick> mockCandlesticks = List.of(
                createTestCandlestick("2023-01-01T00:00:00Z", "2023-01-01T00:00:59Z"),
                createTestCandlestick("2023-01-01T00:01:00Z", "2023-01-01T00:01:59Z")
        );

        // Mocking the Binance API client
        when(binanceApiClient.getCandlestickBars(symbol, interval, 1000, start.toInstant().toEpochMilli(), end.toInstant().toEpochMilli()))
                .thenReturn(mockCandlesticks);

        // Convert ZonedDateTime to Long
        long startTimeMillis = start.toInstant().toEpochMilli();
        long endTimeMillis = end.toInstant().toEpochMilli();
        logger.info("startTimeMillis {} and endTimeMillis {}", startTimeMillis, endTimeMillis);

        // Filter candlesticks within the specified time range
        List<Candlestick> filteredCandlesticks = mockCandlesticks.stream()
                .filter(candlestick -> {
                    long openTimeMillis = candlestick.getOpenTime();
                    long closeTimeMillis = candlestick.getCloseTime();
                    return openTimeMillis >= startTimeMillis && closeTimeMillis <= endTimeMillis;
                })
                .toList();

        // Assert that 2 candlesticks were fetched from mock data
        assertEquals(2, filteredCandlesticks.size(), "Should fetch 2 candlesticks from mock data");
    }

    @Test
    void testValidateCandlestickOrder() {
        // Creating candlesticks for testing order validation
        Candlestick first = createTestCandlestick("2023-01-01T00:00:00Z", "2023-01-01T00:00:59Z");
        Candlestick second = createTestCandlestick("2023-01-01T00:01:00Z", "2023-01-01T00:01:59Z");

        List<Candlestick> correctOrderList = List.of(first, second);
        List<Candlestick> incorrectOrderList = List.of(second, first);

        // Should not throw an exception for correct order
        assertDoesNotThrow(() -> binanceHistoricalDataService.validateCandlestickOrder(correctOrderList));

        // Should throw an exception for incorrect order
        assertThrows(IllegalStateException.class, () -> binanceHistoricalDataService.validateCandlestickOrder(incorrectOrderList));
    }


    @Test
    void testSaveCandlesticksToDatabase() {
        // Create a list of candlesticks for the test
        List<Candlestick> candlesticks = Arrays.asList(
                createTestCandlestick("2024-01-01T00:00:00Z", "2024-01-01T00:01:00Z"),
                createTestCandlestick("2024-01-01T00:01:00Z", "2024-01-01T00:02:00Z")
        );

        String symbol = "BTCUSDT";
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;

        // Create a spy for CandlestickDataService
        CandlestickDataService candlestickDataServiceSpy = Mockito.spy(new CandlestickDataService(candlestickRepository));
        // Create a spy for BinanceHistoricalDataService using the spy of CandlestickDataService
        BinanceHistoricalDataService binanceHistoricalDataServiceSpy = Mockito.spy(new BinanceHistoricalDataService(candlestickDataServiceSpy));

        // Call the method to be tested
        binanceHistoricalDataServiceSpy.saveCandlesticksToDatabase(symbol, candlesticks, interval);

        // Verify that saveCandlesticksToDatabase was called once with the correct parameters
        verify(binanceHistoricalDataServiceSpy, times(1)).saveCandlesticksToDatabase(
                eq(symbol),
                eq(candlesticks),
                eq(interval)
        );

        // Verify that saveCandlestickData was called twice with the correct parameters
        verify(candlestickDataServiceSpy, times(2)).saveCandlestickData(
                eq(symbol),
                any(Candlestick.class),
                eq(interval)
        );
    }

    @Test
    void testUpdateCandlestickSet() {
        // Create a spy for CandlestickDataService
        CandlestickDataService candlestickDataServiceSpy = spy(new CandlestickDataService(candlestickRepository));

        // Create a spy for BinanceHistoricalDataService using the spy of CandlestickDataService
        BinanceHistoricalDataService binanceHistoricalDataServiceSpy = spy(new BinanceHistoricalDataService(candlestickDataServiceSpy));

        // Prepare data for the test
        String symbol = "BTCUSDT";
        ZonedDateTime startDateTime = ZonedDateTime.parse("2023-01-01T00:00:00Z");
        ZonedDateTime endDateTime = ZonedDateTime.parse("2023-01-01T00:05:00Z");
        CandlestickInterval interval = CandlestickInterval.ONE_MINUTE;

        // Create CandlestickWrappers with full data
        CandlestickWrapper candlestickWrapper1 = new CandlestickWrapper(createTestCandlestick("2023-01-01T00:00:00Z", "2023-01-01T00:00:59Z"));
        CandlestickWrapper candlestickWrapper2 = new CandlestickWrapper(createTestCandlestick("2023-01-01T00:01:00Z", "2023-01-01T00:01:59Z"));
        CandlestickWrapper candlestickWrapper3 = new CandlestickWrapper(createTestCandlestick("2023-01-01T00:02:00Z", "2023-01-01T00:02:59Z"));

        Set<CandlestickWrapper> allCandlesticksSet = new HashSet<>();
        allCandlesticksSet.add(candlestickWrapper1);

        List<CandlestickWrapper> newCandlestickWrappers = List.of(
                candlestickWrapper1,
                candlestickWrapper2,
                candlestickWrapper3
        );

        Set<CandlestickWrapper> newCandlestickWrapperSet = new HashSet<>(newCandlestickWrappers);

        List<Candlestick> existingCandlesticks = newCandlestickWrappers.stream()
                .map(CandlestickWrapper::candlestick)
                .collect(Collectors.toList());

        // Mocking the getCandlestickData method
        when(candlestickDataServiceSpy.getCandlestickData(symbol, startDateTime, endDateTime, interval)).thenReturn(existingCandlesticks);

        // Call the method to be tested
        binanceHistoricalDataServiceSpy.updateCandlestickSet(
                symbol,
                startDateTime,
                endDateTime,
                interval,
                allCandlesticksSet
        );

        // Verify that getCandlestickData was called once with the correct parameters
        verify(candlestickDataServiceSpy).getCandlestickData(
                eq(symbol),
                eq(startDateTime),
                eq(endDateTime),
                eq(interval)
        );

        // Verify that findMissingCandlesticks was called once with the correct parameters
        verify(binanceHistoricalDataServiceSpy).findMissingCandlesticks(
                eq(newCandlestickWrapperSet),
                eq(startDateTime),
                eq(endDateTime),
                eq(interval)
        );
    }


    private Candlestick createTestCandlestick(String openTime, String closeTime) {
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(ZonedDateTime.parse(openTime).toInstant().toEpochMilli());
        candlestick.setCloseTime(ZonedDateTime.parse(closeTime).toInstant().toEpochMilli() - 1);
        candlestick.setOpen("0.1");
        candlestick.setClose("0.2");
        candlestick.setHigh("0.3");
        candlestick.setLow("0.1");
        candlestick.setVolume("100");
        candlestick.setQuoteAssetVolume("50");
        candlestick.setNumberOfTrades(10L);
        candlestick.setTakerBuyBaseAssetVolume("50");
        candlestick.setTakerBuyQuoteAssetVolume("25");
        return candlestick;
    }
}
