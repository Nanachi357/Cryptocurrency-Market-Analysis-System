package com.example.CryptocurrencyMarketAnalysisSystem.repository;

import com.binance.api.client.domain.market.CandlestickInterval;
import com.example.CryptocurrencyMarketAnalysisSystem.model.CandlestickEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface CandlestickRepository extends JpaRepository<CandlestickEntity, Long> {
    @Query("SELECT c FROM CandlestickEntity c WHERE c.symbol = :symbol AND c.openTime BETWEEN :startTime AND :endTime AND c.candlestickInterval = :interval")
    List<CandlestickEntity> findBySymbolAndOpenTimeBetweenAndInterval(
            @Param("symbol") String symbol,
            @Param("startTime") @NotNull ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime,
            @Param("interval") CandlestickInterval interval);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM CandlestickEntity c WHERE c.symbol = :symbol AND c.openTime = :openTime AND c.closeTime = :closeTime AND c.candlestickInterval = :interval")
    boolean existsBySymbolAndOpenTimeAndCloseTimeAndInterval(
            @Param("symbol") String symbol,
            @Param("openTime") @NotNull ZonedDateTime openTime,
            @Param("closeTime") @NotNull ZonedDateTime closeTime,
            @Param("interval") CandlestickInterval interval);
}