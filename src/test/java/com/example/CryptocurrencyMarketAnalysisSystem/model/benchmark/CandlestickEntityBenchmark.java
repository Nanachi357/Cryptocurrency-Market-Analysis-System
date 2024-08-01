package com.example.CryptocurrencyMarketAnalysisSystem.model.benchmark;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.model.CandlestickEntity;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark class for measuring the performance of creating CandlestickEntity instances.
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@Fork(1)
@State(Scope.Benchmark)
public class CandlestickEntityBenchmark {

    private Candlestick candlestick;
    private String symbol;
    private CandlestickInterval interval;

    /**
     * Setup method that initializes the mock Candlestick object and other variables
     * before the benchmark tests run.
     */
    @Setup(Level.Trial)
    public void setUp() {
        // Initializing a mock Candlestick object with sample data
        candlestick = new Candlestick();
        candlestick.setOpenTime(1627845600000L);
        candlestick.setCloseTime(1627849200000L);
        candlestick.setOpen("1.0");
        candlestick.setClose("2.0");
        candlestick.setHigh("3.0");
        candlestick.setLow("0.5");
        candlestick.setVolume("1000");
        candlestick.setQuoteAssetVolume("2000");
        candlestick.setNumberOfTrades(500L);
        candlestick.setTakerBuyBaseAssetVolume("700");
        candlestick.setTakerBuyQuoteAssetVolume("1400");

        symbol = "BTCUSDT";
        interval = CandlestickInterval.ONE_MINUTE;
    }

    /**
     * Benchmark method to measure the time taken to create a CandlestickEntity instance
     * from the mock Candlestick data.
     *
     * @return A CandlestickEntity instance created from the mock Candlestick.
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public CandlestickEntity benchmarkCandlestickEntityCreation() {
        return CandlestickEntity.fromCandlestick(candlestick, symbol, interval);
    }
}
