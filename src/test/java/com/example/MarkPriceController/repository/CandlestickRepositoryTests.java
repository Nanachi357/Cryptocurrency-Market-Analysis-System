package com.example.MarkPriceController.repository;

import com.example.MarkPriceController.model.CandlestickEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CandlestickRepositoryTests {

    @Autowired
    private CandlestickRepository candlestickRepository;

    @BeforeEach
    void setUp() {
        // Очищення бази даних перед кожним тестом
        candlestickRepository.deleteAll();
    }

    @Test
    public void testExistsBySymbolAndOpenTimeBetween() {
        // Створити тестовий запис у базі даних
        CandlestickEntity candlestick = new CandlestickEntity();
        candlestick.setSymbol("BTCUSDT");
        candlestick.setOpenTime(LocalDateTime.of(2024, 5, 27, 0, 0));
        candlestick.setCloseTime(LocalDateTime.of(2024, 5, 27, 0, 1));
        candlestick.setOpen(10000.0);
        candlestick.setClose(10100.0);
        candlestick.setHigh(10150.0);
        candlestick.setLow(9950.0);
        candlestick.setVolume(150.0);
        candlestick.setQuoteAssetVolume(1500000.0);
        candlestick.setNumberOfTrades(100L);
        candlestick.setTakerBuyBaseAssetVolume(75.0);
        candlestick.setTakerBuyQuoteAssetVolume(750000.0);

        candlestickRepository.save(candlestick);

        // Перевірка існування запису в базі даних
        boolean exists = candlestickRepository.existsBySymbolAndOpenTimeBetween("BTCUSDT", LocalDateTime.of(2024, 5, 27, 0, 0), LocalDateTime.of(2024, 5, 27, 0, 1));
        assertTrue(exists);
    }
}