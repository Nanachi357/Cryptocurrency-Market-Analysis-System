package com.example.MarkPriceController.repository;

import com.example.MarkPriceController.model.CandlestickEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CandlestickRepository extends JpaRepository<CandlestickEntity, Long> {
    List<CandlestickEntity> findBySymbolAndOpenTimeBetweenAndInterval(String symbol, LocalDateTime startTime, LocalDateTime endTime, String interval);

    boolean existsBySymbolAndOpenTimeBetween(String symbol, LocalDateTime openTime, LocalDateTime endTime);
    @Query("SELECT COUNT(c) > 0 FROM CandlestickEntity c WHERE c.symbol = :symbol AND c.openTime = :openTime AND c.closeTime = :closeTime AND c.interval = :interval")
    boolean existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
            @Param("symbol") String symbol,
            @Param("openTime") LocalDateTime openTime,
            @Param("closeTime") LocalDateTime closeTime,
            @Param("interval") String interval);
}