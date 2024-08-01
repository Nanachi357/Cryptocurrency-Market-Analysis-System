package com.example.CryptocurrencyMarketAnalysisSystem.repository.benchmark;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.CryptocurrencyMarketAnalysisSystemApplication;
import com.example.CryptocurrencyMarketAnalysisSystem.model.CandlestickEntity;
import com.example.CryptocurrencyMarketAnalysisSystem.repository.CandlestickRepository;
import com.example.CryptocurrencyMarketAnalysisSystem.util.DateUtils;
import org.openjdk.jmh.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = {CryptocurrencyMarketAnalysisSystemApplication.class})
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@Fork(1)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CandlestickRepositoryBenchmark {

    @Autowired
    private CandlestickRepository repository;

    private CandlestickEntity candlestickEntity;

    @Setup(Level.Trial)
    public void setUp() {
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(DateUtils.convertDateToMillis("2023-07-01T00:00:00.000", ZoneId.of("UTC")));
        candlestick.setCloseTime(DateUtils.convertDateToMillis("2023-07-01T00:59:59.999", ZoneId.of("UTC")));
        candlestick.setOpen("1.0");
        candlestick.setClose("2.0");
        candlestick.setHigh("3.0");
        candlestick.setLow("0.5");
        candlestick.setVolume("1000");
        candlestick.setQuoteAssetVolume("2000");
        candlestick.setNumberOfTrades(500L);
        candlestick.setTakerBuyBaseAssetVolume("700");
        candlestick.setTakerBuyQuoteAssetVolume("1400");

        candlestickEntity = CandlestickEntity.fromCandlestick(candlestick, "BTCUSDT", CandlestickInterval.ONE_MINUTE);
        repository.saveAndFlush(candlestickEntity);
    }

    @Benchmark
    public List<CandlestickEntity> benchmarkFindBySymbolAndOpenTimeBetweenAndInterval() {
        return repository.findBySymbolAndOpenTimeBetweenAndInterval(
                "BTCUSDT",
                ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2023, 7, 1, 0, 0, 59, 999_000_000, ZoneId.of("UTC")),
                CandlestickInterval.ONE_MINUTE
        );
    }

    @Benchmark
    public boolean benchmarkExistsBySymbolAndOpenTimeAndCloseTimeAndInterval() {
        return repository.existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
                "BTCUSDT",
                candlestickEntity.getOpenTime(),
                candlestickEntity.getCloseTime(),
                CandlestickInterval.ONE_MINUTE
        );
    }
}