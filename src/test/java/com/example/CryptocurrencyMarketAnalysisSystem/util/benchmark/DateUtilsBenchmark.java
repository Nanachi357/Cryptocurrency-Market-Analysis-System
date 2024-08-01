package com.example.CryptocurrencyMarketAnalysisSystem.util.benchmark;

import com.example.CryptocurrencyMarketAnalysisSystem.util.DateUtils;
import org.openjdk.jmh.annotations.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@Fork(1)
public class DateUtilsBenchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {
        public String[] dates = new String[1000];
        public long[] millis = new long[1000];
        public LocalDateTime[] localDateTimes = new LocalDateTime[1000];
        public String[] dateParts = new String[1000];
        public String[] timeParts = new String[1000];

        @Setup(Level.Trial)
        public void setUp() {
            for (int i = 0; i < 1000; i++) {
                dates[i] = "2024-07-08T12:34:56." + String.format("%03d", i % 1000);
                millis[i] = System.currentTimeMillis() + i;
                localDateTimes[i] = LocalDateTime.now().plusDays(i);
                dateParts[i] = "2024-07-" + String.format("%02d", (i % 30) + 1);
                timeParts[i] = String.format("%02d:%02d:%02d", (i % 24), (i % 60), (i % 60));
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testConvertDateToMillis(BenchmarkState state) {
        for (String date : state.dates) {
            DateUtils.convertDateToMillis(date, ZoneId.of("UTC"));
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testConvertMillisToLocalDateTime(BenchmarkState state) {
        for (long millis : state.millis) {
            DateUtils.convertMillisToLocalDateTime(millis);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testConvertMillisToDate(BenchmarkState state) {
        for (long millis : state.millis) {
            DateUtils.convertMillisToDate(millis);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testConvertToTimestamp(BenchmarkState state) {
        for (int i = 0; i < state.dateParts.length; i++) {
            DateUtils.convertToTimestamp(state.dateParts[i], state.timeParts[i]);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testConvertMillisToUtcLocalDateTime(BenchmarkState state) {
        for (long millis : state.millis) {
            DateUtils.convertMillisToUtcLocalDateTime(millis);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testConvertLocalDateTimeToMillis(BenchmarkState state) {
        for (LocalDateTime localDateTime : state.localDateTimes) {
            DateUtils.convertLocalDateTimeToMillis(localDateTime);
        }
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}