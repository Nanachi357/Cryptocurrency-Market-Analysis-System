package com.example.CryptocurrencyMarketAnalysisSystem.model.benchmark;

import com.example.CryptocurrencyMarketAnalysisSystem.model.RSIData;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark class for measuring the performance of creating RSIData instances.
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@Fork(1)
@State(Scope.Benchmark)
public class RSIDataBenchmark {

    private static List<String> dates;
    private static List<Double> rsiValues;

    /**
     * Setup method to initialize the lists of dates and RSI values with sample data
     * before the benchmark tests run.
     */
    @Setup(Level.Trial)
    public void setup() {
        dates = new ArrayList<>();
        rsiValues = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            dates.add("2024-07-08");
            rsiValues.add((double) (Math.random() * 100));
        }
    }

    /**
     * Benchmark method to measure the time taken to create an RSIData instance
     * from the sample lists of dates and RSI values.
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkRSIDataCreation() {
        new RSIData(dates, rsiValues);
    }

    /**
     * Main method to run the benchmark tests using JMH.
     *
     * @param args Command-line arguments for the benchmark runner.
     * @throws Exception if any exception occurs during benchmark execution.
     */
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
