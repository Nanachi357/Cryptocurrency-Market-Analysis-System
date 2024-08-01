package com.example.CryptocurrencyMarketAnalysisSystem.util.benchmark;

import com.binance.api.client.domain.market.Candlestick;
import com.example.CryptocurrencyMarketAnalysisSystem.util.CandlestickWrapper;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@Fork(1)
public class CandlestickWrapperBenchmark {

    private CandlestickWrapper wrapper1;
    private CandlestickWrapper wrapper2;

    @Setup
    public void setUp() {
        Candlestick candlestick1 = createTestCandlestick();
        Candlestick candlestick2 = createTestCandlestick();
        wrapper1 = new CandlestickWrapper(candlestick1);
        wrapper2 = new CandlestickWrapper(candlestick2);
    }

    @Benchmark
    public boolean testEquals() {
        return wrapper1.equals(wrapper2);
    }

    @Benchmark
    public int testHashCode() {
        return wrapper1.hashCode();
    }

    @Benchmark
    public Candlestick testGetCandlestick() {
        return wrapper1.getCandlestick();
    }

    private Candlestick createTestCandlestick() {
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(1688158800000L);
        candlestick.setCloseTime(1688162399999L);
        candlestick.setOpen("1.0");
        candlestick.setClose("2.0");
        candlestick.setHigh("3.0");
        candlestick.setLow("0.5");
        candlestick.setVolume("1000");
        candlestick.setQuoteAssetVolume("2000");
        candlestick.setNumberOfTrades(500L);
        candlestick.setTakerBuyBaseAssetVolume("700");
        candlestick.setTakerBuyQuoteAssetVolume("1400");
        return candlestick;
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}


