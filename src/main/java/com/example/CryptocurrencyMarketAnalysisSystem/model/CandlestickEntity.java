package com.example.CryptocurrencyMarketAnalysisSystem.model;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import jakarta.persistence.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Entity
@Table(name = "candlestick_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"symbol", "open_time", "close_time", "open", "close", "high", "low", "volume", "quote_asset_volume", "number_of_trades", "taker_buy_base_asset_volume", "taker_buy_quote_asset_volume", "candlestick_interval"})
}, indexes = {
        @Index(name = "idx_symbol_open_time_close_time_interval", columnList = "symbol, open_time, close_time, candlestick_interval")
})
public class CandlestickEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "symbol")
    private final String symbol;

    @NotNull
    @Column(name = "open_time")
    private final ZonedDateTime openTime;

    @NotNull
    @Column(name = "close_time")
    private final ZonedDateTime closeTime;

    @NotNull
    @Column(name = "open")
    private final Double open;

    @NotNull
    @Column(name = "close")
    private final Double close;

    @NotNull
    @Column(name = "high")
    private final Double high;

    @NotNull
    @Column(name = "low")
    private final Double low;

    @NotNull
    @Column(name = "volume")
    private final Double volume;

    @NotNull
    @Column(name = "quote_asset_volume")
    private final Double quoteAssetVolume;

    @NotNull
    @Column(name = "number_of_trades")
    private final Long numberOfTrades;

    @NotNull
    @Column(name = "taker_buy_base_asset_volume")
    private final Double takerBuyBaseAssetVolume;

    @NotNull
    @Column(name = "taker_buy_quote_asset_volume")
    private final Double takerBuyQuoteAssetVolume;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "candlestick_interval")
    private final CandlestickInterval candlestickInterval;


    protected CandlestickEntity() {
        this.symbol = null;
        this.openTime = null;
        this.closeTime = null;
        this.open = null;
        this.close = null;
        this.high = null;
        this.low = null;
        this.volume = null;
        this.quoteAssetVolume = null;
        this.numberOfTrades = null;
        this.takerBuyBaseAssetVolume = null;
        this.takerBuyQuoteAssetVolume = null;
        this.candlestickInterval = null;
    }
    CandlestickEntity(@NotNull String symbol, @NotNull ZonedDateTime  openTime,
                      @NotNull ZonedDateTime closeTime, @NotNull Double open,
                      @NotNull Double close, @NotNull Double high,
                      @NotNull Double low, @NotNull Double volume,
                      @NotNull Double quoteAssetVolume, @NotNull Long numberOfTrades,
                      @NotNull Double takerBuyBaseAssetVolume, @NotNull Double takerBuyQuoteAssetVolume,
                      @NotNull CandlestickInterval   candlestickInterval) {
        if (symbol == null || openTime == null || closeTime == null || open == null || close == null ||
                high == null || low == null || volume == null || quoteAssetVolume == null ||
                numberOfTrades == null || takerBuyBaseAssetVolume == null ||
                takerBuyQuoteAssetVolume == null || candlestickInterval == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }

        this.symbol = symbol;
        this.openTime = openTime.withZoneSameInstant(ZoneOffset.UTC);
        this.closeTime = closeTime.withZoneSameInstant(ZoneOffset.UTC);
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.quoteAssetVolume = quoteAssetVolume;
        this.numberOfTrades = numberOfTrades;
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
        this.candlestickInterval = candlestickInterval;

        // Perform validation
        validate();
    }

    private void validate() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<CandlestickEntity>> violations = validator.validate(this);
            if (!violations.isEmpty()) {
                throw new IllegalArgumentException(violations.iterator().next().getMessage());
            }
        }
    }

    @Override
    public String toString() {
        return "CandlestickEntity{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", openTime=" + openTime +
                ", closeTime=" + closeTime +
                ", open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", quoteAssetVolume=" + quoteAssetVolume +
                ", numberOfTrades=" + numberOfTrades +
                ", takerBuyBaseAssetVolume=" + takerBuyBaseAssetVolume +
                ", takerBuyQuoteAssetVolume=" + takerBuyQuoteAssetVolume +
                ", interval='" + candlestickInterval + '\'' +
                '}';
    }

    public static CandlestickEntity fromCandlestick(Candlestick candlestick, String symbol, CandlestickInterval  interval) {
        if (candlestick == null || symbol == null || interval == null) {
            throw new IllegalArgumentException("Parameters candlestick, symbol, and interval must not be null");
        }

        if (candlestick.getOpenTime() == null || candlestick.getCloseTime() == null) {
            throw new IllegalArgumentException("Candlestick times must not be null");
        }

        ZonedDateTime openTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(candlestick.getOpenTime()), ZoneOffset.UTC);
        ZonedDateTime closeTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(candlestick.getCloseTime()), ZoneOffset.UTC);

        CandlestickEntity entity = new CandlestickEntity(
                symbol,
                openTime,
                closeTime,
                Double.parseDouble(candlestick.getOpen()),
                Double.parseDouble(candlestick.getClose()),
                Double.parseDouble(candlestick.getHigh()),
                Double.parseDouble(candlestick.getLow()),
                Double.parseDouble(candlestick.getVolume()),
                Double.parseDouble(candlestick.getQuoteAssetVolume()),
                candlestick.getNumberOfTrades(),
                Double.parseDouble(candlestick.getTakerBuyBaseAssetVolume()),
                Double.parseDouble(candlestick.getTakerBuyQuoteAssetVolume()),
                interval
        );

        // Perform validation
        entity.validate();

        return entity;
    }
}